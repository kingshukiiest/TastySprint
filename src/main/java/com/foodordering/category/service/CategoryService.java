package com.foodordering.category.service;

import com.foodordering.category.dto.CategoryRequest;
import com.foodordering.category.dto.CategoryResponse;
import com.foodordering.category.entity.Category;

import java.util.List;

/**
 * Service interface for Category domain operations.
 */
public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    CategoryResponse getCategoryById(Long id);

    List<CategoryResponse> getAllCategories();

    void deleteCategory(Long id);

    Category findEntityById(Long id);
}
