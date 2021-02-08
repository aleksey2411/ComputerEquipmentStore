package com.yakubovskiy.project.repository;

import com.yakubovskiy.project.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    Optional<OrderItem> findOrderItemById(UUID id);
}
