package com.foodordering.review.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response payload returning review details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long foodId;
    private String foodName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
