package com.neweb.web.repository.elasticsearch;

import com.neweb.web.model.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductElasticsearchRepository extends ElasticsearchRepository<Product, Long> {
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description);
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingOrDescriptionContainingAndCategory(String name, String description, String category);
}
