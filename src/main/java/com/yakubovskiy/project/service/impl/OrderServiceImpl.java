package com.yakubovskiy.project.service.impl;

import com.yakubovskiy.project.entity.*;
import com.yakubovskiy.project.enums.OrderStatus;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.exception.ResourceNotFoundException;
import com.yakubovskiy.project.repository.*;
import com.yakubovskiy.project.service.interfaces.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                            CartItemRepository cartItemRepository, CartRepository cartRepository,
                            UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Order createOrder(UUID userId) {
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with user's id " + userId));
        User user = cart.getUser();
        List<CartItem> cartItem = cartItemRepository.findCartItemsByCartUserId(userId);
        if (cartItem.isEmpty()) {
            log.error("Error while making order. This cart is empty.");
            throw new LogicException("This cart is empty.");
        }
        if(user.getBalance()<cart.getGrandTotal()) {
            log.error("Error while making order. Not enough money.");
            throw new LogicException("You don't have enough money.");
        }
        Order order = new Order();
        ZonedDateTime zonedNow = ZonedDateTime.now(Clock.system(ZoneId.of("Europe/Minsk")));
        Date date = Date.from(zonedNow.toInstant());
        order.setCreationDate(date.getTime());
        order.setClosingDate(date.getTime());
        order.setOrderStatus(OrderStatus.UNDER_CONSIDERATION);
        order.setUser(cart.getUser());
        order.setGrandTotal(0.0);
        orderRepository.save(order);
        List<OrderItem> orderItems = new ArrayList<>();
        cartItem.forEach(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(item.getTotalPrice());
            orderItemRepository.save(orderItem);
            log.info("{} has been added.", orderItem);
            order.setGrandTotal(order.getGrandTotal() + item.getTotalPrice());
            cartItemRepository.delete(item);
            log.info("{} has been removed from cart.", item);
            orderItems.add(orderItem);
        });
        cart.setGrandTotal(0.0);
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        log.info("{} has been created.", order);
        return order;
    }

    @Override
    public void removeItemFromOrder(UUID orderItemId) {
        OrderItem orderItem = orderItemRepository.findOrderItemById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id " + orderItemId));
        log.info("{} has been removed from order", orderItem);
        orderItemRepository.delete(orderItem);
    }

    @Override
    public Order rejectOrder(UUID orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getOrderStatus().equals(OrderStatus.UNDER_CONSIDERATION)) {
            order.setOrderStatus(OrderStatus.DENIED);
            log.info("{} has been rejected.", order);
            orderRepository.saveAndFlush(order);
            return order;
        } else {
            log.error("Error while reject {}", order);
            throw new LogicException("This order has been considered!");
        }
    }

    @Transactional
    @Override
    public Order produceOrder(UUID orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getOrderItems().isEmpty()) {
            throw new ResourceNotFoundException("There not items");
        }
        Double grandTotal = order.getGrandTotal();
        order.setGrandTotal(0.0);
        if (order.getOrderStatus().equals(OrderStatus.UNDER_CONSIDERATION)) {
            order.getOrderItems().forEach(orderItem -> {
                if (orderItem.getQuantity() > orderItem.getProduct().getQuantity()) {
                    log.error("Error while produce {}. Not enough item {}", order, orderItem.getProduct());
                    throw new LogicException("Not enough products in stock " + orderItem.getProduct());
                }
                order.setGrandTotal(order.getGrandTotal() + orderItem.getTotalPrice());
                orderItem.getProduct().setQuantity(orderItem.getProduct().getQuantity() - orderItem.getQuantity());
                log.info("{} has been sold. ", orderItem.getProduct());
            });
            if (!order.getGrandTotal().equals(grandTotal)) {
                log.error("Error while producing {}. One of the order items has been removed", order);
                throw new LogicException("One of the order items has been removed");
            }
            order.setOrderStatus(OrderStatus.PRODUCED);
            ZonedDateTime zonedNow = ZonedDateTime.now(Clock.system(ZoneId.of("Europe/Minsk")));
            Date date = Date.from(zonedNow.toInstant());
            order.setClosingDate(date.getTime());
            log.info("{} has been produced.", order);
            User user = order.getUser();
            user.setBalance(user.getBalance()-order.getGrandTotal());
            log.info("Money has been debited from the account");
            userRepository.saveAndFlush(user);
            orderRepository.saveAndFlush(order);
            return order;
        } else {
            log.error("Error while producing {}", order);
            throw new LogicException("This order has been considered!");
        }
    }

    @Override
    public List<Order> findAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            log.error("Orders not found.");
            throw new LogicException("There are no orders in database.");
        }
        log.info("Orders has been received.");
        return orders;
    }

    @Override
    public Order findOrderById(User user, UUID orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (user.getRole().equals(UserRole.USER)) {
            if (!order.getUser().getId().equals(user.getId())) {
                log.error("Error when trying to view someone else's order.");
                throw new LogicException("You can't watch other user's orders.");
            }
        }
        log.info("Order has been received.");
        return order;
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatus orderStatus) {
        List<Order> orders = orderRepository.findOrderByOrderStatus(orderStatus);
        if (orders.isEmpty()) {
            log.error("Orders with status {} not found.", orderStatus);
            throw new LogicException("There are no orders with status " + orderStatus);
        }
        log.info("Orders with status {} has been received.", orderStatus);
        return orders;
    }
}
