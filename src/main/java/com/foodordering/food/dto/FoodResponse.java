package com.foodordering.food.dto;

import lombok.*;

/**
 * Response payload returning detailed information about menu items.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean available;
    private Long restaurantId;
    private String restaurantName;
    private Long categoryId;
    private String categoryName;
}
