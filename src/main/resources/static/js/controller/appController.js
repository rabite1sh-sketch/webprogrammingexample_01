import { AppModel, TMI_LIST } from '../model/appModel.js';
import { AppView } from '../view/appView.js';

class AppController {
    constructor(model, view) {
        this.model = model;
        this.view = view;
    }

    async init() {
        this.bindEvents();
        this.view.renderAuth(this.model.state.currentUser);
        await this.refreshAll();
        this.view.showRandomTMI(TMI_LIST);
    }

    bindEvents() {
        const { el } = this.view;

        el.loginForm.addEventListener('submit', (event) => this.handleLogin(event));
        el.logoutBtn.addEventListener('click', () => this.handleLogout());
        el.withdrawBtn.addEventListener('click', () => this.handleWithdraw());

        el.toggleSignupBtn.addEventListener('click', () => el.signupForm.classList.toggle('hidden'));
        el.sendVerifyBtn.addEventListener('click', () => this.handleSendVerification());
        el.verifyCodeBtn.addEventListener('click', () => this.handleVerifyCode());
        el.signupForm.addEventListener('submit', (event) => this.handleSignup(event));

        el.addItemBtn.addEventListener('click', () => this.handleCreateItem());
        el.itemList.addEventListener('click', (event) => this.handleItemAction(event));
        el.itemList.addEventListener('error', (event) => {
            if (event.target.matches('img.item-image')) {
                event.target.src = 'https://dummyimage.com/80x80/cccccc/000000.png&text=No+Image';
            }
        }, true);

        el.commentForm.addEventListener('submit', (event) => this.handleAddComment(event));
        el.commentList.addEventListener('click', (event) => this.handleDeleteComment(event));
        el.nextTmiBtn.addEventListener('click', () => this.view.showRandomTMI(TMI_LIST));
    }

    async refreshAll() {
        const [items, comments, totalAmount] = await Promise.all([
            this.model.fetchItems(),
            this.model.fetchComments(),
            this.model.fetchDonation()
        ]);

        this.view.renderItems(items, this.model.state.currentUser);
        this.view.renderComments(comments, this.model.state.currentUser);
        this.view.updateDonation(totalAmount);
    }

    async handleLogin(event) {
        event.preventDefault();
        const id = document.getElementById('userid').value.trim();
        const password = document.getElementById('userpw').value.trim();

        try {
            const data = await this.model.login(id, password);
            if (!data.success) throw new Error(data.message || '로그인 실패');
            this.model.saveLogin(data.token, data.user);
            this.view.renderAuth(this.model.state.currentUser);
            await this.refreshAll();
            alert('로그인 성공!');
        } catch (error) {
            alert(error.message);
        }
    }

    handleLogout() {
        this.model.logout();
        this.view.renderAuth(null);
        this.refreshAll();
    }

    async handleWithdraw() {
        if (!confirm('정말로 탈퇴하시겠습니까?')) return;

        try {
            const result = await this.model.withdrawAccount();
            if (!result.success) throw new Error(result.message);
            alert(result.message);
            this.handleLogout();
        } catch (error) {
            alert(error.message);
        }
    }

    async handleSendVerification() {
        const email = document.getElementById('reg-email').value.trim();
        if (!email) return alert('이메일을 입력해 주세요.');

        try {
            const data = await this.model.sendVerification(email);
            if (!data.success) throw new Error(data.message || '인증번호 발송 실패');
            this.model.state.verifiedEmail = email;
            this.view.toggle(this.view.el.verifyArea, true);
            alert(data.message);
        } catch (error) {
            alert(error.message);
        }
    }

    async handleVerifyCode() {
        const code = document.getElementById('reg-code').value.trim();

        try {
            const data = await this.model.verifyCode(this.model.state.verifiedEmail, code);
            if (!data.success) throw new Error(data.message || '인증 실패');
            this.model.state.isEmailVerified = true;
            this.view.toggle(this.view.el.signupDetails, true);
            alert('이메일 인증 완료!');
        } catch (error) {
            alert(error.message);
        }
    }

