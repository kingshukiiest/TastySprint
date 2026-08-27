package com.foodordering.user.entity;

/**
 * Roles available in the Food Ordering System:
 * - CUSTOMER: Can browse, add to cart, order food, write reviews, manage addresses.
 * - RESTAURANT_OWNER: Can manage own restaurant, food items, and order fulfillment status.
 * - ADMIN: Can manage users, restaurants, categories, and view system-wide metrics/orders.
 */
public enum Role {
    CUSTOMER,
    RESTAURANT_OWNER,
    ADMIN
}
