package com.foodordering.food.service;

import com.foodordering.category.entity.Category;
import com.foodordering.category.service.CategoryService;
import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.exception.UnauthorizedException;
import com.foodordering.food.dto.FoodRequest;
import com.foodordering.food.dto.FoodResponse;
import com.foodordering.food.entity.Food;
import com.foodordering.food.repository.FoodRepository;
import com.foodordering.restaurant.entity.Restaurant;
import com.foodordering.restaurant.service.RestaurantService;
import com.foodordering.user.entity.Role;
import com.foodordering.user.entity.User;
import com.foodordering.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of FoodService with owner verification, category mapping, and search queries.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantService restaurantService;
    private final CategoryService categoryService;
    private final UserService userService;

    @Override
    public FoodResponse createFood(FoodRequest request, String userEmail) {
        Restaurant restaurant = restaurantService.findEntityById(request.getRestaurantId());
        User user = userService.findEntityByEmail(userEmail);

        if (!restaurant.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not authorized to add food items to this restaurant");
        }

        Category category = categoryService.findEntityById(request.getCategoryId());

        Food food = Food.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .restaurant(restaurant)
                .category(category)
                .build();

        Food savedFood = foodRepository.save(food);
        return mapToResponse(savedFood);
    }

    @Override
    public FoodResponse updateFood(Long id, FoodRequest request, String userEmail) {
        Food food = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        if (!food.getRestaurant().getOwner().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not authorized to update food items for this restaurant");
        }

        if (request.getCategoryId() != null && !food.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryService.findEntityById(request.getCategoryId());
            food.setCategory(category);
        }

        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setImageUrl(request.getImageUrl());
        if (request.getAvailable() != null) {
            food.setAvailable(request.getAvailable());
        }

        Food updatedFood = foodRepository.save(food);
        return mapToResponse(updatedFood);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodResponse getFoodById(Long id) {
        Food food = findEntityById(id);
        return mapToResponse(food);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodResponse> searchFoodsByName(String name) {
        return foodRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodResponse> getFoodsByCategory(Long categoryId) {
        return foodRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodResponse> getFoodsByRestaurant(Long restaurantId) {
        return foodRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFood(Long id, String userEmail) {
        Food food = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        if (!food.getRestaurant().getOwner().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not authorized to delete food items from this restaurant");
        }

        foodRepository.delete(food);
    }

    @Override
    @Transactional(readOnly = true)
    public Food findEntityById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));
    }

    private FoodResponse mapToResponse(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imageUrl(food.getImageUrl())
                .available(food.getAvailable())
                .restaurantId(food.getRestaurant().getId())
                .restaurantName(food.getRestaurant().getName())
                .categoryId(food.getCategory().getId())
                .categoryName(food.getCategory().getName())
                .build();
    }
}
