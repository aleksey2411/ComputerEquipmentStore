package com.yakubovskiy.project.controller;

import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.service.interfaces.CartItemService;
import com.yakubovskiy.project.service.interfaces.UserService;
import com.yakubovskiy.project.service.mapper.CartItemMapper;
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
public class CartItemController {
    private final CartItemService cartItemService;
    private final CartItemMapper cartItemMapper;
    private final UserService userService;

    @Autowired
    public CartItemController(CartItemService cartItemService, CartItemMapper cartItemMapper, UserService userService) {
        this.cartItemService = cartItemService;
        this.cartItemMapper = cartItemMapper;
        this.userService = userService;
    }

    @ApiOperation(
            value = "Find all items",
            notes = "This method allows user to view all items from his cart.")
    @GetMapping("/cart-items/")
    public ResponseEntity<?> findAllItemsByCartId() {
        User user = getCurrentUser();
        return ResponseEntity.ok(cartItemService.findCartItemsByUserId(user.getId()).stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Delete item from cart",
            notes = "This method allows user deletes item from his cart by item id.")
    @DeleteMapping("/cart-items/{itemId}")
    public ResponseEntity<?> deleteItemFromCart(@PathVariable final UUID itemId) {
        User user = getCurrentUser();
        cartItemService.deleteItemFromCart(user.getId(), itemId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(
            value = "Add item to cart",
            notes = "This method allows user adds item to his cart.")
    @PostMapping("/cart-items/{productId}/{quantity}")
    public ResponseEntity<?> addProductToCart(@PathVariable final UUID productId,
                                              @PathVariable final Integer quantity) {
        User user = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                cartItemMapper.toDto(cartItemService.addProductToCart(user.getId(), productId, quantity)));
    }

    @ApiOperation(
            value = "Delete all items from cart",
            notes = "This method allows user deletes all items from his cart.")
    @DeleteMapping("/cart-items/")
    public   ResponseEntity<?> deleteAllProductsFromCart() {
        User user = getCurrentUser();
        cartItemService.deleteAllItemsFromCart(user.getId());
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication user = SecurityContextHolder.getContext()
                .getAuthentication();
        return userService.findUserByEmail(user.getName());
    }
}
