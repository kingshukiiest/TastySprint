package com.foodordering.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request payload for creating an order from active cart items.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "Delivery address ID is required")
    private Long deliveryAddressId;
}
