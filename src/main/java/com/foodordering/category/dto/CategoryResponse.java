package com.foodordering.category.dto;

import lombok.*;

/**
 * Response DTO returning category attributes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
}
