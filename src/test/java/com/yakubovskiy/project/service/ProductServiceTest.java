package com.yakubovskiy.project.service;

import com.yakubovskiy.project.entity.Category;
import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.repository.ProductRepository;
import com.yakubovskiy.project.service.impl.ProductServiceImpl;
import com.yakubovskiy.project.service.interfaces.ProductService;
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
@ContextConfiguration(classes = {ProductServiceImpl.class})
public class ProductServiceTest {

    private final UUID id = UUID.randomUUID();
    private final String title = "ASUS";
    private final String searchQuery = "asus";
    private final Category category;
    private Product inputProduct;
    private Product outputProduct;
    private final List<Product> list;

    public ProductServiceTest() {
        category = Category.builder()
                .id(id)
                .title("SSD").build();
        inputProduct = Product.builder()
                .id(id)
                .title(title)
                .category(category)
                .quantity(10)
                .price(100).build();
        outputProduct = Product.builder()
                .id(id)
                .title(title)
                .category(category)
                .quantity(10)
                .price(100).build();
        list = Arrays.asList(inputProduct);
    }

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @Test
    public void testFindAll() {
        when(productRepository.findAll()).thenReturn(list);
        assertEquals(list, productService.findAllProducts());
    }

    @Test
    public void testFindById() {
        when(productRepository.findProductById(id)).thenReturn(Optional.of(inputProduct));
        assertEquals(outputProduct, productService.findProductById(id));
    }

    @Test
    public void testAdd() {
        when(productRepository.findProductByTitleAndModel(
                inputProduct.getTitle(), inputProduct.getModel())).thenReturn(Optional.empty());
        when(productRepository.save(inputProduct)).thenReturn(inputProduct);
        assertEquals(outputProduct, productService.addProduct(inputProduct));
    }

    @Test
    public void testUpdate() {
        when(productRepository.findProductById(id)).thenReturn(Optional.of(inputProduct));
        when(productRepository.save(inputProduct)).thenReturn(inputProduct);
        assertEquals(outputProduct, productService.updateProduct(id, inputProduct));
    }

    @Test
    public void testSell() {
        int quantity = 10;
        outputProduct.setQuantity(inputProduct.getQuantity() - quantity);
        when(productRepository.findProductById(id)).thenReturn(Optional.of(inputProduct));
        when(productRepository.save(inputProduct)).thenReturn(inputProduct);
        assertEquals(outputProduct, productService.sellProduct(id, quantity));
    }

    @Test
    public void testDelete() {
        when(productRepository.findProductById(id)).thenReturn(Optional.of(inputProduct));
        doNothing().when(productRepository).delete(inputProduct);
        productService.deleteProductById(id);
        verify(productRepository).delete(inputProduct);
    }

    @Test
    public void testFindBySearchQuery() {
        when(productRepository.findProductsByTitleContainingIgnoreCase(searchQuery)).thenReturn(list);
        assertEquals(list, productService.findProductsBySearchQuery(searchQuery));
    }
}
