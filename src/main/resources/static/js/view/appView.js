export class AppView {
    constructor() {
        this.el = {
            loginForm: document.getElementById('login-form'),
            signupForm: document.getElementById('signup-form'),
            toggleSignupBtn: document.getElementById('toggle-signup-btn'),
            sendVerifyBtn: document.getElementById('send-verify-btn'),
            verifyCodeBtn: document.getElementById('verify-code-btn'),
            verifyArea: document.getElementById('verify-area'),
            signupDetails: document.getElementById('signup-details'),
            userProfile: document.getElementById('user-profile'),
            welcomeName: document.getElementById('welcome-name'),
            logoutBtn: document.getElementById('logout-btn'),
            withdrawBtn: document.getElementById('withdraw-btn'),
            itemList: document.getElementById('item-list'),
            addItemBtn: document.getElementById('add-item-btn'),
            totalAmount: document.getElementById('total-amount'),
            commentForm: document.getElementById('comment-form'),
            loginPrompt: document.getElementById('login-prompt'),
            writerName: document.getElementById('writer-name'),
            commentInput: document.getElementById('msg'),
            commentList: document.getElementById('comment-list'),
            tmiText: document.getElementById('tmi-text'),
            nextTmiBtn: document.getElementById('next-tmi-btn'),
            adminExcelArea: document.getElementById('admin-excel-area')
        };
    }

    toggle(el, visible) {
        el.classList.toggle('hidden', !visible);
    }

    renderAuth(user) {
        const isLoggedIn = Boolean(user);
        this.toggle(this.el.loginForm, !isLoggedIn);
        this.toggle(this.el.userProfile, isLoggedIn);
        this.toggle(this.el.commentForm, isLoggedIn);
        this.toggle(this.el.loginPrompt, !isLoggedIn);

        if (!isLoggedIn) {
            this.el.writerName.textContent = '손님';
            this.el.commentInput.value = '';
            return;
        }

        this.el.welcomeName.textContent = user.name;
        this.el.writerName.textContent = user.name;
        const isAdmin = user.id === 'admin';
        this.toggle(this.el.addItemBtn, isAdmin);
        this.toggle(this.el.adminExcelArea, isAdmin);
    }

    renderItems(items, currentUser) {
        this.el.itemList.innerHTML = '';
        const isAdmin = currentUser?.id === 'admin';

        items.forEach((item) => {
            const detailUrl = `/item/${item.id}`;
            const detailLink = `<a href="${detailUrl}"><h3>${item.title}</h3></a>`;

            const card = document.createElement('article');
            card.className = 'item-card';
            card.dataset.itemId = item.id;
            card.innerHTML = `
                ${detailLink}
                <p>${item.content || ""}</p>
                <p><strong>${item.donationTarget || "기부 단체 미정"}</strong>에 기부</p>
                <p>${Number(item.price).toLocaleString()}원</p>
                <button class="btn btn-purple" data-action="donate">기부하기</button>
                ${isAdmin ? `<div class="card-actions">
                    <button class="btn btn-orange" data-action="edit">수정</button>
                    <button class="btn btn-red" data-action="delete">삭제</button>
                </div>` : ''}
            `;
            this.el.itemList.append(card);
        });
    }

    renderComments(comments, currentUser) {
        this.el.commentList.innerHTML = '';
        comments.forEach((comment) => {
            const canDelete = currentUser && (currentUser.id === 'admin' || currentUser.id === comment.userId);
            const box = document.createElement('article');
            box.className = 'comment-box';
            box.dataset.commentId = comment.id;
            box.innerHTML = `
                <div class="comment-header">
                    <span class="comment-name">${comment.name}</span>
                    <span class="comment-time">${comment.time || ''}</span>
                </div>
                <div>${comment.text}</div>
                ${canDelete ? '<button class="btn btn-red" data-action="delete-comment" type="button">삭제</button>' : ''}
            `;
            this.el.commentList.append(box);
        });
    }

    updateDonation(totalAmount) {
        this.el.totalAmount.textContent = Number(totalAmount).toLocaleString();
    }

    showRandomTMI(list) {
        const randomIndex = Math.floor(Math.random() * list.length);
        this.el.tmiText.textContent = list[randomIndex];
    }
}
