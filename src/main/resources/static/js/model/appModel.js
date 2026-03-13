const SERVER_URL = 'http://localhost:8080';
const USER_KEY = 'fanpage_user';
const TOKEN_KEY = 'fanpage_token';

export class AppModel {
    constructor() {
        this.state = {
            currentUser: this.loadUser(),
            isEmailVerified: false,
            verifiedEmail: '',
            items: [],
            comments: [],
            totalAmount: 0
        };
    }

    loadUser() {
        const raw = localStorage.getItem(USER_KEY);
        return raw ? JSON.parse(raw) : null;
    }

    saveLogin(token, user) {
        localStorage.setItem(TOKEN_KEY, token);
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        this.state.currentUser = user;
    }

    logout() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        this.state.currentUser = null;
    }

    token() {
        return localStorage.getItem(TOKEN_KEY);
    }

    async request(path, options = {}) {
        const response = await fetch(`${SERVER_URL}${path}`, options);
        const contentType = response.headers.get('content-type') || '';
        const data = contentType.includes('application/json') ? await response.json() : await response.text();
        if (!response.ok) throw new Error(data.message || data || '요청에 실패했습니다.');
        return data;
    }

    async fetchItems() {
        this.state.items = await this.request('/items');
        return this.state.items;
    }

    async createItem(payload) {
        return this.request('/items', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
    }

    async updateItem(id, payload) {
        return this.request(`/items/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
    }

    async deleteItem(id) {
        return this.request(`/items/${id}`, { method: 'DELETE' });
    }

    async fetchComments() {
        this.state.comments = await this.request('/comments');
        return this.state.comments;
    }

    async addComment(text) {
        const now = new Date().toLocaleString('ko-KR');
        const user = this.state.currentUser;
        return this.request('/comments', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: user.name, text, time: now, userId: user.id })
        });
    }

    async deleteComment(id) {
        const user = this.state.currentUser;
        return this.request(`/comments/${id}?user_id=${encodeURIComponent(user.id)}`, {
            method: 'DELETE'
        });
    }

    async fetchDonation() {
        const data = await this.request('/donation');
        this.state.totalAmount = data.total_amount || 0;
        return this.state.totalAmount;
    }

    async saveDonation(amount) {
        const user = this.state.currentUser;
        return this.request('/donate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${this.token()}`
            },
            body: JSON.stringify({ amount, user_id: user.id })
        });
    }

    async login(id, password) {
        return this.request('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id, password })
        });
    }

    async signup({ id, password, name, email }) {
        return this.request('/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id, password, name, email })
        });
    }

    async sendVerification(email) {
        return this.request('/send-verification', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });
    }

    async verifyCode(email, code) {
        return this.request('/verify-code', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, code })
        });
    }

    async withdrawAccount() {
        const user = this.state.currentUser;
        return this.request(`/users/${user.id}`, {
            method: 'DELETE',
            headers: { Authorization: `Bearer ${this.token()}` }
        });
    }
}

export const TMI_LIST = [
    '10cm의 권정열은 감성적인 가사와 독보적인 음색으로 큰 사랑을 받고 있어요.',
    '대표곡 "아메리카노"는 발매 후 오랜 시간 사랑받는 스테디셀러입니다.',
    '10cm는 인디씬에서 시작해 대중적 인지도까지 넓힌 보기 드문 팀입니다.',
    '공연에서의 재치 있는 멘트도 10cm의 매력 포인트입니다.'
];
