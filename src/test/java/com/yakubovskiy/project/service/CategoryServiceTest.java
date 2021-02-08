package com.yakubovskiy.project.service;

import com.yakubovskiy.project.entity.Category;
import com.yakubovskiy.project.repository.CategoryRepository;
import com.yakubovskiy.project.service.impl.CategoryServiceImpl;
import com.yakubovskiy.project.service.interfaces.CategoryService;
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
@ContextConfiguration(classes = {CategoryServiceImpl.class})
public class CategoryServiceTest {

    private final Category inputCategory;
    private final Category outputCategory;
    private final List<Category> list;
    private final String searchQuery = "ssd";
    private final String title = "SSD";
    private final UUID id = UUID.randomUUID();

    public CategoryServiceTest() {
        inputCategory = Category.builder()
                .id(id)
                .title(title)
                .build();
        outputCategory = Category.builder()
                .id(id)
                .title(title)
                .build();
        list = Arrays.asList(inputCategory);
    }

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    @Test
    public void testFindAll() {
        when(categoryRepository.findAll()).thenReturn(list);
        assertEquals(list, categoryService.findAllCategories());
    }

    @Test
    public void testFindById() {
        when(categoryRepository.findById(id)).thenReturn(Optional.of(inputCategory));
        assertEquals(outputCategory, categoryService.findCategoryById(id));
    }

    @Test
    public void testAdd() {
        when(categoryRepository.findCategoryByTitle(inputCategory.getTitle())).thenReturn(Optional.empty());
        when(categoryRepository.save(inputCategory)).thenReturn(inputCategory);
        assertEquals(outputCategory, categoryService.addCategory(inputCategory));
    }

    @Test
    public void testUpdate() {
        when(categoryRepository.findById(id)).thenReturn(Optional.of(inputCategory));
        when(categoryRepository.save(inputCategory)).thenReturn(inputCategory);
        assertEquals(outputCategory, categoryService.updateCategory(id, inputCategory));
    }

    @Test
    public void testDelete() {
        when(categoryRepository.findById(id)).thenReturn(Optional.of(inputCategory));
        doNothing().when(categoryRepository).delete(inputCategory);
        categoryService.deleteCategoryById(id);
        verify(categoryRepository).delete(inputCategory);
    }

    @Test
    public void testFindBySearchQuery() {
        when(categoryRepository.findCategoriesByTitleContainingIgnoreCase(searchQuery)).thenReturn(list);
        assertEquals(list, categoryService.findCategoriesBySearchQuery(searchQuery));
    }
}
