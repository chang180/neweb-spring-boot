package com.neweb.web.repository;

import com.neweb.web.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryAndNameContainingOrDescriptionContaining(String category, String name, String description);
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description);
}
