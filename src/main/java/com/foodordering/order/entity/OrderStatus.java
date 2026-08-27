package com.foodordering.order.entity;

/**
 * Lifecycle status of an order:
 * PLACED -> ACCEPTED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED (or CANCELLED)
 */
public enum OrderStatus {
    PLACED,
    ACCEPTED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
