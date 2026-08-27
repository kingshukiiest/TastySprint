package com.foodordering.review.service;

import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.exception.UnauthorizedException;
import com.foodordering.food.entity.Food;
import com.foodordering.food.service.FoodService;
import com.foodordering.review.dto.ReviewRequest;
import com.foodordering.review.dto.ReviewResponse;
import com.foodordering.review.entity.Review;
import com.foodordering.review.repository.ReviewRepository;
import com.foodordering.user.entity.Role;
import com.foodordering.user.entity.User;
import com.foodordering.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing user food reviews and ownership validations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final FoodService foodService;
    private final UserService userService;

    @Override
    public ReviewResponse addReview(ReviewRequest request, String userEmail) {
        User user = userService.findEntityByEmail(userEmail);
        Food food = foodService.findEntityById(request.getFoodId());

        Review review = Review.builder()
                .user(user)
                .food(food)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByFoodId(Long foodId) {
        return reviewRepository.findByFoodIdOrderByCreatedAtDesc(foodId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse updateReview(Long id, ReviewRequest request, String userEmail) {
        Review review = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        if (!review.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this review");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        return mapToResponse(updatedReview);
    }

    @Override
    public void deleteReview(Long id, String userEmail) {
        Review review = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        if (!review.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Review findEntityById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .foodId(review.getFood().getId())
                .foodName(review.getFood().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
