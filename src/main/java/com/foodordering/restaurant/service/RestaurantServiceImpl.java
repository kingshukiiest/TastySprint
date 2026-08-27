package com.foodordering.restaurant.service;

import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.exception.UnauthorizedException;
import com.foodordering.restaurant.dto.RestaurantRequest;
import com.foodordering.restaurant.dto.RestaurantResponse;
import com.foodordering.restaurant.entity.Restaurant;
import com.foodordering.restaurant.repository.RestaurantRepository;
import com.foodordering.user.entity.Role;
import com.foodordering.user.entity.User;
import com.foodordering.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of RestaurantService providing business logic for creation, ownership checks, and updates.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserService userService;

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request, String ownerEmail) {
        User owner = userService.findEntityByEmail(ownerEmail);

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .phone(request.getPhone())
                .rating(0.0)
                .owner(owner)
                .build();

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return mapToResponse(savedRestaurant);
    }

    @Override
    public RestaurantResponse updateRestaurant(Long id, RestaurantRequest request, String userEmail) {
        Restaurant restaurant = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        if (!restaurant.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not authorized to update this restaurant");
        }

        restaurant.setName(request.getName());
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());

        Restaurant updated = restaurantRepository.save(restaurant);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = findEntityById(id);
        return mapToResponse(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRestaurant(Long id, String userEmail) {
        Restaurant restaurant = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        if (!restaurant.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not authorized to delete this restaurant");
        }

        restaurantRepository.delete(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public Restaurant findEntityById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .rating(restaurant.getRating())
                .ownerId(restaurant.getOwner().getId())
                .ownerName(restaurant.getOwner().getName())
                .createdAt(restaurant.getCreatedAt())
                .build();
    }
}
