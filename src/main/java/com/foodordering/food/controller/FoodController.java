package com.foodordering.food.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.food.dto.FoodRequest;
import com.foodordering.food.dto.FoodResponse;
import com.foodordering.food.service.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing endpoints for menu food item CRUD, search, and filtering.
 */
@RestController
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping("/api/foods")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FoodResponse>> createFood(
            Authentication authentication,
            @Valid @RequestBody FoodRequest request) {
        String userEmail = authentication.getName();
        FoodResponse response = foodService.createFood(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Food item created successfully", response));
    }

    @GetMapping("/api/foods")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> getAllFoods() {
        List<FoodResponse> foods = foodService.getAllFoods();
        return ResponseEntity.ok(ApiResponse.success("Food items retrieved successfully", foods));
    }

    @GetMapping("/api/foods/{id}")
    public ResponseEntity<ApiResponse<FoodResponse>> getFoodById(@PathVariable Long id) {
        FoodResponse response = foodService.getFoodById(id);
        return ResponseEntity.ok(ApiResponse.success("Food item retrieved successfully", response));
    }

    @PutMapping("/api/foods/{id}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FoodResponse>> updateFood(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody FoodRequest request) {
        String userEmail = authentication.getName();
        FoodResponse response = foodService.updateFood(id, request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Food item updated successfully", response));
    }

    @DeleteMapping("/api/foods/{id}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFood(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        foodService.deleteFood(id, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Food item deleted successfully", null));
    }

    @GetMapping("/api/foods/search")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> searchFoods(@RequestParam("name") String name) {
        List<FoodResponse> foods = foodService.searchFoodsByName(name);
        return ResponseEntity.ok(ApiResponse.success("Food search results retrieved", foods));
    }

    @GetMapping("/api/foods/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> getFoodsByCategory(@PathVariable Long categoryId) {
        List<FoodResponse> foods = foodService.getFoodsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category food items retrieved", foods));
    }

    @GetMapping("/api/restaurants/{restaurantId}/foods")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> getFoodsByRestaurant(@PathVariable Long restaurantId) {
        List<FoodResponse> foods = foodService.getFoodsByRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Restaurant food items retrieved", foods));
    }
}
