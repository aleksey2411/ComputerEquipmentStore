package com.yakubovskiy.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakubovskiy.project.dto.CartDto;
import com.yakubovskiy.project.dto.CategoryDto;
import com.yakubovskiy.project.dto.ProductDto;
import com.yakubovskiy.project.entity.Category;
import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.repository.CategoryRepository;
import com.yakubovskiy.project.repository.ProductRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
public class ProductControllerTest {
    private final UUID id = UUID.randomUUID();
    private final Product product;
    private final Category category;
    private final CategoryDto categoryDto;
    private final ProductDto productDto;
    private final String title;
    private final List<Product> list;
    private final List<ProductDto> listDto;

    public ProductControllerTest() {
        title = "nvidia";
        category = Category.builder().id(id).build();
        categoryDto = CategoryDto.builder().id(id).build();
        product = Product.builder()
                .id(id)
                .category(category)
                .price(0.1)
                .quantity(1)
                .model(title)
                .title(title).build();
        productDto = ProductDto.builder()
                .category(categoryDto)
                .id(id)
                .price(0.1)
                .quantity(1)
                .model(title)
                .title(title).build();
        list = Arrays.asList(product);
        listDto = Arrays.asList(productDto);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @WithMockUser(username = "alex@mail.ru", roles = "admin")

    @Test
    @SneakyThrows
    public void testSearch() {
        when(productRepository.findProductsByTitleContainingIgnoreCase(title)).thenReturn(list);
        mockMvc.perform(get("/demo/v1/products/search/" + title)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", is(product.getTitle())));
    }

    @Test
    @SneakyThrows
    public void testFindAll() {
        when(productRepository.findAll()).thenReturn(list);
        mockMvc.perform(get("/demo/v1/products/")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", is(productDto.getTitle())));
    }

    @Test
    @SneakyThrows
    public void testFindById() {
        when(productRepository.findProductById(id)).thenReturn(Optional.of(product));
        mockMvc.perform(get("/demo/v1/products/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(productDto.getTitle())));
    }

    @Test
    @SneakyThrows
    public void testDelete() {
        when(productRepository.findProductById(id)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);
        mockMvc.perform(delete("/demo/v1/products/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    public void testUpdate() {
        when(productRepository.findProductById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        mockMvc.perform(put("/demo/v1/products/" + id)
                .content(new ObjectMapper().writeValueAsString(productDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(productDto.getTitle())));
    }

    @Test
    @SneakyThrows
    public void testCreate() {
        when(productRepository.save(product)).thenReturn(product);
        mockMvc.perform(post("/demo/v1/products/")
                .content(new ObjectMapper().writeValueAsString(productDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(productDto.getTitle())));
    }
}