// TASTY SPRINT Single Page Application (SPA) Engine

const API_BASE = '';

let state = {
    token: localStorage.getItem('jwt_token') || null,
    user: JSON.parse(localStorage.getItem('jwt_user')) || null,
    cart: null,
    foods: [],
    categories: [],
    restaurants: [],
    addresses: [],
    orders: [],
    selectedCategoryId: null
};

// UI Notification Toast
function showToast(msg) {
    const toast = document.getElementById('toast');
    toast.innerText = msg;
    toast.style.display = 'block';
    setTimeout(() => { toast.style.display = 'none'; }, 4000);
}

// API Request Wrapper (Safely parses empty, JSON, and error responses)
async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    const config = { method, headers };
    if (body) config.body = JSON.stringify(body);

    try {
        const res = await fetch(endpoint, config);
        
        // Read raw text first to handle empty bodies safely
        const text = await res.text();
        let json = {};
        if (text && text.trim().length > 0) {
            try {
                json = JSON.parse(text);
            } catch (parseErr) {
                json = { message: text };
            }
        }

        if (!res.ok) {
            // Auto clear expired or invalid session token on HTTP 401 / 403
            if (res.status === 401 && state.token) {
                logout();
                throw new Error('Session expired or invalid token. Please log in again.');
            }

            const errorMsg = json.message || json.error || `HTTP ${res.status}: ${res.statusText || 'API Request Failed'}`;
            throw new Error(errorMsg);
        }
        return json;
    } catch (err) {
        showToast(`❌ ${err.message}`);
        throw err;
    }
}

// Toggle Restaurant Owner Registration Inputs
function toggleOwnerFields(roleValue) {
    const box = document.getElementById('owner-fields-box');
    if (roleValue === 'RESTAURANT_OWNER') {
        box.style.display = 'block';
    } else {
        box.style.display = 'none';
    }
}

// Update User Header State
function updateHeaderState() {
    const pill = document.getElementById('user-info-pill');
    const authBtn = document.getElementById('nav-auth');
    const manageBtn = document.getElementById('nav-manage');
    const cartBtn = document.getElementById('nav-cart');
    const ordersBtn = document.getElementById('nav-orders');
    const cartCount = document.getElementById('cart-count');

    if (state.token && state.user) {
        const restLabel = state.user.restaurantName ? ` • ${state.user.restaurantName}` : '';
        pill.innerHTML = `<span>👤 ${state.user.name}</span> <span class="role-badge">${state.user.role}${restLabel}</span>`;
        pill.style.display = 'flex';
        authBtn.innerText = 'Logout 🚪';

        // Role-based visibility and navigation label rules
        if (state.user.role === 'CUSTOMER') {
            cartBtn.style.display = 'inline-flex';
            manageBtn.style.display = 'none';
            ordersBtn.innerText = '📦 My Orders';
        } else if (state.user.role === 'RESTAURANT_OWNER') {
            cartBtn.style.display = 'none';
            manageBtn.style.display = 'inline-flex';
            ordersBtn.innerText = '🏪 Restaurant Orders';
        } else if (state.user.role === 'ADMIN') {
            cartBtn.style.display = 'none';
            manageBtn.style.display = 'inline-flex';
            ordersBtn.innerText = '👑 All System Orders';
        }
    } else {
        pill.style.display = 'none';
        authBtn.innerText = '🔐 Login / Register';
        cartBtn.style.display = 'inline-flex';
        manageBtn.style.display = 'none';
        ordersBtn.innerText = '📦 My Orders';
    }

    if (state.cart && state.cart.items) {
        cartCount.innerText = state.cart.items.length;
    } else {
        cartCount.innerText = '0';
    }
}

// Page View Router
function switchPage(pageId) {
    // Guard against non-customers entering cart page
    if (pageId === 'cart' && state.user && state.user.role !== 'CUSTOMER') {
        showToast('Restaurant Owners and Admins cannot access the shopping cart.');
        return;
    }

    document.querySelectorAll('.page-view').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));

    const targetPage = document.getElementById(`page-${pageId}`);
    if (targetPage) targetPage.classList.add('active');

    const targetNav = document.getElementById(`nav-${pageId}`);
    if (targetNav) targetNav.classList.add('active');

    // Data Load Hooks per Page
    if (pageId === 'catalog') loadCatalogPage();
    if (pageId === 'cart') loadCartPage();
    if (pageId === 'orders') loadOrdersPage();
    if (pageId === 'manage') loadManagePage();
}

