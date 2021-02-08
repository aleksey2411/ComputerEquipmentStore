package com.yakubovskiy.project.service.impl;

import com.yakubovskiy.project.entity.Cart;
import com.yakubovskiy.project.entity.CartItem;
import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.exception.ResourceNotFoundException;
import com.yakubovskiy.project.repository.CartItemRepository;
import com.yakubovskiy.project.repository.CartRepository;
import com.yakubovskiy.project.repository.ProductRepository;
import com.yakubovskiy.project.service.interfaces.CartItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartRepository cartRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public CartItem addProductToCart(UUID userId, UUID productId, Integer quantity) {
        Product product = productRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with user id " + userId));
        CartItem cartItem = new CartItem();
        Optional<CartItem> cartItemOptional = cartItemRepository.findCartItemByProductId(product.getId());
        if (cartItemOptional.isPresent()) {
            cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setTotalPrice(cartItem.getTotalPrice() + quantity * product.getPrice());
            cart.setGrandTotal(cart.getGrandTotal() + quantity * product.getPrice());
        } else {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setTotalPrice(quantity * product.getPrice());
            cart.setGrandTotal(cart.getGrandTotal() + cartItem.getTotalPrice());
        }
        log.info("{} has been added to cart.", cartItem);
        cartRepository.save(cart);
        cartItemRepository.save(cartItem);
        return cartItem;
    }

    @Override
    public List<CartItem> findCartItemsByUserId(UUID userId) {
        List<CartItem> items = cartItemRepository.findCartItemsByCartUserId(userId);
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with user id " + userId));
        cart.setGrandTotal(0.0);
        items.stream()
                .forEach(item -> {
                    cart.setGrandTotal(cart.getGrandTotal() + item.getTotalPrice());
                });
        if (items.isEmpty()) {
            log.error("{} cart is empty", cart.getUser());
            throw new LogicException("This cart is empty.");
        }
        log.info("All cart items has been received.");
        return items;
    }

    @Override
    public void deleteAllItemsFromCart(UUID userId) {
        List<CartItem> cartItems = findCartItemsByUserId(userId);
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with user id " + userId));
        cart.setGrandTotal(0.0);
        cartItems.stream()
                .forEach(cartItem -> {
                    cartItemRepository.delete(cartItem);
                    log.debug("{} has been removed from cart.", cartItem);
                });
        log.info("All items has been removed from {} cart.", cart.getUser());
    }

    @Transactional
    @Override
    public void deleteItemFromCart(UUID userId, UUID itemId) {
        CartItem cartItem = cartItemRepository.findCartItemById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + itemId));
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with user id " + userId));
        cart.setGrandTotal(cart.getGrandTotal() - cartItem.getTotalPrice());
        cartRepository.save(cart);
        cartItemRepository.delete(cartItem);
    }
}
