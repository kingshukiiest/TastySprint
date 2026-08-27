package com.foodordering.order.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.order.dto.CreateOrderRequest;
import com.foodordering.order.dto.OrderResponse;
import com.foodordering.order.dto.UpdateOrderStatusRequest;
import com.foodordering.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing endpoints for creating orders, fetching order history, updating status, and cancelling orders.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request) {
        String customerEmail = authentication.getName();
        OrderResponse response = orderService.createOrder(request, customerEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        List<OrderResponse> orders = orderService.getUserOrders(userEmail);
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        OrderResponse response = orderService.getOrderById(id, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        String userEmail = authentication.getName();
        OrderResponse response = orderService.updateOrderStatus(id, request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        OrderResponse response = orderService.cancelOrder(id, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", response));
    }
}
