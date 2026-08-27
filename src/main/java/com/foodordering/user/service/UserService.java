package com.foodordering.user.service;

import com.foodordering.user.dto.UserRequest;
import com.foodordering.user.dto.UserResponse;
import com.foodordering.user.entity.User;

import java.util.List;

/**
 * Service interface for managing User entity domain logic.
 */
public interface UserService {

    UserResponse getUserProfileByEmail(String email);

    UserResponse updateUserProfile(String email, UserRequest userRequest);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    User findEntityByEmail(String email);

    User findEntityById(Long id);
}
