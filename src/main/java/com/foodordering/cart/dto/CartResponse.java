package com.foodordering.cart.dto;

import lombok.*;

import java.util.List;

/**
 * Response payload returning full user shopping cart details and calculated total.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private Double totalPrice;
}
