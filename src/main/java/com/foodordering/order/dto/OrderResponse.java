package com.foodordering.order.dto;

import com.foodordering.address.dto.AddressResponse;
import com.foodordering.order.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response payload returning full order details including customer contact information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Long restaurantId;
    private String restaurantName;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private OrderStatus status;
    private AddressResponse deliveryAddress;
    private List<OrderItemResponse> items;
}
