package com.yakubovskiy.project.service.interfaces;

import com.yakubovskiy.project.entity.Review;
import com.yakubovskiy.project.entity.User;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    Review findReviewByParentId(UUID parentId);

    List<Review> findReviewsTree(UUID productId);

    List<Review> findAllReviews();

    List<Review> findChildReviews(UUID reviewId);

    Review createReview(User user, UUID productId, String reviewText);

    Review createChildReview(User user, UUID productId, UUID reviewId, String reviewText);

    Review findReviewById(UUID reviewId);

    void deleteReviewById(User user, UUID reviewId);

    void deleteReviewsByProductId(UUID productId);
}
