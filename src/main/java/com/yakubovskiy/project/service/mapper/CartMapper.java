package com.yakubovskiy.project.service.mapper;

import com.yakubovskiy.project.dto.CartDto;
import com.yakubovskiy.project.entity.Cart;
import com.yakubovskiy.project.service.interfaces.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CartMapper implements MapperService<Cart, CartDto> {
    private CartItemMapper cartItemMapper;

    @Autowired
    public CartMapper(CartItemMapper cartItemMapper) {
        this.cartItemMapper = cartItemMapper;
    }


    @Override
    public CartDto toDto(Cart cart) {
        return CartDto.builder()
                .id(cart.getId())
                .cartItems(cart.getCartItems().stream()
                        .map(cartItemMapper::toDto)
                        .collect(Collectors.toList()))
                .grandTotal(cart.getGrandTotal())
                .user(cart.getUser())
                .build();
    }

    @Override
    public Cart toEntity(CartDto cartDto) {
        return Cart.builder()
                .id(cartDto.getId())
                .user(cartDto.getUser())
                .cartItems(cartDto.getCartItems().stream()
                        .map(cartItemMapper::toEntity)
                        .collect(Collectors.toList()))
                .grandTotal(cartDto.getGrandTotal())
                .build();
    }
}
