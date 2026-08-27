package com.foodordering.user.dto;

import com.foodordering.user.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for returning safe User details without exposing passwords.
 * Includes restaurant ownership details if the user is a RESTAURANT_OWNER.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private Long restaurantId;
    private String restaurantName;
    private LocalDateTime createdAt;
}
