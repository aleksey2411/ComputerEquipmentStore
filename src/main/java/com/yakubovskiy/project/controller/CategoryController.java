package com.yakubovskiy.project.controller;

import com.yakubovskiy.project.dto.CategoryDto;
import com.yakubovskiy.project.entity.Category;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.service.interfaces.CategoryService;
import com.yakubovskiy.project.service.mapper.CategoryMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/demo/v1")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @ApiOperation(
            value = "Find all categories",
            notes = "This method allows everyone to view the list of categories")
    @GetMapping("/categories/")
    public ResponseEntity<?> findAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Add new category",
            notes = "This method allows admin add new category to database.")
    @PostMapping("/categories/")
    public ResponseEntity<?> addCategory(@RequestBody final CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(categoryMapper.toDto(categoryService.addCategory(category)));
    }

    @ApiOperation(
            value = "Update category",
            notes = "This method allows admin update category")
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable final UUID categoryId,
                                            @RequestBody final CategoryDto categoryDto) {
        Set<ConstraintViolation<CategoryDto>> violations = validate(categoryDto);
        for (ConstraintViolation<CategoryDto> violation : violations) {
            throw new LogicException("Incorrect data. Try again.\n" + violation.getMessage());
        }
        Category category = categoryMapper.toEntity(categoryDto);
        return ResponseEntity.ok(categoryMapper.toDto(categoryService.updateCategory(categoryId, category)));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<?> findCategoryById(@PathVariable final UUID categoryId) {
        return ResponseEntity.ok(categoryMapper.toDto(categoryService.findCategoryById(categoryId)));
    }

    @ApiOperation(
            value = "Delete category",
            notes = "This method allows admin delete category")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable final UUID categoryId) {
        categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(
            value = "Find category by search query",
            notes = "This method allows everyone to find categories by search query")
    @GetMapping("/categories/search/{searchQuery}")
    public ResponseEntity<?> findCategoriesBySearchQuery(@PathVariable final String searchQuery) {
        return ResponseEntity.ok(categoryService.findCategoriesBySearchQuery(searchQuery).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList()));
    }

    private Set<ConstraintViolation<CategoryDto>> validate(CategoryDto categoryDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(categoryDto);
    }
}
