package com.yakubovskiy.project.repository;

import com.yakubovskiy.project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findProductById(UUID productId);

    List<Product> findProductsByTitleContainingIgnoreCase(String title);

    Product findProductByTitle(String title);

    Optional<Product> findProductByTitleAndModel(String title, String model);
}
