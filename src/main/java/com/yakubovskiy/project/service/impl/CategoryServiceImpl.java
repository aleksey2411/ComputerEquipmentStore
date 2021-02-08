package com.yakubovskiy.project.service.impl;

import com.yakubovskiy.project.entity.Category;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.exception.ResourceNotFoundException;
import com.yakubovskiy.project.repository.CategoryRepository;
import com.yakubovskiy.project.service.interfaces.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            log.error("Product categories not found.");
            throw new LogicException("There are no categories in database.");
        }
        log.info("Product categories has been received.");
        return categories;
    }

    @Override
    public Category findCategoryById(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));
        log.info("Product category has been received by id: " + category);
        return category;
    }

    @Override
    public List<Category> findCategoriesBySearchQuery(String searchQuery) {
        List<Category> categories = categoryRepository.findCategoriesByTitleContainingIgnoreCase(searchQuery);
        if (categories.isEmpty()) {
            log.error("Product categories by search query not found: " + searchQuery);
            throw new LogicException("There are no categories found by this search query.");
        }
        log.info("Product categories has been received by search query: " + searchQuery);
        return categories;
    }

    @Override
    public Category updateCategory(UUID categoryId, Category category) {
        Category newCategory = findCategoryById(categoryId);
        newCategory.setTitle(category.getTitle());
        log.info("{} has been updated.", newCategory);
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category addCategory(Category category) {
        if (categoryRepository.findCategoryByTitle(category.getTitle()).isPresent()) {
            log.error("Error while adding category. {} already exist.", category.getTitle());
            throw new LogicException("This category already exists. Use the update if u need.");
        }
        log.info("{} has been created.", category);
        return categoryRepository.save(category);
    }

    @Transactional
    @Override
    public void deleteCategoryById(UUID categoryId) {
        Category category = findCategoryById(categoryId);
        log.info("{} has been removed.", category);
        categoryRepository.delete(category);
    }
}
