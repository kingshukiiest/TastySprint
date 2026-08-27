package com.foodordering.food.service;

import com.foodordering.food.dto.FoodRequest;
import com.foodordering.food.dto.FoodResponse;
import com.foodordering.food.entity.Food;

import java.util.List;

/**
 * Service interface for Food menu item operations.
 */
public interface FoodService {

    FoodResponse createFood(FoodRequest request, String userEmail);

    FoodResponse updateFood(Long id, FoodRequest request, String userEmail);

    FoodResponse getFoodById(Long id);

    List<FoodResponse> getAllFoods();

    List<FoodResponse> searchFoodsByName(String name);

    List<FoodResponse> getFoodsByCategory(Long categoryId);

    List<FoodResponse> getFoodsByRestaurant(Long restaurantId);

    void deleteFood(Long id, String userEmail);

    Food findEntityById(Long id);
}
