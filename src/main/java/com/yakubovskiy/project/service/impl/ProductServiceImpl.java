package com.yakubovskiy.project.service.impl;

import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.exception.ResourceNotFoundException;
import com.yakubovskiy.project.repository.ProductRepository;
import com.yakubovskiy.project.service.interfaces.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            log.error("Products not found.");
            throw new LogicException("There are no products in database.");
        }
        log.info("Products has been received.");
        return products;
    }

    @Override
    public Product findProductById(UUID productId) {
        return productRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
    }

    @Override
    public Product addProduct(Product product) {
        if (productRepository.findProductByTitleAndModel(product.getTitle(), product.getModel()).isPresent()) {
            log.error("Error while adding product. {} {} already exist.", product.getTitle(), product.getModel());
            throw new LogicException("This product already exists. Use the update if u need.");
        }
        log.info("{} has been created.", product);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(UUID productId, Product productRequest) {
        return productRepository.findProductById(productId).map(product -> {
            product.setModel(productRequest.getModel());
            product.setTitle(productRequest.getTitle());
            product.setQuantity(productRequest.getQuantity());
            product.setPrice(productRequest.getPrice());
            product.setId(productId);
            log.info("{} has been updated.", product);
            return productRepository.save(product);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
    }

    @Override
    public Product sellProduct(UUID productId, Integer quantity) {
        Product product = findProductById(productId);
        product.setQuantity(product.getQuantity() - quantity);
        log.info("{} '{}' has been sold.", quantity, product.getTitle());
        productRepository.save(product);
        return product;
    }

    @Override
    public void deleteProductById(UUID productId) {
        Product product = findProductById(productId);
        log.info("{} has been removed.", product);
        productRepository.delete(product);
    }

    @Override
    public List<Product> findProductsBySearchQuery(String searchQuery) {
        List<Product> products = productRepository.findProductsByTitleContainingIgnoreCase(searchQuery);
        if (products.isEmpty()) {
            log.error("Products by search query not found: " + searchQuery);
            throw new LogicException("There are no products found by this search query.");
        }
        log.info("Products has been received by search query: " + searchQuery);
        return products;
    }

}
