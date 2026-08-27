package com.foodordering.restaurant.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO returning safe details of a restaurant.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private Double rating;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
}
