package com.foodordering.order.dto;

import com.foodordering.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request payload for updating order status by restaurant owner or admin.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus status;
}
