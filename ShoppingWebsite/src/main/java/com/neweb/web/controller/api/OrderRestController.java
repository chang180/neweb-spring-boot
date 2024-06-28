package com.neweb.web.controller.api;

import com.neweb.web.model.Order;
import com.neweb.web.model.Member;
import com.neweb.web.service.OrderService;
import com.neweb.web.service.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Long memberId = userDetails.getId();
        Order order = orderService.createOrder(memberId);
        return ResponseEntity.ok(order);
    }
}
