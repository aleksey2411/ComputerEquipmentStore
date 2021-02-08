package com.yakubovskiy.project.service.interfaces;

import com.yakubovskiy.project.entity.CartItem;

import java.util.List;
import java.util.UUID;

public interface CartItemService {
    CartItem addProductToCart(UUID userId, UUID productId, Integer quantity);

    void deleteAllItemsFromCart(UUID userId);

    void deleteItemFromCart(UUID userId, UUID itemId);

    List<CartItem> findCartItemsByUserId(UUID userId);
}
