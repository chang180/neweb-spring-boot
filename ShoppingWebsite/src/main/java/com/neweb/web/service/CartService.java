package com.neweb.web.service;

import com.neweb.web.model.Cart;
import com.neweb.web.model.Member;
import com.neweb.web.model.Product;
import com.neweb.web.repository.CartRepository;
import com.neweb.web.repository.MemberRepository;
import com.neweb.web.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    public void addProductToCart(Long memberId, Long productId, int quantity) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Cart cart = new Cart(member, product, quantity);
        cartRepository.save(cart);
    }

    public List<Cart> getCartItems(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }
}
