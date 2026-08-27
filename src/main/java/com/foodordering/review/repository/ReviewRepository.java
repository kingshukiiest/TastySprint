package com.foodordering.review.repository;

import com.foodordering.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Review queries.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByFoodIdOrderByCreatedAtDesc(Long foodId);

    List<Review> findByUserId(Long userId);
}
