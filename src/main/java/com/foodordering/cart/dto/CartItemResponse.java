package com.foodordering.cart.dto;

import lombok.*;

/**
 * Response payload for individual cart item details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long id;
    private Long foodId;
    private String foodName;
    private String foodImageUrl;
    private Double foodPrice;
    private Integer quantity;
    private Double price;
}
