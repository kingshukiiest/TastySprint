// Management Portal Module (Restaurants, Categories & Food Creation)

import { state, setRestaurants, setCategories } from '../config.js';
import { apiCall, showToast } from '../api.js';
import { switchPage } from '../app.js';

export async function loadManagePage() {
    try {
        const [resRest, resCats] = await Promise.all([
            apiCall('/api/restaurants'),
            apiCall('/api/categories')
        ]);

        setRestaurants(resRest.data || []);
        setCategories(resCats.data || []);

        const restSelect = document.getElementById('mrg-food-rest-id');
        const catSelect = document.getElementById('mrg-food-cat-id');

        if (restSelect) restSelect.innerHTML = state.restaurants.map(r => `<option value="${r.id}">${r.name}</option>`).join('');
        if (catSelect) catSelect.innerHTML = state.categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
    } catch (err) {}
}

export async function handleCreateRestaurant(e) {
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

export async function handleCreateCategory(e) {
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

export async function handleCreateFood(e) {
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
