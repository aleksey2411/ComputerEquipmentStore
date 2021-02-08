package com.yakubovskiy.project.service.mapper;

import com.yakubovskiy.project.dto.CartItemDto;
import com.yakubovskiy.project.entity.CartItem;
import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.repository.ProductRepository;
import com.yakubovskiy.project.service.interfaces.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper implements MapperService<CartItem, CartItemDto> {
    private ProductRepository productRepository;

    @Autowired
    public CartItemMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public CartItemDto toDto(CartItem cartItem) {
        return CartItemDto.builder()
                .id(cartItem.getId())
                .productTitle(cartItem.getProduct().getTitle())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getTotalPrice())
                .build();
    }

    @Override
    public CartItem toEntity(CartItemDto cartItemDto) {
        Product product = productRepository.findProductByTitle(cartItemDto.getProductTitle());
        return CartItem.builder()
                .id(cartItemDto.getId())
                .product(product)
                .quantity(cartItemDto.getQuantity())
                .totalPrice(cartItemDto.getTotalPrice())
                .build();
    }
}
