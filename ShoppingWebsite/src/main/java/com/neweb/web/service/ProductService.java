package com.neweb.web.service;

import com.neweb.web.model.Product;
import com.neweb.web.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void insertFakeProducts() {
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setName("Fake Product " + i);
            product.setDescription("Description for fake product " + i);
            product.setPrice(10.0 * i);
            product.setStock(100);
            product.setCategory("Category " + i);
            products.add(product);
        }
        productRepository.saveAll(products);
    }

    public Page<Product> getProductsPaginated(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }
}
