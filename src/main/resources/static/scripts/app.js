// TASTY SPRINT Single Page Application (SPA) Engine - Master Router

import { state } from './config.js';
import { updateHeaderState, handleLogin, handleRegister, logout, toggleOwnerFields, toggleAuthMode, fillDemo } from './modules/auth.js';
import { loadCatalogPage, filterByCategory, renderFoodsGrid } from './modules/catalog.js';
import { loadCartPage, handleAddAddress, handlePlaceOrder, addFoodToCart, removeCartItem } from './modules/cart.js';
import { loadOrdersPage, updateStatus } from './modules/orders.js';
import { loadManagePage, handleCreateRestaurant, handleCreateCategory, handleCreateFood } from './modules/manage.js';
import { openReviewsModal, closeReviewsModal, handleAddReview } from './modules/reviews.js';

// Page View Router
export function switchPage(pageId) {
    if (pageId === 'cart' && state.user && state.user.role !== 'CUSTOMER') {
        alert('Restaurant Owners and Admins cannot access the shopping cart.');
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

// Expose global window bindings for inline HTML triggers
window.switchPage = switchPage;
window.filterByCategory = filterByCategory;
window.openReviewsModal = openReviewsModal;
window.closeReviewsModal = closeReviewsModal;
window.addFoodToCart = addFoodToCart;
window.removeCartItem = removeCartItem;
window.updateStatus = updateStatus;
window.toggleOwnerFields = toggleOwnerFields;
window.toggleAuthMode = toggleAuthMode;
window.fillDemo = fillDemo;
window.logout = logout;
window.loadCartPage = loadCartPage;

// Attach Form Event Listeners
function attachFormListeners() {
    const bind = (id, event, fn) => {
        const elem = document.getElementById(id);
        if (elem) elem.addEventListener(event, fn);
    };

    bind('nav-auth', 'click', () => {
        if (state.token) logout();
        else switchPage('auth');
    });

    bind('search-input', 'input', renderFoodsGrid);
    bind('form-login', 'submit', handleLogin);
    bind('form-register', 'submit', handleRegister);
    bind('form-add-address', 'submit', handleAddAddress);
    bind('btn-submit-order', 'click', handlePlaceOrder);
    bind('form-create-restaurant', 'submit', handleCreateRestaurant);
    bind('form-create-category', 'submit', handleCreateCategory);
    bind('form-create-food', 'submit', handleCreateFood);
    bind('form-add-review', 'submit', handleAddReview);
}

// Application Bootstrapping
document.addEventListener('DOMContentLoaded', () => {
    attachFormListeners();
    updateHeaderState();
    switchPage('catalog');

    if (state.token && state.user && state.user.role === 'CUSTOMER') {
        loadCartPage(true);
    }
});
