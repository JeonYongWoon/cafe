let currentUser = null;
let cart = [];
let selectedMenu = null;

const API_HOST = window.location.origin;

window.addEventListener('DOMContentLoaded', () => {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        updateUserUI();
    }
    loadMenus();
    loadPopularMenus();
    showPage('main');
});

function showPage(pageId) {
    if ((pageId === 'cart' || pageId === 'point') && !currentUser) {
        showToast('로그인이 필요한 서비스입니다.', 'error');
        showPage('login');
        return;
    }

    if (pageId === 'admin') {
        if (!currentUser) {
            showToast('로그인이 필요한 서비스입니다.', 'error');
            showPage('login');
            return;
        }
        if (currentUser.role !== 'ADMIN') {
            showToast('관리자만 접근할 수 있습니다.', 'error');
            showPage('main');
            return;
        }
    }

    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
    });
    const targetPage = document.getElementById(`page-${pageId}`);
    if (targetPage) {
        targetPage.classList.add('active');
    }

    document.querySelectorAll('nav button').forEach(btn => {
        btn.classList.remove('active');
    });
    
    if (pageId === 'point' && currentUser) {
        fetchMemberPoint();
    }
}

const MENU_IMAGE_MAP = {
    '아메리카노': 'https://images.unsplash.com/photo-1507133750040-4a8f57021571?auto=format&fit=crop&q=80&w=600',
    '카페라떼': 'https://images.unsplash.com/photo-1541167760496-1628856ab772?auto=format&fit=crop&q=80&w=600',
    '돌체라떼': 'https://images.unsplash.com/photo-1517701604599-bb29b565090c?auto=format&fit=crop&q=80&w=600',
    '카라멜마키아토': 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?auto=format&fit=crop&q=80&w=600',
    '바닐라라떼': 'https://images.unsplash.com/photo-1595434091143-b375ced5fe5c?auto=format&fit=crop&q=80&w=600',
    '카푸치노': 'https://images.unsplash.com/photo-1534778101976-62847782c213?auto=format&fit=crop&q=80&w=600',
    '에스프레소': 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&q=80&w=600',
    '자몽에이드': 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&q=80&w=600'
};

const DEFAULT_COFFEE_IMAGE = 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&q=80&w=600';

async function loadMenus() {
    try {
        const response = await fetch(`${API_HOST}/menus`);
        const json = await response.json();
        if (json.success) {
            const container = document.getElementById('menu-list');
            container.innerHTML = '';
            json.data.forEach(menu => {
                const finalImgUrl = MENU_IMAGE_MAP[menu.name] || DEFAULT_COFFEE_IMAGE;
                const card = document.createElement('div');
                card.className = 'menu-card';
                card.onclick = () => openMenuDetail(menu);
                card.innerHTML = `
                    <img src="${finalImgUrl}" alt="${menu.name}">
                    <h3>${menu.name}</h3>
                    <p class="price">${menu.price}원</p>
                    <p class="status">${menu.status === 'AVAILABLE' ? '주문 가능' : '품절'}</p>
                `;
                container.appendChild(card);
            });
        }
    } catch (err) {
        console.error(err);
    }
}

async function loadPopularMenus() {
    try {
        const response = await fetch(`${API_HOST}/menus/popular?days=7`);
        const json = await response.json();
        if (json.success) {
            const container = document.getElementById('popular-menu-list');
            container.innerHTML = '';
            json.data.forEach(menu => {
                const finalImgUrl = MENU_IMAGE_MAP[menu.name] || DEFAULT_COFFEE_IMAGE;
                const card = document.createElement('div');
                card.className = 'menu-card popular';
                card.onclick = () => openMenuDetail(menu);
                card.innerHTML = `
                    <img src="${finalImgUrl}" alt="${menu.name}">
                    <div style="background:#f1c40f;color:#fff;font-size:12px;padding:3px;border-radius:3px;margin-bottom:5px;">BEST</div>
                    <h3>${menu.name}</h3>
                    <p class="price">${menu.price}원</p>
                    <p style="font-size:13px;color:#7f8c8d;">누적 주문: ${menu.orderCount}회</p>
                `;
                container.appendChild(card);
            });
        }
    } catch (err) {
        console.error(err);
    }
}

