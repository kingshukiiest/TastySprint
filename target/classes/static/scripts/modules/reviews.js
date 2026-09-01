// Food Reviews Modal Module

import { state } from '../config.js';
import { apiCall, showToast } from '../api.js';
import { switchPage } from '../app.js';

export async function openReviewsModal(foodId, foodName) {
    const titleElem = document.getElementById('modal-food-title');
    const foodIdInput = document.getElementById('review-food-id');
    const modal = document.getElementById('reviews-modal');

    if (titleElem) titleElem.innerText = `${foodName} - Reviews`;
    if (foodIdInput) foodIdInput.value = foodId;
    if (modal) modal.style.display = 'flex';

    loadFoodReviews(foodId);
}

export function closeReviewsModal() {
    const modal = document.getElementById('reviews-modal');
    if (modal) modal.style.display = 'none';
}

export async function loadFoodReviews(foodId) {
    const listContainer = document.getElementById('modal-reviews-list');
    if (!listContainer) return;

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

export async function handleAddReview(e) {
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
