package com.neweb.web.service;

import com.neweb.web.model.Cart;
import com.neweb.web.model.CartItem;
import com.neweb.web.model.Member;
import com.neweb.web.model.Product;
import com.neweb.web.repository.CartRepository;
import com.neweb.web.repository.MemberRepository;
import com.neweb.web.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    public void addProductToCart(Long memberId, Long productId, int quantity) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Cart existingCartItem = cartRepository.findByMemberAndProduct(member, product);
        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartRepository.save(existingCartItem);
        } else {
            Cart newCartItem = new Cart();
            newCartItem.setMember(member);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            cartRepository.save(newCartItem);
        }
    }

    public void removeProductFromCart(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Cart existingCartItem = cartRepository.findByMemberAndProduct(member, product);
        if (existingCartItem != null) {
            cartRepository.delete(existingCartItem);
        }
    }

    public List<CartItem> getCartItems(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        List<Cart> cartItems = cartRepository.findByMember(member);
        return cartItems.stream()
                .map(cart -> new CartItem(cart.getProduct().getName(),
                        cart.getQuantity(),
                        cart.getProduct().getPrice(),
                        cart.getQuantity() * cart.getProduct().getPrice()))
                .collect(Collectors.toList());
    }

    public CartItem findCartItem(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Cart cart = cartRepository.findByMemberAndProduct(member, product);
        if (cart != null) {
            return new CartItem(cart.getProduct().getName(),
                                cart.getQuantity(),
                                cart.getProduct().getPrice(),
                                cart.getQuantity() * cart.getProduct().getPrice());
        }
        return null;
    }
    
    public void clearCart(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        List<Cart> cartItems = cartRepository.findByMember(member);
        cartRepository.deleteAll(cartItems);
    }
}
