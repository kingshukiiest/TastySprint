package com.foodordering.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request payload for creating or updating food category definitions.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}
