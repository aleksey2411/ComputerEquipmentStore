package com.yakubovskiy.project.controller;

import com.yakubovskiy.project.dto.ProductDto;
import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.service.interfaces.ProductService;
import com.yakubovskiy.project.service.mapper.ProductMapper;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/demo/v1")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @ApiOperation(
            value = "Add new product",
            notes = "This method allows admin add new product to database.")
    @PostMapping("/products/")
    public ResponseEntity<?> addProduct(@RequestBody final ProductDto productDto) {
        Set<ConstraintViolation<ProductDto>> violations = validate(productDto);
        for (ConstraintViolation<ProductDto> violation : violations) {
            throw new LogicException("Incorrect data. Try again.\n" + violation.getMessage());
        }
        Product product = productMapper.toEntity(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                productMapper.toDto(productService.addProduct(product)));
    }

    @ApiOperation(
            value = "Find all products",
            notes = "This method allows everyone to view the list of products")
    @GetMapping("/products/")
    public ResponseEntity<?> findAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Update product",
            notes = "This method allows admin update product")
    @PutMapping("/products/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable final UUID productId,
                                           @RequestBody final ProductDto productDto) {
        Set<ConstraintViolation<ProductDto>> violations = validate(productDto);
        for (ConstraintViolation<ProductDto> violation : violations) {
            throw new LogicException("Incorrect data. Try again.\n" + violation.getMessage());
        }
        Product product = productMapper.toEntity(productDto);
        return ResponseEntity.ok(productService.updateProduct(productId, product));
    }

    @ApiOperation(
            value = "Find product by id",
            notes = "This method allows everyone to view product by id")
    @GetMapping("/products/{productId}")
    public ResponseEntity<?> findProductById(@PathVariable final UUID productId) {
        return ResponseEntity.ok(productMapper.toDto(productService.findProductById(productId)));
    }

    @ApiOperation(
            value = "Find product by search query",
            notes = "This method allows everyone to find products by search query")
    @GetMapping("/products/search/{searchQuery}")
    public ResponseEntity<?> findProductBySearchQuery(@PathVariable final String searchQuery) {
        return ResponseEntity.ok(productService.findProductsBySearchQuery(searchQuery).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Delete product",
            notes = "This method allows admin delete product")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable final UUID productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }

    private Set<ConstraintViolation<ProductDto>> validate(ProductDto productDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(productDto);
    }
}