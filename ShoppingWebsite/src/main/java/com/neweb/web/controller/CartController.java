package com.neweb.web.controller;

import com.neweb.web.model.CartItem;
import com.neweb.web.service.CartService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addProductToCart(@RequestParam Long memberId, @RequestParam Long productId, @RequestParam int quantity) {
        logger.debug("Received request to add product to cart - memberId: {}, productId: {}, quantity: {}", memberId, productId, quantity);
        cartService.addProductToCart(memberId, productId, quantity);
        return "redirect:/cart/view?memberId=" + memberId;
    }


    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeProductFromCart(@RequestParam Long memberId, @RequestParam Long productId) {
        logger.debug("Received request to remove product from cart - memberId: {}, productId: {}", memberId, productId);
        cartService.removeProductFromCart(memberId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view")
    @ResponseBody
    public List<CartItem> viewCart(@RequestParam Long memberId) {
        logger.debug("Received request to view cart - memberId: {}", memberId);
        return cartService.getCartItems(memberId);
    }

    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<CartItem> checkCartItem(@RequestParam Long memberId, @RequestParam Long productId) {
        logger.debug("Received request to check cart item - memberId: {}, productId: {}", memberId, productId);
        CartItem cartItem = cartService.findCartItem(memberId, productId);
        return cartItem != null ? ResponseEntity.ok(cartItem) : ResponseEntity.ok(null);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam Long memberId) {
        logger.debug("Received request to clear cart - memberId: {}", memberId);
        cartService.clearCart(memberId);
        return ResponseEntity.ok().build();
    }
}