async function handleLogin() {
    const usernameInput = document.getElementById('username').value;
    const passwordInput = document.getElementById('password').value;

    if (!usernameInput || !passwordInput) {
        showToast('아이디와 비밀번호를 모두 입력해 주세요.', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_HOST}/sessions`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: usernameInput, password: passwordInput })
        });
        const json = await response.json();
        if (json.success) {
            currentUser = json.data;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            updateUserUI();
            showToast('로그인에 성공했습니다.', 'success');
            showPage('main');
        } else {
            showToast(json.error.message || '로그인 실패', 'error');
        }
    } catch (err) {
        console.error(err);
    }
}

async function handleSignup() {
    const usernameInput = document.getElementById('username').value;
    const passwordInput = document.getElementById('password').value;

    if (!usernameInput || !passwordInput) {
        showToast('아이디와 비밀번호를 입력해 주세요.', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_HOST}/members`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: usernameInput, password: passwordInput })
        });
        const json = await response.json();
        if (json.success) {
            showToast('회원가입이 완료되었습니다. 로그인해 주십시오.', 'success');
        } else {
            showToast(json.error.message || '회원가입 실패', 'error');
        }
    } catch (err) {
        console.error(err);
    }
}

function logOut() {
    currentUser = null;
    localStorage.removeItem('currentUser');
    updateUserUI();
    showPage('main');
}

function updateUserUI() {
    const userInfoSpan = document.getElementById('user-info');
    const loginNavBtn = document.getElementById('login-nav-btn');
    const adminNavBtn = document.querySelector("button[onclick=\"showPage('admin')\"]");

    if (currentUser) {
        userInfoSpan.innerHTML = `<strong>${currentUser.username}</strong> 회원님 환영합니다.`;
        loginNavBtn.innerText = '로그아웃';
        loginNavBtn.onclick = logOut;
        if (currentUser.role === 'ADMIN') {
            if (adminNavBtn) adminNavBtn.style.display = 'inline-block';
        } else {
            if (adminNavBtn) adminNavBtn.style.display = 'none';
        }
    } else {
        userInfoSpan.innerHTML = '';
        loginNavBtn.innerText = '로그인';
        loginNavBtn.onclick = () => showPage('login');
        if (adminNavBtn) adminNavBtn.style.display = 'none';
    }
}

function openMenuDetail(menu) {
    if (menu.status === 'SOLD_OUT') {
        showToast('품절된 상품입니다.', 'error');
        return;
    }
    selectedMenu = menu;
    document.getElementById('modal-menu-name').innerText = menu.name;
    document.getElementById('modal-menu-price').innerText = `가격: ${menu.price}원`;
    document.getElementById('modal-menu-qty').value = 1;
    document.getElementById('menu-detail-modal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('menu-detail-modal').style.display = 'none';
}

function addToCart() {
    if (!currentUser) {
        showToast('장바구니 담기는 로그인이 필요합니다.', 'error');
        closeModal();
        showPage('login');
        return;
    }
    const temp = document.getElementById('modal-menu-temp').value;
    const qty = parseInt(document.getElementById('modal-menu-qty').value);

    const existing = cart.find(item => item.menuId === selectedMenu.menuId && item.temperature === temp);
    if (existing) {
        existing.quantity += qty;
    } else {
        cart.push({
            menuId: selectedMenu.menuId,
            name: selectedMenu.name,
            price: selectedMenu.price,
            temperature: temp,
            quantity: qty
        });
    }

    updateCartUI();
    closeModal();
    showConfirm('장바구니에 추가되었습니다. 장바구니 화면으로 이동하시겠습니까?', () => {
        showPage('cart');
    });
}

function updateCartUI() {
    document.getElementById('cart-count').innerText = cart.reduce((acc, curr) => acc + curr.quantity, 0);
    const container = document.getElementById('cart-items');
    container.innerHTML = '';

    let total = 0;
    cart.forEach((item, index) => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;

        const div = document.createElement('div');
        div.className = 'cart-item';
        div.style = 'background:#fff; padding:15px; border-radius:6px; margin-bottom:10px; display:flex; justify-content:space-between; align-items:center;';
        div.innerHTML = `
            <div>
                <h4>${item.name} (${item.temperature})</h4>
                <p>${item.price}원 x ${item.quantity}개 = ${itemTotal}원</p>
            </div>
            <button onclick="removeFromCart(${index})" class="btn-secondary" style="padding:5px 10px;">삭제</button>
        `;
        container.appendChild(div);
    });

    document.getElementById('cart-total-price').innerText = total;
}

