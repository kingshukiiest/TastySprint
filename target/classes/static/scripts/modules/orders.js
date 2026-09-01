// Orders History & Live Tracker Module

import { state, setOrders } from '../config.js';
import { apiCall, showToast } from '../api.js';

export async function loadOrdersPage() {
    const listContainer = document.getElementById('orders-list-container');
    if (!state.token) {
        if (listContainer) listContainer.innerHTML = '<p class="text-muted">Please login to view order history.</p>';
        return;
    }

    const headerBanner = document.getElementById('orders-header-banner');
    if (headerBanner) {
        if (state.user && state.user.role === 'RESTAURANT_OWNER') {
            const restTitle = state.user.restaurantName ? `: ${state.user.restaurantName}` : '';
            headerBanner.innerHTML = `<h2 style="color: #0f172a;">🏪 Restaurant Orders Dashboard${restTitle}</h2><p class="text-muted" style="margin-top:0.25rem;">Review and release orders placed for your restaurant.</p>`;
        } else if (state.user && state.user.role === 'ADMIN') {
            headerBanner.innerHTML = `<h2 style="color: #0f172a;">👑 Platform System Orders Dashboard</h2><p class="text-muted" style="margin-top:0.25rem;">Admin overview of all orders placed across all restaurants.</p>`;
        } else {
            headerBanner.innerHTML = `<h2 style="color: #0f172a;">Order History & Live Tracker</h2><p class="text-muted" style="margin-top:0.25rem;">Track real-time status of your food orders.</p>`;
        }
    }

    try {
        const res = await apiCall('/api/orders');
        setOrders(res.data || []);
        renderOrdersList();
    } catch (err) {}
}

export function renderOrdersList() {
    const container = document.getElementById('orders-list-container');
    if (!container) return;

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
                        <select style="width:auto; padding:0.4rem 0.75rem;" class="select-order-status" data-order-id="${order.id}">
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

    container.querySelectorAll('.select-order-status').forEach(select => {
        select.addEventListener('change', () => {
            const orderId = parseInt(select.getAttribute('data-order-id'));
            updateStatus(orderId, select.value);
        });
    });
}

export async function updateStatus(orderId, newStatus) {
    if (!newStatus) return;
    try {
        await apiCall(`/api/orders/${orderId}/status`, 'PUT', { status: newStatus });
        showToast('Order status updated!');
        loadOrdersPage();
    } catch (err) {}
}
