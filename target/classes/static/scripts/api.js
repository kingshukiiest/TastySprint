// API Fetch Engine & Toast UI Notifications

import { state } from './config.js';

export function showToast(msg) {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.innerText = msg;
    toast.style.display = 'block';
    setTimeout(() => { toast.style.display = 'none'; }, 4000);
}

export async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    const config = { method, headers };
    if (body) config.body = JSON.stringify(body);

    try {
        const res = await fetch(endpoint, config);
        
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
            if (res.status === 401 && state.token) {
                if (window.logout) window.logout();
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
