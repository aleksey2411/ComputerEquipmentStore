package com.yakubovskiy.project.service;

import com.yakubovskiy.project.entity.*;
import com.yakubovskiy.project.enums.OrderStatus;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.repository.*;
import com.yakubovskiy.project.service.impl.OrderServiceImpl;
import com.yakubovskiy.project.service.interfaces.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {OrderServiceImpl.class})
public class OrderServiceTest {

    private final OrderStatus orderStatus = OrderStatus.UNDER_CONSIDERATION;
    private final Order inputOrder;
    private Order outputOrder;
    private final Product product;
    private final OrderItem orderItem;
    private final CartItem cartItem;
    private final User user;
    private final Cart cart;
    private final List<Order> list;
    private final UUID id = UUID.randomUUID();

    public OrderServiceTest() {
        user = User.builder()
                .id(id)
                .balance(100.0).build();
        product = Product.builder()
                .id(id)
                .quantity(100).build();
        cartItem = CartItem.builder()
                .id(id)
                .totalPrice(0.0).build();
        cart = Cart.builder()
                .user(user)
                .id(id)
                .grandTotal(50.0)
                .cartItems(Arrays.asList(cartItem)).build();
        orderItem = OrderItem.builder()
                .id(id)
                .quantity(10)
                .product(product)
                .totalPrice(0.0).build();
        inputOrder = Order.builder()
                .user(user)
                .id(id)
                .grandTotal(0.0)
                .orderItems(Arrays.asList(orderItem))
                .orderStatus(orderStatus).build();
        outputOrder = Order.builder()
                .user(user)
                .id(id)
                .grandTotal(0.0)
                .orderItems(Arrays.asList(orderItem))
                .orderStatus(orderStatus).build();
        list = Arrays.asList(inputOrder);
    }

    @Autowired
    private OrderService orderService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private OrderItemRepository orderItemRepository;
    @MockBean
    private CartItemRepository cartItemRepository;
    @MockBean
    private CartRepository cartRepository;
    @MockBean
    private ProductRepository productRepository;

    @Test
    public void testReject() {
        outputOrder.setOrderStatus(OrderStatus.DENIED);
        when(orderRepository.findOrderById(id)).thenReturn(Optional.of(inputOrder));
        when(orderRepository.saveAndFlush(inputOrder)).thenReturn(inputOrder);
        assertEquals(outputOrder, orderService.rejectOrder(id));
    }

    @Test
    public void testProduce() {
        outputOrder.setOrderStatus(OrderStatus.PRODUCED);
        when(orderRepository.findOrderById(id)).thenReturn(Optional.of(inputOrder));
        when(orderRepository.saveAndFlush(inputOrder)).thenReturn(inputOrder);
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        Order newOrder = orderService.produceOrder(id);
        assertTrue(newOrder.getOrderStatus().equals(OrderStatus.PRODUCED));
    }

    @Test
    public void testCreate() {
        when(cartItemRepository.findCartItemsByCartUserId(id)).thenReturn(Arrays.asList(cartItem));
        when(cartRepository.findCartByUserId(id)).thenReturn(Optional.of(cart));
        when(orderRepository.save(inputOrder)).thenReturn(inputOrder);
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        doNothing().when(cartItemRepository).delete(cartItem);
        Order newOrder = orderService.createOrder(id);
        assertTrue(outputOrder.getOrderStatus().equals(newOrder.getOrderStatus()));
    }

    @Test
    public void testFindByStatus() {
        when(orderRepository.findOrderByOrderStatus(orderStatus)).thenReturn(list);
        assertEquals(list, orderService.findOrdersByStatus(orderStatus));
    }

    @Test
    public void testFindAll() {
        when(orderRepository.findAll()).thenReturn(list);
        assertEquals(list, orderService.findAllOrders());
    }

    @Test
    public void testFindById() {
        User user = User.builder()
                .role(UserRole.ADMIN).build();
        when(orderRepository.findOrderById(id)).thenReturn(Optional.of(inputOrder));
        assertEquals(outputOrder, orderService.findOrderById(user, id));
    }

    @Test
    public void testDelete() {
        when(orderItemRepository.findOrderItemById(id)).thenReturn(Optional.of(orderItem));
        doNothing().when(orderItemRepository).delete(orderItem);
        orderService.removeItemFromOrder(id);
        verify(orderItemRepository).delete(orderItem);
    }
}
