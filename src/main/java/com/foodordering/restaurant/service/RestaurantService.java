package com.foodordering.restaurant.service;

import com.foodordering.restaurant.dto.RestaurantRequest;
import com.foodordering.restaurant.dto.RestaurantResponse;
import com.foodordering.restaurant.entity.Restaurant;

import java.util.List;

/**
 * Service interface for Restaurant management operations.
 */
public interface RestaurantService {

    RestaurantResponse createRestaurant(RestaurantRequest request, String ownerEmail);

    RestaurantResponse updateRestaurant(Long id, RestaurantRequest request, String userEmail);

    RestaurantResponse getRestaurantById(Long id);

    List<RestaurantResponse> getAllRestaurants();

    void deleteRestaurant(Long id, String userEmail);

    Restaurant findEntityById(Long id);
}
