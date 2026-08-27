package com.foodordering.category.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing food categories (e.g., Pizza, Burgers, Beverages, Asian, Desserts).
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}
