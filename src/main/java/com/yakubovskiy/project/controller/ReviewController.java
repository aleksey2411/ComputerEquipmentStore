package com.yakubovskiy.project.controller;

import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.service.interfaces.ReviewService;
import com.yakubovskiy.project.service.interfaces.UserService;
import com.yakubovskiy.project.service.mapper.ReviewMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/demo/v1")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper, UserService userService) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
        this.userService = userService;
    }

    @ApiOperation(
            value = "Write review",
            notes = "This method allows authorized users add review to product.")
    @PostMapping("/reviews/{productId}/{review}")
    public ResponseEntity<?> createReview(@PathVariable final UUID productId,
                                          @PathVariable final String review) {
        User user = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                reviewMapper.toDto(reviewService.createReview(user, productId, review)));
    }

    @ApiOperation(
            value = "Write reply to review",
            notes = "This method allows authorized users add reply to review.")
    @PostMapping("/reviews/{productId}/{review}/{parentId}")
    public ResponseEntity<?> createChildReview(@PathVariable final UUID productId,
                                               @PathVariable final UUID parentId,
                                               @PathVariable final String review) {
        User user = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                reviewMapper.toDto(reviewService.createChildReview(user, productId, parentId, review)));
    }

    @ApiOperation(
            value = "Find all reviews",
            notes = "This method allows everyone to view all reviews.")
    @GetMapping("/reviews/")
    public ResponseEntity<?> findAllReviews() {
        return ResponseEntity.ok(reviewService.findAllReviews().stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find all reviews to product",
            notes = "This method allows everyone to view all reviews to product.")
    @GetMapping("/reviews/products/{productId}")
    public ResponseEntity<?> findReviewsTree(@PathVariable final UUID productId) {
        return ResponseEntity.ok(reviewService.findReviewsTree(productId).stream()
                .map(reviewMapper::treeToDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find review by id",
            notes = "This method allows everyone to view review by id.")
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<?> findReviewById(@PathVariable final UUID reviewId) {
        return ResponseEntity.ok(reviewMapper.toDto(reviewService.findReviewById(reviewId)));
    }

    @ApiOperation(
            value = "Delete review",
            notes = "This method allows admin delete review by id." +
                    "\n Also this method allows user delete his review.")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable final UUID reviewId) {
        User user = getCurrentUser();
        reviewService.deleteReviewById(user, reviewId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(
            value = "Create review",
            notes = "This method allows admin delete all reviews to product.")
    @DeleteMapping("/reviews/products/{productId}")
    public ResponseEntity<?> deleteReviewsByProductId(@PathVariable final UUID productId) {
        reviewService.deleteReviewsByProductId(productId);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication user = SecurityContextHolder.getContext()
                .getAuthentication();
        return userService.findUserByEmail(user.getName());
    }
}
