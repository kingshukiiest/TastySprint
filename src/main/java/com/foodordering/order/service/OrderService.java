package com.foodordering.order.service;

import com.foodordering.order.dto.CreateOrderRequest;
import com.foodordering.order.dto.OrderResponse;
import com.foodordering.order.dto.UpdateOrderStatusRequest;
import com.foodordering.order.entity.Order;

import java.util.List;

/**
 * Service interface for Order lifecycle and fulfillment management.
 */
public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request, String customerEmail);

    OrderResponse getOrderById(Long id, String userEmail);

    List<OrderResponse> getUserOrders(String userEmail);

    OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request, String userEmail);

    OrderResponse cancelOrder(Long id, String userEmail);

    Order findEntityById(Long id);
}
