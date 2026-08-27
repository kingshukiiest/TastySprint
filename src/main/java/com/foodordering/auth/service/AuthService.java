package com.foodordering.auth.service;

import com.foodordering.auth.dto.AuthResponse;
import com.foodordering.auth.dto.LoginRequest;
import com.foodordering.auth.dto.RegisterRequest;

/**
 * Service interface for registration and authentication workflow.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
