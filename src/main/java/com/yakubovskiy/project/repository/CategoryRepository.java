package com.yakubovskiy.project.repository;

import com.yakubovskiy.project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findById(UUID categoryId);

    List<Category> findCategoriesByTitleContainingIgnoreCase(String title);

    Optional<Category> findCategoryByTitle(String title);

}
