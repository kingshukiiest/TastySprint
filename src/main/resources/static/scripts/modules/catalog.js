// Food Menu Catalog Module

import { state, setFoods, setCategories, setRestaurants, setSelectedCategoryId } from '../config.js';
import { apiCall } from '../api.js';

export async function loadCatalogPage() {
    try {
        const [resFoods, resCats, resRest] = await Promise.all([
            apiCall('/api/foods'),
            apiCall('/api/categories'),
            apiCall('/api/restaurants')
        ]);

        setFoods(resFoods.data || []);
        setCategories(resCats.data || []);
        setRestaurants(resRest.data || []);

        renderCategoryPills();
        renderFoodsGrid();
    } catch (err) {}
}

export function renderCategoryPills() {
    const container = document.getElementById('categories-container');
    if (!container) return;

    let html = `<button class="cat-pill ${state.selectedCategoryId === null ? 'active' : ''}" data-cat-id="null">All Foods</button>`;
    
    html += state.categories.map(c => `
        <button class="cat-pill ${state.selectedCategoryId === c.id ? 'active' : ''}" data-cat-id="${c.id}">${c.name}</button>
    `).join('');

    container.innerHTML = html;

    container.querySelectorAll('.cat-pill').forEach(btn => {
        btn.addEventListener('click', () => {
            const rawId = btn.getAttribute('data-cat-id');
            const catId = rawId === 'null' ? null : parseInt(rawId);
            filterByCategory(catId);
        });
    });
}

export function filterByCategory(catId) {
    setSelectedCategoryId(catId);
    renderCategoryPills();
    renderFoodsGrid();
}

export function renderFoodsGrid() {
    const container = document.getElementById('food-grid');
    if (!container) return;

    const searchInput = document.getElementById('search-input');
    const search = searchInput ? searchInput.value.toLowerCase() : '';

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
                    <button class="tag-pill btn-open-reviews" data-food-id="${f.id}" data-food-name="${f.name.replace(/"/g, '&quot;')}">⭐ Reviews</button>
                </div>
            </div>
            <div class="food-footer">
                <span class="food-price">₹${Math.round(f.price)}</span>
                ${isCustomerOrGuest ? `
                    <button class="btn btn-primary btn-add-cart" data-food-id="${f.id}" style="padding:0.5rem 1rem;">+ Add to Cart</button>
                ` : ''}
            </div>
        </div>
    `).join('');

    // Bind dynamic review and add-to-cart buttons
    container.querySelectorAll('.btn-open-reviews').forEach(btn => {
        btn.addEventListener('click', () => {
            const foodId = parseInt(btn.getAttribute('data-food-id'));
            const foodName = btn.getAttribute('data-food-name');
            if (window.openReviewsModal) window.openReviewsModal(foodId, foodName);
        });
    });

    container.querySelectorAll('.btn-add-cart').forEach(btn => {
        btn.addEventListener('click', () => {
            const foodId = parseInt(btn.getAttribute('data-food-id'));
            if (window.addFoodToCart) window.addFoodToCart(foodId);
        });
    });
}
