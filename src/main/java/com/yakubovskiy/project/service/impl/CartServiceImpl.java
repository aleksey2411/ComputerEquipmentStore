package com.yakubovskiy.project.service.impl;

import com.yakubovskiy.project.entity.Cart;
import com.yakubovskiy.project.entity.CartItem;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.exception.ResourceNotFoundException;
import com.yakubovskiy.project.repository.CartItemRepository;
import com.yakubovskiy.project.repository.CartRepository;
import com.yakubovskiy.project.service.interfaces.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public List<Cart> findAll() {
        log.info("All user's carts has been received.");
        List<Cart> carts = cartRepository.findAll();
        carts.forEach(cart -> {
            List<CartItem> items = cartItemRepository.findCartItemsByCartUserId(cart.getUser().getId());
            cart.setGrandTotal(0.0);
            items.forEach(cartItem -> {
                cart.setGrandTotal(cart.getGrandTotal() + cartItem.getTotalPrice());
            });
        });
        cartRepository.saveAll(carts);
        return carts;
    }

    @Override
    public Cart findCartByUserId(User user, UUID userId) {
        log.info("User's cart has been received by user id: ", userId);
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with user's id " + userId));
        cart.setGrandTotal(0.0);
        List<CartItem> items = cartItemRepository.findCartItemsByCartUserId(userId);
        items.forEach(cartItem -> {
            cart.setGrandTotal(cart.getGrandTotal() + cartItem.getTotalPrice());
        });
        cartRepository.save(cart);
        if (user.getRole().equals(UserRole.USER)) {
            if (!cart.getUser().getId().equals(user.getId())) {
                log.error("Error when trying to view someone else's cart.");
                throw new LogicException("You can't watch other user's cart.");
            }
        }
        log.info("Cart has been received");
        return cart;
    }
}
