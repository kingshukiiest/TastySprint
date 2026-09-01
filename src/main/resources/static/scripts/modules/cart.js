// Cart & Checkout Module

import { state, setCart, setAddresses } from '../config.js';
import { apiCall, showToast } from '../api.js';
import { updateHeaderState } from './auth.js';

export async function addFoodToCart(foodId) {
    if (!state.token) {
        showToast('Please login or register as a Customer to add items to cart');
        if (window.switchPage) window.switchPage('auth');
        return;
    }

    if (state.user && state.user.role !== 'CUSTOMER') {
        showToast('Restaurant Owners and Admins cannot add items to cart or place orders.');
        return;
    }

    try {
        const res = await apiCall('/api/cart/items', 'POST', { foodId: foodId, quantity: 1 });
        setCart(res.data);
        updateHeaderState();
        showToast('🛒 Added to cart!');
    } catch (err) {}
}

export async function loadCartPage(silent = false) {
    if (!state.token || (state.user && state.user.role !== 'CUSTOMER')) {
        const container = document.getElementById('cart-items-container');
        if (!silent && container) container.innerHTML = '<p class="text-muted">Shopping cart is available for Customer accounts only.</p>';
        return;
    }

    try {
        const [resCart, resAddresses] = await Promise.all([
            apiCall('/api/cart'),
            apiCall('/api/addresses')
        ]);

        setCart(resCart.data);
        setAddresses(resAddresses.data || []);

        updateHeaderState();
        if (!silent) {
            renderCartItems();
            renderAddressDropdown();
        }
    } catch (err) {}
}

export function renderCartItems() {
    const container = document.getElementById('cart-items-container');
    const totalElem = document.getElementById('cart-total-amount');

    if (!container || !totalElem) return;

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
                <button class="btn btn-danger" onclick="removeCartItem(${item.id})" style="padding:0.35rem 0.75rem; font-size:0.8rem;">Remove</button>
            </div>
        </div>
    `).join('');
}

export function renderAddressDropdown() {
    const select = document.getElementById('select-address-dropdown');
    if (!select) return;

    if (state.addresses.length === 0) {
        select.innerHTML = '<option value="">No saved address. Add one below!</option>';
        return;
    }
    select.innerHTML = state.addresses.map(a => `
        <option value="${a.id}">${a.type}: ${a.houseNumber ? a.houseNumber + ', ' : ''}${a.street}, ${a.city} (${a.pincode})</option>
    `).join('');
}

export async function removeCartItem(itemId) {
    try {
        const res = await apiCall(`/api/cart/items/${itemId}`, 'DELETE');
        setCart(res.data);
        updateHeaderState();
        renderCartItems();
        showToast('Item removed from cart');
    } catch (err) {}
}

export async function handleAddAddress(e) {
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

export async function handlePlaceOrder() {
    const addrId = document.getElementById('select-address-dropdown').value;
    if (!addrId) {
        showToast('Please select or add a delivery address');
        return;
    }

    try {
        await apiCall('/api/orders', 'POST', { deliveryAddressId: parseInt(addrId) });
        showToast('🎉 Order placed successfully!');
        setCart(null);
        updateHeaderState();
        if (window.switchPage) window.switchPage('orders');
    } catch (err) {}
}
