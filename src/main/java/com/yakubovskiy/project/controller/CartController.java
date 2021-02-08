package com.yakubovskiy.project.controller;

import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.service.interfaces.CartService;
import com.yakubovskiy.project.service.interfaces.UserService;
import com.yakubovskiy.project.service.mapper.CartMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/demo/v1")
public class CartController {
    private final CartMapper cartMapper;
    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public CartController(CartMapper cartMapper, CartService cartService, UserService userService) {
        this.cartMapper = cartMapper;
        this.cartService = cartService;
        this.userService = userService;
    }
    @ApiOperation(
            value = "Find all carts",
            notes = "This method allows admin to view the list of user's carts")
    @GetMapping("/carts/")
    public ResponseEntity<?> findAllCarts() {
        return ResponseEntity.ok(cartService.findAll().stream()
                .map(cartMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find cart by id",
            notes = "This method allows admin to view user's cart by id." +
                    "\n Also this method allows user to view his user cart.")
    @GetMapping("/carts/{userId}")
    public ResponseEntity<?> findCartByUserId(@PathVariable final UUID userId) {
        User user = getCurrentUser();
        return ResponseEntity.ok(cartMapper.toDto(cartService.findCartByUserId(user, userId)));
    }

    private User getCurrentUser() {
        Authentication user = SecurityContextHolder.getContext()
                .getAuthentication();
        return userService.findUserByEmail(user.getName());
    }
}
