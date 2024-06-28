package com.neweb.web.controller;

import com.neweb.web.model.CartItem;
import com.neweb.web.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeProductFromCart(@RequestParam Long memberId, @RequestParam Long productId) {
        cartService.removeProductFromCart(memberId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view")
    @ResponseBody
    public List<CartItem> viewCart(@RequestParam Long memberId) {
        return cartService.getCartItems(memberId);
    }

    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<CartItem> checkCartItem(@RequestParam Long memberId, @RequestParam Long productId) {
        CartItem cartItem = cartService.findCartItem(memberId, productId);
        return cartItem != null ? ResponseEntity.ok(cartItem) : ResponseEntity.ok(null);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam Long memberId) {
        cartService.clearCart(memberId);
        return ResponseEntity.ok().build();
    }
}