    async handleSignup(event) {
        event.preventDefault();
        if (!this.model.state.isEmailVerified) return alert('이메일 인증을 먼저 진행해 주세요.');

        const payload = {
            id: document.getElementById('reg-id').value.trim(),
            password: document.getElementById('reg-pw').value.trim(),
            name: document.getElementById('reg-name').value.trim(),
            email: this.model.state.verifiedEmail
        };

        try {
            const data = await this.model.signup(payload);
            if (!data.success) throw new Error(data.message || '가입 실패');
            alert(data.message);
            this.view.el.signupForm.reset();
            this.view.el.signupForm.classList.add('hidden');
            this.view.toggle(this.view.el.verifyArea, false);
            this.view.toggle(this.view.el.signupDetails, false);
            this.model.state.isEmailVerified = false;
        } catch (error) {
            alert(error.message);
        }
    }

    async handleCreateItem() {
        const name = prompt('추가할 기부 품목 이름');
        const price = Number(prompt('가격(숫자)'));
        const imageUrl = prompt('이미지 파일명', 'default.jpg') || 'default.jpg';
        if (!name || Number.isNaN(price)) return alert('올바른 값을 입력해 주세요.');

        try {
            await this.model.createItem({ name, price, imageUrl });
            await this.refreshAll();
        } catch (error) {
            alert(error.message);
        }
    }

    async handleItemAction(event) {
        const action = event.target.dataset.action;
        if (!action) return;

        const card = event.target.closest('.item-card');
        const itemId = Number(card?.dataset.itemId);
        const item = this.model.state.items.find((row) => row.id === itemId);
        if (!item) return;

        if (action === 'donate') return this.handleDonate(item.price);
        if (action === 'edit') {
            const name = prompt('새 품목 이름', item.name);
            const price = Number(prompt('새 가격', item.price));
            if (!name || Number.isNaN(price)) return;
            await this.model.updateItem(item.id, { name, price });
            return this.refreshAll();
        }
        if (action === 'delete') {
            if (!confirm('정말 삭제할까요?')) return;
            await this.model.deleteItem(item.id);
            return this.refreshAll();
        }
    }

    async handleDonate(amount) {
        const user = this.model.state.currentUser;
        if (!user) return alert('로그인 후 이용해 주세요.');

        try {
            if (window.PortOne?.requestPayment) {
                const response = await window.PortOne.requestPayment({
                    storeId: 'store-868eb069-58f8-4e2c-9b85-dd456e7d5bf2',
                    channelKey: 'channel-key-76312e47-57bd-4548-8f5b-af34a0988ac8',
                    paymentId: `order_${Date.now()}`,
                    orderName: '10cm 팬페이지 기부',
                    totalAmount: amount,
                    currency: 'KRW',
                    payMethod: 'EASY_PAY'
                });
                if (response.code != null) throw new Error(response.message || '결제가 취소되었습니다.');
            }

            await this.model.saveDonation(amount);
            await this.refreshAll();
            alert('기부가 완료되었습니다. 감사합니다!');
        } catch (error) {
            alert(error.message);
        }
    }

    async handleAddComment(event) {
        event.preventDefault();
        const text = this.view.el.commentInput.value.trim();
        if (!text) return;

        try {
            await this.model.addComment(text);
            this.view.el.commentInput.value = '';
            await this.refreshAll();
        } catch (error) {
            alert(error.message);
        }
    }

    async handleDeleteComment(event) {
        if (event.target.dataset.action !== 'delete-comment') return;
        const commentId = Number(event.target.closest('.comment-box')?.dataset.commentId);
        if (!commentId) return;
        if (!confirm('댓글을 삭제할까요?')) return;

        try {
            await this.model.deleteComment(commentId);
            await this.refreshAll();
        } catch (error) {
            alert(error.message);
        }
    }
}

const controller = new AppController(new AppModel(), new AppView());
controller.init();
