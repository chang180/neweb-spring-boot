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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    public void addProductToCart(Long memberId, Long productId, int quantity) {
        logger.debug("Adding product to cart - memberId: {}, productId: {}, quantity: {}", memberId, productId, quantity);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            logger.error("Member not found - memberId: {}", memberId);
            return new RuntimeException("Member not found");
        });

        Product product = productRepository.findById(productId).orElseThrow(() -> {
            logger.error("Product not found - productId: {}", productId);
            return new RuntimeException("Product not found");
        });

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
        logger.debug("Product added to cart successfully - memberId: {}, productId: {}", memberId, productId);
    }

    public void removeProductFromCart(Long memberId, Long productId) {
        logger.debug("Removing product from cart - memberId: {}, productId: {}", memberId, productId);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            logger.error("Member not found - memberId: {}", memberId);
            return new RuntimeException("Member not found");
        });

        Product product = productRepository.findById(productId).orElseThrow(() -> {
            logger.error("Product not found - productId: {}", productId);
            return new RuntimeException("Product not found");
        });

        Cart existingCartItem = cartRepository.findByMemberAndProduct(member, product);
        if (existingCartItem != null) {
            cartRepository.delete(existingCartItem);
        }
        logger.debug("Product removed from cart successfully - memberId: {}, productId: {}", memberId, productId);
    }

    public List<CartItem> getCartItems(Long memberId) {
        logger.debug("Retrieving cart items - memberId: {}", memberId);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            logger.error("Member not found - memberId: {}", memberId);
            return new RuntimeException("Member not found");
        });

        List<Cart> cartItems = cartRepository.findByMember(member);
        List<CartItem> cartItemList = cartItems.stream()
                .map(cart -> new CartItem(cart.getProduct().getName(),
                        cart.getQuantity(),
                        cart.getProduct().getPrice(),
                        cart.getQuantity() * cart.getProduct().getPrice()))
                .collect(Collectors.toList());

        logger.debug("Retrieved cart items successfully - memberId: {}", memberId);
        return cartItemList;
    }

    public CartItem findCartItem(Long memberId, Long productId) {
        logger.debug("Finding cart item - memberId: {}, productId: {}", memberId, productId);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            logger.error("Member not found - memberId: {}", memberId);
            return new RuntimeException("Member not found");
        });

        Product product = productRepository.findById(productId).orElseThrow(() -> {
            logger.error("Product not found - productId: {}", productId);
            return new RuntimeException("Product not found");
        });

        Cart cart = cartRepository.findByMemberAndProduct(member, product);
        if (cart != null) {
            CartItem cartItem = new CartItem(cart.getProduct().getName(),
                    cart.getQuantity(),
                    cart.getProduct().getPrice(),
                    cart.getQuantity() * cart.getProduct().getPrice());
            logger.debug("Found cart item - memberId: {}, productId: {}", memberId, productId);
            return cartItem;
        }

        logger.warn("Cart item not found - memberId: {}, productId: {}", memberId, productId);
        return null;
    }

    public void clearCart(Long memberId) {
        logger.debug("Clearing cart - memberId: {}", memberId);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            logger.error("Member not found - memberId: {}", memberId);
            return new RuntimeException("Member not found");
        });

        List<Cart> cartItems = cartRepository.findByMember(member);
        cartRepository.deleteAll(cartItems);

        logger.debug("Cleared cart successfully - memberId: {}", memberId);
    }
}
