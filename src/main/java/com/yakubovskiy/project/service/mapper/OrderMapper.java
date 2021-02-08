package com.yakubovskiy.project.service.mapper;

import com.yakubovskiy.project.dto.OrderDto;
import com.yakubovskiy.project.entity.Order;
import com.yakubovskiy.project.service.interfaces.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class OrderMapper implements MapperService<Order, OrderDto> {
    private OrderItemMapper orderItemMapper;

    @Autowired
    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public OrderDto toDto(Order order) {
        Date creationDate = new Date(order.getCreationDate());
        Date closingDate = new Date(order.getClosingDate());
        return OrderDto.builder()
                .id(order.getId())
                .creationDate(creationDate)
                .closingDate(closingDate)
                .orderStatus(order.getOrderStatus())
                .orderItems(order.getOrderItems().stream()
                        .map(orderItemMapper::toDto)
                        .collect(Collectors.toList()))
                .grandTotal(order.getGrandTotal())
                .user(order.getUser())
                .build();
    }

    @Override
    public Order toEntity(OrderDto orderDto) {
        return Order.builder()
                .id(orderDto.getId())
                .creationDate(orderDto.getCreationDate().getTime())
                .closingDate(orderDto.getClosingDate().getTime())
                .orderStatus(orderDto.getOrderStatus())
                .orderItems(orderDto.getOrderItems().stream()
                        .map(orderItemMapper::toEntity)
                        .collect(Collectors.toList()))
                .grandTotal(orderDto.getGrandTotal())
                .user(orderDto.getUser())
                .build();
    }

}
