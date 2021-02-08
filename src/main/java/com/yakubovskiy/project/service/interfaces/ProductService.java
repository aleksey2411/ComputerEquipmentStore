package com.yakubovskiy.project.service.interfaces;

import com.yakubovskiy.project.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<Product> findAllProducts();

    Product findProductById(UUID productId);

    Product addProduct(Product product);

    Product updateProduct(UUID productId, Product productRequest);

    Product sellProduct(UUID productId, Integer quantity);

    void deleteProductById(UUID productId);

    List<Product> findProductsBySearchQuery(String searchQuery);
}
