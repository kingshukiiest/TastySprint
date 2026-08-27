package com.foodordering.restaurant.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.restaurant.dto.RestaurantRequest;
import com.foodordering.restaurant.dto.RestaurantResponse;
import com.foodordering.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing endpoints for creating, retrieving, updating, and deleting restaurants.
 */
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> createRestaurant(
            Authentication authentication,
            @Valid @RequestBody RestaurantRequest request) {
        String ownerEmail = authentication.getName();
        RestaurantResponse response = restaurantService.createRestaurant(request, ownerEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Restaurant created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(ApiResponse.success("Restaurants retrieved successfully", restaurants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getRestaurantById(@PathVariable Long id) {
        RestaurantResponse response = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> updateRestaurant(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody RestaurantRequest request) {
        String userEmail = authentication.getName();
        RestaurantResponse response = restaurantService.updateRestaurant(id, request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRestaurant(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        restaurantService.deleteRestaurant(id, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Restaurant deleted successfully", null));
    }
}