// Auth Toggle inside Login/Register Page
function toggleAuthMode(mode) {
    const loginBox = document.getElementById('auth-login-box');
    const regBox = document.getElementById('auth-register-box');
    const btnLogin = document.getElementById('btn-toggle-login');
    const btnReg = document.getElementById('btn-toggle-register');

    if (mode === 'login') {
        loginBox.style.display = 'block';
        regBox.style.display = 'none';
        btnLogin.classList.add('active');
        btnReg.classList.remove('active');
    } else {
        loginBox.style.display = 'none';
        regBox.style.display = 'block';
        btnLogin.classList.remove('active');
        btnReg.classList.add('active');
    }
}

function fillDemo(email, password) {
    document.getElementById('login-email').value = email;
    document.getElementById('login-pass').value = password;
}

// Auth Actions
async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-pass').value;

    try {
        const res = await apiCall('/api/auth/login', 'POST', { email, password });
        saveSession(res.data.token, res.data.user);
        showToast(`Welcome back, ${res.data.user.name}!`);

        if (res.data.user.role === 'RESTAURANT_OWNER' || res.data.user.role === 'ADMIN') {
            switchPage('orders');
        } else {
            switchPage('catalog');
        }
    } catch (err) {}
}

async function handleRegister(e) {
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

        if (res.data.user.role === 'RESTAURANT_OWNER' || res.data.user.role === 'ADMIN') {
            switchPage('manage');
        } else {
            switchPage('catalog');
        }
    } catch (err) {}
}

function saveSession(token, user) {
    state.token = token;
    state.user = user;
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('jwt_user', JSON.stringify(user));
    updateHeaderState();
    if (user.role === 'CUSTOMER') {
        loadCartPage(true);
    }
}

function logout() {
    state.token = null;
    state.user = null;
    state.cart = null;
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_user');
    updateHeaderState();
    showToast('Logged out');
    switchPage('auth');
}

// CATALOG & FOOD PAGE LOGIC
async function loadCatalogPage() {
    try {
        const [resFoods, resCats, resRest] = await Promise.all([
            apiCall('/api/foods'),
            apiCall('/api/categories'),
            apiCall('/api/restaurants')
        ]);

        state.foods = resFoods.data || [];
        state.categories = resCats.data || [];
        state.restaurants = resRest.data || [];

        renderCategoryPills();
        renderFoodsGrid();
    } catch (err) {}
}

function renderCategoryPills() {
    const container = document.getElementById('categories-container');
    let html = `<button class="cat-pill ${state.selectedCategoryId === null ? 'active' : ''}" onclick="filterByCategory(null)">All Foods</button>`;
    
    html += state.categories.map(c => `
        <button class="cat-pill ${state.selectedCategoryId === c.id ? 'active' : ''}" onclick="filterByCategory(${c.id})">${c.name}</button>
    `).join('');

    container.innerHTML = html;
}

function filterByCategory(catId) {
    state.selectedCategoryId = catId;
    renderCategoryPills();
    renderFoodsGrid();
}

function renderFoodsGrid() {
    const container = document.getElementById('food-grid');
    const search = document.getElementById('search-input').value.toLowerCase();

    const filtered = state.foods.filter(f => {
        const matchName = f.name.toLowerCase().includes(search);
        const matchCat = state.selectedCategoryId === null || f.categoryId === state.selectedCategoryId;
        return matchName && matchCat;
    });

    if (filtered.length === 0) {
        container.innerHTML = `<div class="card" style="grid-column: 1/-1; text-align:center; padding:3rem;"><p class="text-muted">No food items match your criteria.</p></div>`;
        return;
    }

    const defaultImages = [
        'https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=500',
        'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500',
        'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=500',
        'https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=500'
    ];

    const isCustomerOrGuest = !state.user || state.user.role === 'CUSTOMER';

    container.innerHTML = filtered.map((f, idx) => `
        <div class="food-card">
            <div class="food-img-wrapper">
                <img src="${f.imageUrl || defaultImages[idx % defaultImages.length]}" class="food-img" alt="${f.name}">
                <div class="food-badge">${f.restaurantName || 'Restaurant'}</div>
            </div>
            <div class="food-body">
                <div class="food-title">${f.name}</div>
                <div class="food-desc">${f.description || 'Freshly prepared delicious meal with premium quality ingredients.'}</div>
                <div class="food-meta">
                    <button class="tag-pill" onclick="openReviewsModal(${f.id}, '${f.name.replace(/'/g, "\\'")}')">⭐ Reviews</button>
                </div>
            </div>
            <div class="food-footer">
                <span class="food-price">₹${Math.round(f.price)}</span>
                ${isCustomerOrGuest ? `
                    <button class="btn btn-primary" onclick="addFoodToCart(${f.id})" style="padding:0.5rem 1rem;">+ Add to Cart</button>
                ` : ''}
            </div>
        </div>
    `).join('');
}

