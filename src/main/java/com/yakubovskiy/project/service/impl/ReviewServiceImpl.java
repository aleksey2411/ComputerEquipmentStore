package com.yakubovskiy.project.service.impl;

import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.entity.Review;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.exception.ResourceNotFoundException;
import com.yakubovskiy.project.repository.ProductRepository;
import com.yakubovskiy.project.repository.ReviewRepository;
import com.yakubovskiy.project.repository.UserRepository;
import com.yakubovskiy.project.service.interfaces.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Review findReviewByParentId(UUID parentId) {
        return reviewRepository.findReviewByParentId(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + parentId));
    }

    @Override
    public List<Review> findAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) {
            log.error("Reviews not found.");
            throw new LogicException("There are no reviews in database.");
        }
        log.info("Reviews has been received.");
        return reviews;
    }

    @Override
    public List<Review> findChildReviews(UUID reviewId) {
        List<Review> reviews = reviewRepository.findChildReviews(reviewId);
        log.info("Child reviews has been received.");
        return reviews;
    }

    @Override
    public List<Review> findReviewsTree(UUID productId) {
        List<Review> reviews = reviewRepository.findReviewsByProductId(productId).stream()
                .filter(review -> Objects.isNull(review.getParentId()))
                .collect(Collectors.toList());
        System.out.println(reviews.toString());
        if (reviews.isEmpty()) {
            log.error("Reviews for this product not found.");
            throw new LogicException("There are no reviews for this product in database.");
        }
        return reviews;
    }

    @Override
    public Review createReview(User user, UUID productId, String reviewText) {
        Review review = new Review();
        Product product = productRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        ZonedDateTime zonedNow = ZonedDateTime.now(Clock.system(ZoneId.of("Europe/Moscow")));
        Date date = Date.from(zonedNow.toInstant());
        review.setCreationDate(date.getTime());
        review.setProduct(product);
        review.setUser(user);
        review.setTitle(reviewText);
        log.info("{} has been created.", review);
        reviewRepository.save(review);
        return review;
    }

    @Override
    public Review createChildReview(User user, UUID productId, UUID reviewId, String reviewText) {
        Review review = new Review();
        Product product = productRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        Review parentReview = findReviewByParentId(reviewId);
        ZonedDateTime zonedNow = ZonedDateTime.now(Clock.system(ZoneId.of("Europe/Moscow")));
        Date date = Date.from(zonedNow.toInstant());
        review.setCreationDate(date.getTime());
        System.out.println(product.toString());
        System.out.println(parentReview.getProduct().toString());
        if (!parentReview.getProduct().getId().equals(product.getId())) {
            throw new LogicException("Parent id refers another product!");
        }
        review.setTitle(reviewText);
        review.setProduct(product);
        review.setUser(user);
        review.setParentId(parentReview.getId());
        log.info("Child {} has been created.", review);
        reviewRepository.save(review);
        return review;
    }

    @Override
    public Review findReviewById(UUID reviewId) {
        return reviewRepository.findReviewById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId));
    }

    @Override
    public void deleteReviewById(User user, UUID reviewId) {
        Review review = findReviewById(reviewId);
        if (user.getRole().equals(UserRole.USER)) {
            if (!user.getId().equals(review.getUser().getId())) {
                log.error("Error when trying to delete someone else's review.");
                throw new LogicException("You can't delete other user's review.");
            }
        }
        log.info("{} has been removed.", review);
        reviewRepository.delete(review);
    }


    @Override
    public void deleteReviewsByProductId(UUID productId) {
        List<Review> reviews = findReviewsTree(productId);
        if (reviews.isEmpty()) {
            log.error("Error when trying to delete all product's reviews. Reviews not found.");
            throw new ResourceNotFoundException("There are no reviews for this product.");
        }
        reviews.forEach(review -> {
            log.info("{} has been removed.", review);
            reviewRepository.delete(review);
        });
        log.info("All product's reviews has been removed.");
    }
}
