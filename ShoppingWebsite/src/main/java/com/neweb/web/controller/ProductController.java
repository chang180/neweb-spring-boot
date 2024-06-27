package com.neweb.web.controller;

import com.neweb.web.model.Product;
import com.neweb.web.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/admin/products")
    public String getAdminProducts(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Product> productPage = productService.getProductsPaginated(page, 20);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", getCategories()); // 添加分类列表
        return "adminProducts";
    }

    @PostMapping("/admin/products/edit")
    public String editProduct(@RequestParam Long id, @RequestParam String name, @RequestParam String description,
                              @RequestParam double price, @RequestParam int stock, @RequestParam String category) {
        productService.editProduct(id, name, description, price, stock, category);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/delete")
    public String deleteProduct(@RequestParam Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/add")
    public String addProduct(@RequestParam String name, @RequestParam String description,
                             @RequestParam double price, @RequestParam int stock, @RequestParam String category) {
        productService.addProduct(name, description, price, stock, category);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/insertFake")
    public String insertFakeProducts() {
        productService.insertFakeProducts();
        return "redirect:/admin/products";
    }

    @GetMapping("/api/products/{id}")
    @ResponseBody
    public Product getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.orElse(null); // Handle product not found case as per your need
    }

    private List<String> getCategories() {
        // 假设这是您的分类列表，可以从数据库或配置文件中获取
        return List.of("Category 1", "Category 2", "Category 3", "Category 4", "Category 5", "Category 6", "Category 7", "Category 8", "Category 9", "Category 10");
    }
}
