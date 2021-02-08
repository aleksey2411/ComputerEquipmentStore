package com.yakubovskiy.project.repository;

import com.yakubovskiy.project.entity.Order;
import com.yakubovskiy.project.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findOrderByOrderStatus(OrderStatus orderStatus);

    Optional<Order> findOrderById(UUID orderId);
}
