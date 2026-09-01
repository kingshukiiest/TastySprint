// Application State & Storage Manager

export const API_BASE = '';

export let state = {
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

export function setToken(token) {
    state.token = token;
    if (token) localStorage.setItem('jwt_token', token);
    else localStorage.removeItem('jwt_token');
}

export function setUser(user) {
    state.user = user;
    if (user) localStorage.setItem('jwt_user', JSON.stringify(user));
    else localStorage.removeItem('jwt_user');
}

export function setCart(cart) {
    state.cart = cart;
}

export function setFoods(foods) {
    state.foods = foods;
}

export function setCategories(categories) {
    state.categories = categories;
}

export function setRestaurants(restaurants) {
    state.restaurants = restaurants;
}

export function setAddresses(addresses) {
    state.addresses = addresses;
}

export function setOrders(orders) {
    state.orders = orders;
}

export function setSelectedCategoryId(catId) {
    state.selectedCategoryId = catId;
}
