package com.yakubovskiy.project.service.mapper;

import com.yakubovskiy.project.dto.OrderItemDto;
import com.yakubovskiy.project.entity.OrderItem;
import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.repository.ProductRepository;
import com.yakubovskiy.project.service.interfaces.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper implements MapperService<OrderItem, OrderItemDto> {
    private ProductRepository productRepository;

    @Autowired
    public OrderItemMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public OrderItemDto toDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .productTitle(orderItem.getProduct().getTitle())
                .quantity(orderItem.getQuantity())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }

    @Override
    public OrderItem toEntity(OrderItemDto orderItemDto) {
        Product product = productRepository.findProductByTitle(orderItemDto.getProductTitle());
        return OrderItem.builder()
                .id(orderItemDto.getId())
                .product(product)
                .quantity(orderItemDto.getQuantity())
                .totalPrice(orderItemDto.getTotalPrice())
                .build();
    }

}
