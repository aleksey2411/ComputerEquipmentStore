package com.yakubovskiy.project.repository;

import com.yakubovskiy.project.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findReviewsByProductId(UUID productId);

    Optional<Review> findReviewById(UUID id);

    @Query(value = "select * from product_review pr where pr.product_review_id=:parentId", nativeQuery = true)
    Optional<Review> findReviewByParentId(@Param("parentId") UUID parentId);

    @Query(value = "select * from product_review pr where pr.parent_id=:reviewId", nativeQuery = true)
    List<Review> findChildReviews(@Param("reviewId") UUID reviewId);
}
