// Authentication Module (Login, Register, Session & Header State)

import { state, setToken, setUser, setCart } from '../config.js';
import { apiCall, showToast } from '../api.js';

export function toggleOwnerFields(roleValue) {
    const box = document.getElementById('owner-fields-box');
    if (box) {
        box.style.display = roleValue === 'RESTAURANT_OWNER' ? 'block' : 'none';
    }
}

export function toggleAuthMode(mode) {
    const loginBox = document.getElementById('auth-login-box');
    const regBox = document.getElementById('auth-register-box');
    const btnLogin = document.getElementById('btn-toggle-login');
    const btnReg = document.getElementById('btn-toggle-register');

    if (!loginBox || !regBox) return;

    if (mode === 'login') {
        loginBox.style.display = 'block';
        regBox.style.display = 'none';
        if (btnLogin) btnLogin.classList.add('active');
        if (btnReg) btnReg.classList.remove('active');
    } else {
        loginBox.style.display = 'none';
        regBox.style.display = 'block';
        if (btnLogin) btnLogin.classList.remove('active');
        if (btnReg) btnReg.classList.add('active');
    }
}

export function fillDemo(email, password) {
    const emailInput = document.getElementById('login-email');
    const passInput = document.getElementById('login-pass');
    if (emailInput) emailInput.value = email;
    if (passInput) passInput.value = password;
}

export function updateHeaderState() {
    const pill = document.getElementById('user-info-pill');
    const authBtn = document.getElementById('nav-auth');
    const manageBtn = document.getElementById('nav-manage');
    const cartBtn = document.getElementById('nav-cart');
    const ordersBtn = document.getElementById('nav-orders');
    const cartCount = document.getElementById('cart-count');

    if (!pill || !authBtn) return;

    if (state.token && state.user) {
        const restLabel = state.user.restaurantName ? ` • ${state.user.restaurantName}` : '';
        pill.innerHTML = `<span>👤 ${state.user.name}</span> <span class="role-badge">${state.user.role}${restLabel}</span>`;
        pill.style.display = 'flex';
        authBtn.innerText = 'Logout 🚪';

        if (state.user.role === 'CUSTOMER') {
            if (cartBtn) cartBtn.style.display = 'inline-flex';
            if (manageBtn) manageBtn.style.display = 'none';
            if (ordersBtn) ordersBtn.innerText = '📦 My Orders';
        } else if (state.user.role === 'RESTAURANT_OWNER') {
            if (cartBtn) cartBtn.style.display = 'none';
            if (manageBtn) manageBtn.style.display = 'inline-flex';
            if (ordersBtn) ordersBtn.innerText = '🏪 Restaurant Orders';
        } else if (state.user.role === 'ADMIN') {
            if (cartBtn) cartBtn.style.display = 'none';
            if (manageBtn) manageBtn.style.display = 'inline-flex';
            if (ordersBtn) ordersBtn.innerText = '👑 All System Orders';
        }
    } else {
        pill.style.display = 'none';
        authBtn.innerText = '🔐 Login / Register';
        if (cartBtn) cartBtn.style.display = 'inline-flex';
        if (manageBtn) manageBtn.style.display = 'none';
        if (ordersBtn) ordersBtn.innerText = '📦 My Orders';
    }

    if (cartCount) {
        cartCount.innerText = (state.cart && state.cart.items) ? state.cart.items.length : '0';
    }
}

export async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-pass').value;

    try {
        const res = await apiCall('/api/auth/login', 'POST', { email, password });
        saveSession(res.data.token, res.data.user);
        showToast(`Welcome back, ${res.data.user.name}!`);

        if (window.switchPage) {
            if (res.data.user.role === 'RESTAURANT_OWNER' || res.data.user.role === 'ADMIN') {
                window.switchPage('orders');
            } else {
                window.switchPage('catalog');
            }
        }
    } catch (err) {}
}

export async function handleRegister(e) {
    e.preventDefault();
    const role = document.getElementById('reg-role').value;
    const payload = {
        name: document.getElementById('reg-name').value,
        email: document.getElementById('reg-email').value,
        password: document.getElementById('reg-pass').value,
        phone: document.getElementById('reg-phone').value,
        role: role,
        restaurantName: role === 'RESTAURANT_OWNER' ? document.getElementById('reg-rest-name').value : null,
        restaurantAddress: role === 'RESTAURANT_OWNER' ? document.getElementById('reg-rest-addr').value : null
    };

    try {
        const res = await apiCall('/api/auth/register', 'POST', payload);
        saveSession(res.data.token, res.data.user);
        showToast(`Account registered as ${res.data.user.role}!`);

        if (window.switchPage) {
            if (res.data.user.role === 'RESTAURANT_OWNER' || res.data.user.role === 'ADMIN') {
                window.switchPage('manage');
            } else {
                window.switchPage('catalog');
            }
        }
    } catch (err) {}
}

export function saveSession(token, user) {
    setToken(token);
    setUser(user);
    updateHeaderState();
    if (user.role === 'CUSTOMER' && window.loadCartPage) {
        window.loadCartPage(true);
    }
}

export function logout() {
    setToken(null);
    setUser(null);
    setCart(null);
    updateHeaderState();
    showToast('Logged out');
    if (window.switchPage) window.switchPage('auth');
}
