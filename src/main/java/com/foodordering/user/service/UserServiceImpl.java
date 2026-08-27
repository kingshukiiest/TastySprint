package com.foodordering.user.service;

import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.restaurant.entity.Restaurant;
import com.foodordering.restaurant.repository.RestaurantRepository;
import com.foodordering.user.dto.UserRequest;
import com.foodordering.user.dto.UserResponse;
import com.foodordering.user.entity.Role;
import com.foodordering.user.entity.User;
import com.foodordering.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing User profile operations and lookups.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfileByEmail(String email) {
        User user = findEntityByEmail(email);
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUserProfile(String email, UserRequest userRequest) {
        User user = findEntityByEmail(email);
        user.setName(userRequest.getName());
        if (userRequest.getPhone() != null) {
            user.setPhone(userRequest.getPhone());
        }
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findEntityById(id);
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        if (user.getRole() == Role.RESTAURANT_OWNER) {
            List<Restaurant> restaurants = restaurantRepository.findByOwnerId(user.getId());
            if (!restaurants.isEmpty()) {
                response.setRestaurantId(restaurants.get(0).getId());
                response.setRestaurantName(restaurants.get(0).getName());
            }
        }

        return response;
    }
}
