package com.foodordering.auth.service;

import com.foodordering.auth.dto.AuthResponse;
import com.foodordering.auth.dto.LoginRequest;
import com.foodordering.auth.dto.RegisterRequest;
import com.foodordering.exception.BadRequestException;
import com.foodordering.restaurant.entity.Restaurant;
import com.foodordering.restaurant.repository.RestaurantRepository;
import com.foodordering.security.JwtService;
import com.foodordering.user.dto.UserResponse;
import com.foodordering.user.entity.Role;
import com.foodordering.user.entity.User;
import com.foodordering.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation handling user authentication, BCrypt registration, and JWT token issuance.
 * Automatically provisions Restaurant entities when a RESTAURANT_OWNER registers with restaurant details.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        Role userRole = request.getRole() != null ? request.getRole() : Role.CUSTOMER;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(userRole)
                .build();

        User savedUser = userRepository.save(user);

        // Provision restaurant if RESTAURANT_OWNER registers with restaurant details
        if (userRole == Role.RESTAURANT_OWNER && request.getRestaurantName() != null && !request.getRestaurantName().isBlank()) {
            Restaurant restaurant = Restaurant.builder()
                    .name(request.getRestaurantName())
                    .description(request.getRestaurantDescription() != null ? request.getRestaurantDescription() : "Delicious quality food")
                    .address(request.getRestaurantAddress() != null ? request.getRestaurantAddress() : "Main City Branch")
                    .phone(request.getRestaurantPhone() != null ? request.getRestaurantPhone() : savedUser.getPhone())
                    .owner(savedUser)
                    .build();

            restaurantRepository.save(restaurant);
        }

        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(savedUser))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
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
