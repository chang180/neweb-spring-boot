package com.neweb.web.controller;

import com.neweb.web.model.Cart;
import com.neweb.web.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addProductToCart(@RequestParam Long memberId, @RequestParam Long productId, @RequestParam int quantity) {
        cartService.addProductToCart(memberId, productId, quantity);
        return "redirect:/cart/view?memberId=" + memberId;
    }

    @GetMapping("/view")
    public String viewCart(@RequestParam Long memberId, Model model) {
        List<Cart> cartItems = cartService.getCartItems(memberId);
        model.addAttribute("cartItems", cartItems);
        return "cart";
    }
}
