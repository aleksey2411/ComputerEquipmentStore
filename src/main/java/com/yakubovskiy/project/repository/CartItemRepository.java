package com.yakubovskiy.project.repository;

import com.yakubovskiy.project.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findCartItemsByCartUserId(UUID userId);

    Optional<CartItem> findCartItemByProductId(UUID productId);

    Optional<CartItem> findCartItemById(UUID itemId);
}
