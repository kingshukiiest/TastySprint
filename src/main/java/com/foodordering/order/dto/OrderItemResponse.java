package com.foodordering.order.dto;

import lombok.*;

/**
 * Response payload for order item details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long foodId;
    private String foodName;
    private Double foodPrice;
    private Integer quantity;
    private Double price;
}