// FOOD REVIEWS MODAL LOGIC
async function openReviewsModal(foodId, foodName) {
    document.getElementById('modal-food-title').innerText = `${foodName} - Reviews`;
    document.getElementById('review-food-id').value = foodId;

    const modal = document.getElementById('reviews-modal');
    modal.style.display = 'flex';

    loadFoodReviews(foodId);
}

function closeReviewsModal() {
    document.getElementById('reviews-modal').style.display = 'none';
}

async function loadFoodReviews(foodId) {
    const listContainer = document.getElementById('modal-reviews-list');
    listContainer.innerHTML = '<p class="text-muted">Loading reviews...</p>';

    try {
        const res = await apiCall(`/api/foods/${foodId}/reviews`);
        const reviews = res.data || [];

        if (reviews.length === 0) {
            listContainer.innerHTML = '<p class="text-muted" style="padding:1rem 0;">No reviews yet for this dish. Be the first to review!</p>';
            return;
        }

        listContainer.innerHTML = reviews.map(r => `
            <div style="background:var(--bg-input); padding:0.85rem; border-radius:var(--radius-md); margin-bottom:0.75rem;">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.35rem;">
                    <strong>👤 ${r.userName}</strong>
                    <span style="color:#eab308; font-weight:800;">${'⭐'.repeat(r.rating)}</span>
                </div>
                <div style="font-size:0.9rem; color:#334155;">"${r.comment}"</div>
                <div class="text-muted" style="font-size:0.75rem; margin-top:0.35rem;">${new Date(r.createdAt).toLocaleDateString()}</div>
            </div>
        `).join('');
    } catch (err) {
        listContainer.innerHTML = '<p class="text-muted">Unable to load reviews.</p>';
    }
}

async function handleAddReview(e) {
    e.preventDefault();
    if (!state.token) {
        showToast('Please login or register to write a review');
        closeReviewsModal();
        switchPage('auth');
        return;
    }

    const foodId = parseInt(document.getElementById('review-food-id').value);
    const rating = parseInt(document.getElementById('review-rating').value);
    const comment = document.getElementById('review-comment').value;

    try {
        await apiCall('/api/reviews', 'POST', { foodId, rating, comment });
        showToast('⭐ Review submitted!');
        document.getElementById('review-comment').value = '';
        loadFoodReviews(foodId);
    } catch (err) {}
}

// CART & CHECKOUT PAGE LOGIC
async function addFoodToCart(foodId) {
    if (!state.token) {
        showToast('Please login or register as a Customer to add items to cart');
        switchPage('auth');
        return;
    }

    if (state.user && state.user.role !== 'CUSTOMER') {
        showToast('Restaurant Owners and Admins cannot add items to cart or place orders.');
        return;
    }

    try {
        const res = await apiCall('/api/cart/items', 'POST', { foodId: foodId, quantity: 1 });
        state.cart = res.data;
        updateHeaderState();
        showToast('🛒 Added to cart!');
    } catch (err) {}
}

async function loadCartPage(silent = false) {
    if (!state.token || (state.user && state.user.role !== 'CUSTOMER')) {
        if (!silent) document.getElementById('cart-items-container').innerHTML = '<p class="text-muted">Shopping cart is available for Customer accounts only.</p>';
        return;
    }

    try {
        const [resCart, resAddresses] = await Promise.all([
            apiCall('/api/cart'),
            apiCall('/api/addresses')
        ]);

        state.cart = resCart.data;
        state.addresses = resAddresses.data || [];

        updateHeaderState();
        if (!silent) {
            renderCartItems();
            renderAddressDropdown();
        }
    } catch (err) {}
}

function renderCartItems() {
    const container = document.getElementById('cart-items-container');
    const totalElem = document.getElementById('cart-total-amount');

    if (!state.cart || !state.cart.items || state.cart.items.length === 0) {
        container.innerHTML = '<p class="text-muted">Your shopping cart is empty. Explore the Menu Catalog!</p>';
        totalElem.innerText = '₹0';
        return;
    }

    totalElem.innerText = `₹${Math.round(state.cart.totalPrice)}`;

    container.innerHTML = state.cart.items.map(item => `
        <div style="display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--border); padding:0.9rem 0;">
            <div>
                <strong style="font-size:1.05rem;">${item.foodName}</strong>
                <div class="text-muted" style="font-size:0.85rem;">₹${Math.round(item.foodPrice)} x ${item.quantity}</div>
            </div>
            <div style="display:flex; align-items:center; gap:1rem;">
                <span style="font-weight:800; font-size:1.1rem; color:#0f172a;">₹${Math.round(item.price)}</span>
                <button class="btn btn-danger" style="padding:0.35rem 0.75rem; font-size:0.8rem;" onclick="removeCartItem(${item.id})">Remove</button>
            </div>
        </div>
    `).join('');
}

