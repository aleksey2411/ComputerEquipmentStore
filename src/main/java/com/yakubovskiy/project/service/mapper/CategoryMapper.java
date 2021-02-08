package com.yakubovskiy.project.service.mapper;

import com.yakubovskiy.project.dto.CategoryDto;
import com.yakubovskiy.project.entity.Category;
import com.yakubovskiy.project.service.interfaces.MapperService;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements MapperService<Category, CategoryDto> {
    @Override
    public CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .title(category.getTitle())
                .build();
    }

    @Override
    public Category toEntity(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .title(categoryDto.getTitle())
                .build();
    }
}
