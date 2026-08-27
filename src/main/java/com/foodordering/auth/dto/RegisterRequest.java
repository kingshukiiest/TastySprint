package com.foodordering.auth.dto;

import com.foodordering.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request payload for user registration, including optional restaurant details for Restaurant Owners.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private String phone;

    private Role role;

    // Optional fields for RESTAURANT_OWNER registration
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPhone;
    private String restaurantDescription;
}