function renderAddressDropdown() {
    const select = document.getElementById('select-address-dropdown');
    if (state.addresses.length === 0) {
        select.innerHTML = '<option value="">No saved address. Add one below!</option>';
        return;
    }
    select.innerHTML = state.addresses.map(a => `
        <option value="${a.id}">${a.type}: ${a.houseNumber ? a.houseNumber + ', ' : ''}${a.street}, ${a.city} (${a.pincode})</option>
    `).join('');
}

async function removeCartItem(itemId) {
    try {
        const res = await apiCall(`/api/cart/items/${itemId}`, 'DELETE');
        state.cart = res.data;
        updateHeaderState();
        renderCartItems();
        showToast('Item removed from cart');
    } catch (err) {}
}

async function handleAddAddress(e) {
    e.preventDefault();
    const payload = {
        street: document.getElementById('addr-street').value,
        city: document.getElementById('addr-city').value,
        state: document.getElementById('addr-state').value,
        pincode: document.getElementById('addr-pincode').value,
        type: document.getElementById('addr-type').value
    };

    try {
        await apiCall('/api/addresses', 'POST', payload);
        showToast('Address added!');
        loadCartPage();
    } catch (err) {}
}

async function handlePlaceOrder() {
    const addrId = document.getElementById('select-address-dropdown').value;
    if (!addrId) {
        showToast('Please select or add a delivery address');
        return;
    }

    try {
        await apiCall('/api/orders', 'POST', { deliveryAddressId: parseInt(addrId) });
        showToast('🎉 Order placed successfully!');
        state.cart = null;
        updateHeaderState();
        switchPage('orders');
    } catch (err) {}
}

// ORDERS PAGE LOGIC
async function loadOrdersPage() {
    if (!state.token) {
        document.getElementById('orders-list-container').innerHTML = '<p class="text-muted">Please login to view order history.</p>';
        return;
    }

    const headerBanner = document.getElementById('orders-header-banner');
    if (state.user && state.user.role === 'RESTAURANT_OWNER') {
        const restTitle = state.user.restaurantName ? `: ${state.user.restaurantName}` : '';
        headerBanner.innerHTML = `<h2 style="color: #0f172a;">🏪 Restaurant Orders Dashboard${restTitle}</h2><p class="text-muted" style="margin-top:0.25rem;">Review and release orders placed for your restaurant.</p>`;
    } else if (state.user && state.user.role === 'ADMIN') {
        headerBanner.innerHTML = `<h2 style="color: #0f172a;">👑 Platform System Orders Dashboard</h2><p class="text-muted" style="margin-top:0.25rem;">Admin overview of all orders placed across all restaurants.</p>`;
    } else {
        headerBanner.innerHTML = `<h2 style="color: #0f172a;">Order History & Live Tracker</h2><p class="text-muted" style="margin-top:0.25rem;">Track real-time status of your food orders.</p>`;
    }

    try {
        const res = await apiCall('/api/orders');
        state.orders = res.data || [];
        renderOrdersList();
    } catch (err) {}
}

