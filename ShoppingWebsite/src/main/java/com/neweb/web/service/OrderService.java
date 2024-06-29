package com.neweb.web.service;

import com.neweb.web.model.*;
import com.neweb.web.repository.CartRepository;
import com.neweb.web.repository.MemberRepository;
import com.neweb.web.repository.OrderRepository;
import com.neweb.web.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    public Order createOrder(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        List<Cart> cartItems = cartRepository.findByMember(member);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setMember(member);
        order.setOrderDate(new Date());
        order.setStatus("Pending");
        order.setOrderNumber("order" + memberId + System.currentTimeMillis());

        final double[] totalPrice = {0};
        List<OrderItem> orderItems = cartItems.stream().map(cart -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cart.getProduct());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(cart.getProduct().getPrice() * cart.getQuantity());
            totalPrice[0] += orderItem.getPrice();
            return orderItem;
        }).collect(Collectors.toList());

        order.setTotalPrice(totalPrice[0]);
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        cartRepository.deleteAll(cartItems);

        return order;
    }

    public List<Order> getOrdersByMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        return orderRepository.findByMember(member);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}

