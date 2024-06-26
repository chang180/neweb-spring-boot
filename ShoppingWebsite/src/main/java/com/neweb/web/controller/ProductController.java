package com.neweb.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

    @GetMapping("/products")
    public String showProductsPage() {
        return "products"; // 返回產品頁面的視圖名稱，即 products.html
    }
}