function renderOrdersList() {
    const container = document.getElementById('orders-list-container');
    if (state.orders.length === 0) {
        container.innerHTML = '<p class="text-muted">No orders found for this account.</p>';
        return;
    }

    const isOwner = state.user && state.user.role === 'RESTAURANT_OWNER';

    container.innerHTML = state.orders.map(order => `
        <div class="card">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem;">
                <div>
                    <h3 style="font-size:1.15rem; color:#0f172a;">Order #${order.id}</h3>
                    <div class="text-muted" style="font-size:0.8rem;">Placed on: ${new Date(order.orderDate).toLocaleString()}</div>
                </div>
                <span class="status-tag status-${order.status}">${order.status}</span>
            </div>

            <div style="background:var(--bg-input); padding:1rem; border-radius:var(--radius-md); margin-bottom:1rem; display:grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap:0.5rem;">
                <div><strong>🏪 Restaurant:</strong> ${order.restaurantName}</div>
                <div><strong>👤 Customer:</strong> ${order.customerName}</div>
                <div><strong>✉️ Email:</strong> ${order.customerEmail || 'N/A'}</div>
                <div><strong>📞 Phone:</strong> ${order.customerPhone || 'N/A'}</div>
                <div style="grid-column: 1 / -1;"><strong>📍 Delivery Address:</strong> ${order.deliveryAddress ? order.deliveryAddress.street + ', ' + order.deliveryAddress.city + ' (' + order.deliveryAddress.pincode + ')' : 'Standard Delivery'}</div>
            </div>

            <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:1rem;">
                <span style="font-size:1.2rem; font-weight:800; color:#0f172a;">Total Amount: ₹${Math.round(order.totalAmount)}</span>
                ${isOwner ? `
                    <div style="display:flex; align-items:center; gap:0.5rem;">
                        <span style="font-size:0.85rem; color:var(--text-muted);">Release / Update Status:</span>
                        <select style="width:auto; padding:0.4rem 0.75rem;" onchange="updateStatus(${order.id}, this.value)">
                            <option value="">Choose State...</option>
                            <option value="ACCEPTED">ACCEPTED</option>
                            <option value="PREPARING">PREPARING</option>
                            <option value="OUT_FOR_DELIVERY">OUT FOR DELIVERY</option>
                            <option value="DELIVERED">DELIVERED</option>
                            <option value="CANCELLED">CANCELLED</option>
                        </select>
                    </div>
                ` : ''}
            </div>
        </div>
    `).join('');
}

async function updateStatus(orderId, newStatus) {
    if (!newStatus) return;
    try {
        await apiCall(`/api/orders/${orderId}/status`, 'PUT', { status: newStatus });
        showToast('Order status updated!');
        loadOrdersPage();
    } catch (err) {}
}

// MANAGEMENT PORTAL LOGIC
async function loadManagePage() {
    try {
        const [resRest, resCats] = await Promise.all([
            apiCall('/api/restaurants'),
            apiCall('/api/categories')
        ]);

        state.restaurants = resRest.data || [];
        state.categories = resCats.data || [];

        const restSelect = document.getElementById('mrg-food-rest-id');
        const catSelect = document.getElementById('mrg-food-cat-id');

        restSelect.innerHTML = state.restaurants.map(r => `<option value="${r.id}">${r.name}</option>`).join('');
        catSelect.innerHTML = state.categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
    } catch (err) {}
}

async function handleCreateRestaurant(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById('mrg-rest-name').value,
        description: document.getElementById('mrg-rest-desc').value,
        address: document.getElementById('mrg-rest-addr').value,
        phone: document.getElementById('mrg-rest-phone').value
    };

    try {
        await apiCall('/api/restaurants', 'POST', payload);
        showToast('Restaurant created!');
        loadManagePage();
        switchPage('catalog');
    } catch (err) {}
}

async function handleCreateCategory(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById('mrg-cat-name').value
    };

    try {
        await apiCall('/api/categories', 'POST', payload);
        showToast('Category created!');
        loadManagePage();
    } catch (err) {}
}

async function handleCreateFood(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById('mrg-food-name').value,
        description: document.getElementById('mrg-food-desc').value,
        price: parseFloat(document.getElementById('mrg-food-price').value),
        imageUrl: document.getElementById('mrg-food-img').value,
        restaurantId: parseInt(document.getElementById('mrg-food-rest-id').value),
        categoryId: parseInt(document.getElementById('mrg-food-cat-id').value)
    };

    try {
        await apiCall('/api/foods', 'POST', payload);
        showToast('Food item added to menu!');
        switchPage('catalog');
    } catch (err) {}
}

// Application Bootstrapping
document.addEventListener('DOMContentLoaded', () => {
    updateHeaderState();
    switchPage('catalog');
    if (state.token && state.user && state.user.role === 'CUSTOMER') {
        loadCartPage(true);
    }

    // Event Bindings
    document.getElementById('nav-auth').addEventListener('click', () => {
        if (state.token) logout();
        else switchPage('auth');
    });

    document.getElementById('search-input').addEventListener('input', renderFoodsGrid);
    document.getElementById('form-login').addEventListener('submit', handleLogin);
    document.getElementById('form-register').addEventListener('submit', handleRegister);
    document.getElementById('form-add-address').addEventListener('submit', handleAddAddress);
    document.getElementById('btn-submit-order').addEventListener('click', handlePlaceOrder);

    document.getElementById('form-create-restaurant').addEventListener('submit', handleCreateRestaurant);
    document.getElementById('form-create-category').addEventListener('submit', handleCreateCategory);
    document.getElementById('form-create-food').addEventListener('submit', handleCreateFood);
    document.getElementById('form-add-review').addEventListener('submit', handleAddReview);
});
