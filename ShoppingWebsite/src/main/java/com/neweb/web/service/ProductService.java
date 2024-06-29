package com.neweb.web.service;

import com.neweb.web.model.Product;
import com.neweb.web.repository.ProductRepository;
import com.neweb.web.repository.elasticsearch.ProductElasticsearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductElasticsearchRepository productElasticsearchRepository;

    public void insertFakeProducts() {
        List<Product> products = new ArrayList<>();
        Random random = new Random();
        String[] additionalWords = {"Amazing", "Quality", "Affordable", "Durable", "Eco-Friendly", "Popular", "Innovative", "Stylish", "Exclusive", "New"};

        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            String additionalName = additionalWords[random.nextInt(additionalWords.length)];
            String additionalDescription = additionalWords[random.nextInt(additionalWords.length)];

            product.setName("Fake Product " + i + " " + additionalName);
            product.setDescription("Description for fake product " + i + ". This product is " + additionalDescription + " and perfect for use.");
            product.setPrice(10.0 * i);
            product.setStock(100);
            product.setCategory("Category " + i);
            products.add(product);
        }

        productRepository.saveAll(products);
        productElasticsearchRepository.saveAll(products); // 同時保存到Elasticsearch
    }

    public Page<Product> getProductsPaginated(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }

    public void editProduct(Long id, String name, String description, double price, int stock, String category) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        productRepository.save(product);
        productElasticsearchRepository.save(product); // 同時保存到Elasticsearch
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
        productElasticsearchRepository.deleteById(productId); // 同時從Elasticsearch刪除
    }

    public void addProduct(String name, String description, double price, int stock, String category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        productRepository.save(product);
        productElasticsearchRepository.save(product); // 同時保存到Elasticsearch
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productElasticsearchRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
    }

       
    public List<Product> searchProductsByKeywordAndCategory(String keyword, String category) {
        return productElasticsearchRepository.findByNameContainingOrDescriptionContainingAndCategory(keyword, keyword, category);
    }

    public List<Product> searchProductsByCategory(String category) {
        return productElasticsearchRepository.findByCategory(category);
    }
}
