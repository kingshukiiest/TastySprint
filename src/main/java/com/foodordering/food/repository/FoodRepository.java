package com.foodordering.food.repository;

import com.foodordering.food.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Food entity queries and filtering.
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantId(Long restaurantId);

    List<Food> findByCategoryId(Long categoryId);

    List<Food> findByNameContainingIgnoreCase(String name);

    List<Food> findByRestaurantIdAndAvailableTrue(Long restaurantId);
}
