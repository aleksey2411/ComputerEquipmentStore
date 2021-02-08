package com.yakubovskiy.project.service.mapper;

import com.yakubovskiy.project.dto.ProductDto;
import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.service.interfaces.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements MapperService<Product, ProductDto> {
    private CategoryMapper categoryMapper;

    @Autowired
    public ProductMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .model(product.getModel())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .category(categoryMapper.toDto(product.getCategory()))
                .build();
    }

    @Override
    public Product toEntity(ProductDto productDto) {
        return Product.builder()
                .id(productDto.getId())
                .title(productDto.getTitle())
                .model(productDto.getModel())
                .quantity(productDto.getQuantity())
                .price(productDto.getPrice())
                .category(categoryMapper.toEntity(productDto.getCategory()))
                .build();
    }

}
