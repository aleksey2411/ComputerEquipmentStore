package com.yakubovskiy.project.service.interfaces;

import com.yakubovskiy.project.entity.Order;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface OrderService {
    Order createOrder(UUID userId);

    Order rejectOrder(UUID orderId);

    Order produceOrder(UUID orderId);

    List<Order> findOrdersByStatus(OrderStatus orderStatus);

    List<Order> findAllOrders();

    void removeItemFromOrder(UUID orderItemId);

    Order findOrderById(User user, UUID orderId);
}
