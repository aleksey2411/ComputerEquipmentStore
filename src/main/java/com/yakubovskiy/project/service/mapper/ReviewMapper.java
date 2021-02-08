package com.yakubovskiy.project.service.mapper;

import com.yakubovskiy.project.dto.ReviewDto;
import com.yakubovskiy.project.entity.Review;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.service.interfaces.MapperService;
import com.yakubovskiy.project.service.interfaces.ReviewService;
import com.yakubovskiy.project.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper implements MapperService<Review, ReviewDto> {
    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public ReviewMapper(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @Override
    public ReviewDto toDto(Review review) {
        Date creationDate = new Date(review.getCreationDate());
        return ReviewDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .product(review.getProduct())
                .username(review.getUser().getEmail())
                .creationDate(creationDate)
                .build();
    }

    @Override
    public Review toEntity(ReviewDto reviewDto) {
        User user = userService.findUserByEmail(reviewDto.getUsername());
        return Review.builder()
                .id(reviewDto.getId())
                .title(reviewDto.getTitle())
                .product(reviewDto.getProduct())
                .user(user)
                .creationDate(reviewDto.getCreationDate().getTime())
                .build();
    }

    public ReviewDto treeToDto(Review review) {
        Date creationDate = new Date(review.getCreationDate());
        System.out.println(review.toString());
        List<ReviewDto> parentReview = reviewService.findChildReviews(review.getId()).stream()
                .map(this::treeToDto)
                .collect(Collectors.toList());
        if (parentReview.isEmpty()) {
            parentReview = null;
        }
        return ReviewDto.builder()
                .id(review.getId())
                .creationDate(creationDate)
                .title(review.getTitle())
                .username(review.getUser().getEmail())
                .answers(parentReview)
                .build();
    }
}
