package com.yakubovskiy.project.service;

import com.yakubovskiy.project.entity.Cart;
import com.yakubovskiy.project.entity.CartItem;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.repository.CartItemRepository;
import com.yakubovskiy.project.repository.CartRepository;
import com.yakubovskiy.project.service.impl.CartServiceImpl;
import com.yakubovskiy.project.service.interfaces.CartService;
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
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {CartServiceImpl.class})
public class CartServiceTest {

    private final Cart inputCart;
    private final Cart outputCart;
    private final CartItem cartItem;
    private final User user;
    private final List<Cart> list;
    private final UUID id = UUID.randomUUID();

    public CartServiceTest() {
        user = User.builder()
                .id(id)
                .role(UserRole.ADMIN).build();
        cartItem = CartItem.builder()
                .id(id)
                .totalPrice(0.0).build();
        inputCart = Cart.builder()
                .user(user)
                .id(id)
                .cartItems(Arrays.asList(cartItem))
                .grandTotal(0.0).build();
        outputCart = Cart.builder()
                .id(id)
                .user(user)
                .cartItems(Arrays.asList(cartItem))
                .grandTotal(0.0).build();
        list = Arrays.asList(inputCart);
    }

    @Autowired
    private CartService cartService;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private CartItemRepository cartItemRepository;

    @Test
    public void testFindAll() {
        when(cartRepository.findAll()).thenReturn(list);
        assertEquals(list, cartService.findAll());
    }

    @Test
    public void findByUserId() {
        when(cartRepository.findCartByUserId(id)).thenReturn(Optional.of(inputCart));
        assertEquals(inputCart, cartService.findCartByUserId(user, id));
    }

}
