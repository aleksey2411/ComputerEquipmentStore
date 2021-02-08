package com.yakubovskiy.project.service.interfaces;

import com.yakubovskiy.project.entity.Cart;
import com.yakubovskiy.project.entity.User;

import java.util.List;
import java.util.UUID;

public interface CartService {
    Cart findCartByUserId(User user, UUID userId);

    List<Cart> findAll();
}
