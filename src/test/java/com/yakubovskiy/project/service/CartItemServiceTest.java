package com.yakubovskiy.project.service;

import com.yakubovskiy.project.entity.Cart;
import com.yakubovskiy.project.entity.CartItem;
import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.repository.CartItemRepository;
import com.yakubovskiy.project.repository.CartRepository;
import com.yakubovskiy.project.repository.ProductRepository;
import com.yakubovskiy.project.service.impl.CartItemServiceImpl;
import com.yakubovskiy.project.service.interfaces.CartItemService;
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
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {CartItemServiceImpl.class})
public class CartItemServiceTest {

    private final CartItem inputItem;
    private final CartItem outputItem;
    private final List<CartItem> list;
    private final UUID id = UUID.randomUUID();
    private final Product product;
    private final Cart cart;

    public CartItemServiceTest() {
        product = Product.builder()
                .id(id)
                .title("NVIDIA").build();
        cart = Cart.builder().id(id).grandTotal(0.0).build();
        inputItem = CartItem.builder()
                .id(id)
                .product(product)
                .cart(cart)
                .quantity(5)
                .totalPrice(0.0).build();
        outputItem = CartItem.builder()
                .product(product)
                .cart(cart)
                .quantity(5)
                .totalPrice(0.0).build();
        list = Arrays.asList(inputItem);
    }

    @MockBean
    private CartItemRepository cartItemRepository;
    @MockBean
    private ProductRepository productRepository;
    @Autowired
    private CartItemService cartItemService;
    @MockBean
    private CartRepository cartRepository;

    @Test
    public void testFindItemsByUserId() {
        when(cartItemRepository.findCartItemsByCartUserId(id)).thenReturn(list);
        when(cartRepository.findCartByUserId(id)).thenReturn(Optional.of(cart));
        assertEquals(list, cartItemService.findCartItemsByUserId(id));
    }

    @Test
    public void testDeleteAllItemsFromCart() {
        when(cartItemRepository.findCartItemsByCartUserId(id)).thenReturn(list);
        when(cartRepository.findCartByUserId(id)).thenReturn(Optional.of(cart));
        doNothing().when(cartItemRepository).delete(inputItem);
        cartItemService.deleteAllItemsFromCart(id);
        verify(cartItemRepository).delete(inputItem);
    }

    @Test
    public void testAddProductToCart() {
        when(productRepository.findProductById(id)).thenReturn(Optional.of(product));
        when(cartRepository.findCartByUserId(id)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findCartItemByProductId(id)).thenReturn(Optional.empty());
        when(cartItemRepository.save(inputItem)).thenReturn(inputItem);
        assertEquals(outputItem, cartItemService.addProductToCart(id, id, 5));
    }

    @Test
    public void testDeleteItemFromCart() {
        when(cartItemRepository.findCartItemById(id)).thenReturn(Optional.of(inputItem));
        when(cartRepository.findCartByUserId(id)).thenReturn(Optional.of(cart));
        doNothing().when(cartItemRepository).delete(inputItem);
        cartItemService.deleteItemFromCart(id, id);
        verify(cartItemRepository, times(1)).delete(inputItem);
    }
}