function removeFromCart(index) {
    cart.splice(index, 1);
    updateCartUI();
}

async function processOrder() {
    if (!currentUser) {
        showToast('로그인이 필요한 서비스입니다.', 'error');
        showPage('login');
        return;
    }
    if (cart.length === 0) {
        showToast('장바구니가 비어 있습니다.', 'error');
        return;
    }

    const orderPayload = {
        memberId: currentUser.memberId,
        items: cart.map(item => ({
            menuId: item.menuId,
            temperature: item.temperature,
            quantity: item.quantity
        }))
    };

    try {
        const response = await fetch(`${API_HOST}/orders`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderPayload)
        });
        const json = await response.json();
        if (json.success) {
            cart = [];
            updateCartUI();
            document.getElementById('complete-order-id').innerText = json.data.orderId;
            document.getElementById('complete-total-price').innerText = json.data.totalPrice;
            showPage('complete');
        } else {
            showToast(json.error.message || '주문 처리 중 에러가 발생했습니다.', 'error');
        }
    } catch (err) {
        console.error(err);
    }
}

async function fetchMemberPoint() {
    if (!currentUser) return;
    try {
        const response = await fetch(`${API_HOST}/members/${currentUser.memberId}`);
        const json = await response.json();
        if (json.success) {
            document.getElementById('current-point').innerText = json.data.pointBalance.toLocaleString();
        }
    } catch (err) {
        console.error(err);
    }
}

async function chargePoint(amount) {
    if (!currentUser) {
        showToast('포인트 충전은 로그인이 필요합니다.', 'error');
        showPage('login');
        return;
    }

    try {
        const response = await fetch(`${API_HOST}/points/charge`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ memberId: currentUser.memberId, amount: amount })
        });
        const json = await response.json();
        if (json.success) {
            showToast(`${amount.toLocaleString()} P가 정상적으로 충전되었습니다.`, 'success');
            fetchMemberPoint();
        } else {
            showToast(json.error.message || '충전 실패', 'error');
        }
    } catch (err) {
        console.error(err);
    }
}

async function loadAdminOrders() {
    try {
        const response = await fetch(`${API_HOST}/admin/orders`);
        const json = await response.json();
        if (json.success) {
            const tbody = document.getElementById('admin-order-list');
            tbody.innerHTML = '';
            json.data.forEach(order => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${order.orderId}</td>
                    <td>${order.username}</td>
                    <td>${order.totalPrice}원</td>
                    <td><span class="status-badge status-${order.status}">${order.status}</span></td>
                    <td>${new Date(order.createdAt).toLocaleString()}</td>
                    <td>
                        <select onchange="changeOrderStatus(${order.orderId}, this.value)">
                            <option value="RECEIVED" ${order.status === 'RECEIVED' ? 'selected' : ''}>주문접수</option>
                            <option value="PREPARING" ${order.status === 'PREPARING' ? 'selected' : ''}>제조중</option>
                            <option value="READY_FOR_PICKUP" ${order.status === 'READY_FOR_PICKUP' ? 'selected' : ''}>픽업대기</option>
                            <option value="COMPLETED" ${order.status === 'COMPLETED' ? 'selected' : ''}>주문완료</option>
                        </select>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (err) {
        console.error(err);
    }
}

async function changeOrderStatus(orderId, newStatus) {
    try {
        const response = await fetch(`${API_HOST}/orders/${orderId}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        const json = await response.json();
        if (json.success) {
            showToast(`주문 ${orderId}의 상태가 변경되었습니다.`, 'success');
            loadAdminOrders();
        } else {
            showToast(json.error.message || '상태 변경 실패', 'error');
        }
    } catch (err) {
        console.error(err);
    }
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast-card ${type}`;
    toast.innerText = message;

    container.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.add('show');
    }, 10);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            toast.remove();
        }, 400);
    }, 3000);
}

let activeConfirmCallback = null;

function showConfirm(message, callback) {
    document.getElementById('confirm-message').innerText = message;
    activeConfirmCallback = callback;
    document.getElementById('confirm-modal').style.display = 'flex';
}

function closeConfirmModal() {
    document.getElementById('confirm-modal').style.display = 'none';
    activeConfirmCallback = null;
}

document.getElementById('confirm-yes-btn').addEventListener('click', () => {
    if (activeConfirmCallback) {
        activeConfirmCallback();
    }
    closeConfirmModal();
});
