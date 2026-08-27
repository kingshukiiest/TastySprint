package com.foodordering.auth.dto;

import com.foodordering.user.dto.UserResponse;
import lombok.*;

/**
 * Response payload containing generated JWT Bearer token and user details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private UserResponse user;
}
