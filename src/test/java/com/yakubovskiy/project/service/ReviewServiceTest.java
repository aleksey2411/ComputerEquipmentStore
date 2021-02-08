package com.yakubovskiy.project.service;

import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.entity.Review;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.repository.ProductRepository;
import com.yakubovskiy.project.repository.ReviewRepository;
import com.yakubovskiy.project.repository.UserRepository;
import com.yakubovskiy.project.service.impl.ReviewServiceImpl;
import com.yakubovskiy.project.service.interfaces.ReviewService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {ReviewServiceImpl.class})
public class ReviewServiceTest {

    private Review review;
    private Review childReview;
    private final List<Review> reviewList;
    private final List<Review> childReviewList;
    private final Product product;
    private final User user;
    private final UUID id = UUID.randomUUID();
    private final UUID childId = UUID.randomUUID();
    private final String title = "review";

    public ReviewServiceTest() {
        product = Product.builder().id(id).build();
        user = User.builder()
                .id(id)
                .role(UserRole.ADMIN).build();
        review = Review.builder()
                .id(id)
                .product(product)
                .user(user)
                .title(title).build();
        childReview = Review.builder()
                .id(childId)
                .parentId(id)
                .product(product)
                .user(user)
                .title(title).build();
        reviewList = Arrays.asList(review);
        childReviewList = Arrays.asList(childReview);
    }

    @Autowired
    private ReviewService reviewService;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @Test
    public void testFindReviewById() {
        when(reviewRepository.findReviewById(id)).thenReturn(Optional.of(review));
        assertEquals(review, reviewService.findReviewById(id));
    }

    @Test
    public void testFindReviewByParentId() {
        when(reviewRepository.findReviewByParentId(id)).thenReturn(Optional.of(review));
        assertEquals(review, reviewService.findReviewByParentId(id));
    }

    @Test
    public void testFindReviewsTree() {
        when(reviewRepository.findReviewsByProductId(id)).thenReturn(reviewList);
        assertEquals(reviewList, reviewService.findReviewsTree(id));
    }

    @Test
    public void testFindAllReviews() {
        when(reviewRepository.findAll()).thenReturn(reviewList);
        assertEquals(reviewList, reviewService.findAllReviews());
    }

    @Test
    public void testFindChildReviews() {
        when(reviewRepository.findChildReviews(id)).thenReturn(childReviewList);
        assertEquals(childReviewList, reviewService.findChildReviews(id));
    }

    @Test
    public void testCreateReview() {
        when(productRepository.findProductById(id)).thenReturn(Optional.of(product));
        Review outputReview = reviewService.createReview(user, id, title);
        assertTrue(outputReview.getTitle().equals(review.getTitle()));
    }

    @Test
    public void testCreateChildReview() {
        when(productRepository.findProductById(id)).thenReturn(Optional.of(product));
        when(reviewRepository.findReviewByParentId(childReview.getParentId())).thenReturn(Optional.of(review));
        Review outputChildReview = reviewService.createChildReview(user, id, id, title);
        assertTrue(outputChildReview.getTitle().equals(childReview.getTitle()));
    }

    @Test
    public void testDeleteReviewById() {
        when(reviewRepository.findReviewById(id)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);
        reviewService.deleteReviewById(user, id);
        verify(reviewRepository).delete(review);
    }

    @Test
    public void testDeleteReviewsByProduct() {
        when(reviewRepository.findReviewsByProductId(id)).thenReturn(reviewList);
        doNothing().when(reviewRepository).delete(review);
        reviewService.deleteReviewsByProductId(id);
        verify(reviewRepository).delete(review);
    }
}
