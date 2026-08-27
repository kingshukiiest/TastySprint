package com.foodordering.review.service;

import com.foodordering.review.dto.ReviewRequest;
import com.foodordering.review.dto.ReviewResponse;
import com.foodordering.review.entity.Review;

import java.util.List;

/**
 * Service interface for managing food reviews.
 */
public interface ReviewService {

    ReviewResponse addReview(ReviewRequest request, String userEmail);

    List<ReviewResponse> getReviewsByFoodId(Long foodId);

    ReviewResponse updateReview(Long id, ReviewRequest request, String userEmail);

    void deleteReview(Long id, String userEmail);

    Review findEntityById(Long id);
}
