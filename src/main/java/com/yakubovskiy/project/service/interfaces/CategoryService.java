package com.yakubovskiy.project.service.interfaces;

import com.yakubovskiy.project.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CategoryService {
    List<Category> findAllCategories();

    Category findCategoryById(UUID categoryId);

    List<Category> findCategoriesBySearchQuery(String searchQuery);

    Category updateCategory(UUID categoryId, Category category);

    Category addCategory(Category category);

    void deleteCategoryById(UUID categoryId);
}
