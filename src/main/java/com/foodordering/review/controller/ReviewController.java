package com.foodordering.review.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.review.dto.ReviewRequest;
import com.foodordering.review.dto.ReviewResponse;
import com.foodordering.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing endpoints for creating, retrieving, updating, and deleting food item reviews.
 */
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            Authentication authentication,
            @Valid @RequestBody ReviewRequest request) {
        String userEmail = authentication.getName();
        ReviewResponse response = reviewService.addReview(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review added successfully", response));
    }

    @GetMapping("/api/foods/{foodId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByFoodId(@PathVariable Long foodId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByFoodId(foodId);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", reviews));
    }

    @PutMapping("/api/reviews/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody ReviewRequest request) {
        String userEmail = authentication.getName();
        ReviewResponse response = reviewService.updateReview(id, request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", response));
    }

    @DeleteMapping("/api/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        reviewService.deleteReview(id, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}
