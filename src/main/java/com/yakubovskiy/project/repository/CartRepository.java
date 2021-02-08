package com.yakubovskiy.project.repository;

import com.yakubovskiy.project.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findCartByUserId(UUID userId);
}
