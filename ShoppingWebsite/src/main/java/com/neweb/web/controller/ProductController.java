package com.neweb.web.controller;

import com.neweb.web.model.Product;
import com.neweb.web.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String getAllProducts(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Product> productPage = productService.getProductsPaginated(page, 20);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "products";
    }

    @GetMapping("/products/insertFake")
    public String insertFakeProducts() {
        productService.insertFakeProducts();
        return "redirect:/products";
    }
}
