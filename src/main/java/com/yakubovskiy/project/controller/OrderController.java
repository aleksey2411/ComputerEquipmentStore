package com.yakubovskiy.project.controller;

import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.OrderStatus;
import com.yakubovskiy.project.service.interfaces.OrderService;
import com.yakubovskiy.project.service.interfaces.UserService;
import com.yakubovskiy.project.service.mapper.OrderMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/demo/v1")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper, UserService userService) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.userService = userService;
    }

    @ApiOperation(
            value = "Make order",
            notes = "This method allows user make order")
    @PostMapping("/orders/")
    public ResponseEntity<?> createOrder() {
        User user = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                orderMapper.toDto(orderService.createOrder(user.getId())));
    }

    @ApiOperation(
            value = "Produce order",
            notes = "This method allows admin approve order")
    @PostMapping("/orders/order-producing-form/{orderId}")
    public ResponseEntity<?> produceOrder(@PathVariable final UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderMapper.toDto(orderService.produceOrder(orderId)));
    }

    @ApiOperation(
            value = "Reject order",
            notes = "This method allows admin reject order")
    @PostMapping("/orders/order-rejecting-form/{orderId}")
    public ResponseEntity<?> rejectOrder(@PathVariable final UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderMapper.toDto(orderService.rejectOrder(orderId)));
    }

    @ApiOperation(
            value = "Show all orders",
            notes = "This method allows admin to view orders list")
    @GetMapping("/orders/")
    public ResponseEntity<?> findAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findAllOrders().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find rejected orders",
            notes = "This method allows admin to view rejected orders")
    @GetMapping("/orders/rejected-orders")
    public ResponseEntity<?> showRejectedOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findOrdersByStatus(OrderStatus.DENIED).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find produced orders",
            notes = "This method allows admin to view produced orders")
    @GetMapping("/orders/produced-orders")
    public ResponseEntity<?> showProducedOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findOrdersByStatus(OrderStatus.PRODUCED).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find rejected orders",
            notes = "This method allows admin to view under consideration orders")
    @GetMapping("orders/under-consideration-orders")
    public ResponseEntity<?> showUnderConsiderationOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(
                orderService.findOrdersByStatus(OrderStatus.UNDER_CONSIDERATION).stream()
                        .map(orderMapper::toDto)
                        .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find order ",
            notes = "This method allows admin to view order by id." +
                    "\n Also this method allows user to view his order.")
    @GetMapping("/orders/id/{orderId}")
    public ResponseEntity<?> findOrderById(@PathVariable final UUID orderId) {
        User user = getCurrentUser();
        return ResponseEntity.ok(orderMapper.toDto(orderService.findOrderById(user, orderId)));
    }

    @ApiOperation(
            value = "Delete order item",
            notes = "This method allows admin delete order item from order")
    @DeleteMapping("/orders/{orderItemId}}")
    public ResponseEntity<?> deleteOrderItemById(@PathVariable final UUID orderItemId) {
        orderService.removeItemFromOrder(orderItemId);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication user = SecurityContextHolder.getContext()
                .getAuthentication();
        return userService.findUserByEmail(user.getName());
    }
}
