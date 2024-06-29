package com.neweb.web.controller;

import com.neweb.web.model.Order;
import com.neweb.web.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public String checkout(@RequestParam Long memberId, Model model) {
        try {
            Order order = orderService.createOrder(memberId);
            model.addAttribute("order", order);
            return "orderDetail"; // 導向訂單詳細頁面
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error"; // 錯誤處理頁面
        }
    }

    @GetMapping
    public String getOrderList(@RequestParam Long memberId, Model model) {
        List<Order> orders = orderService.getOrdersByMember(memberId);
        model.addAttribute("orders", orders);
        return "orderList";
    }

    @GetMapping("/detail")
    public String getOrderDetail(@RequestParam Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "orderDetail";
    }

    @PostMapping("/updateStatus")
    @ResponseBody
    public ResponseEntity<String> updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        try {
            orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok("Order status updated");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelOrder(@RequestParam Long orderId) {
        try {
            orderService.updateOrderStatus(orderId, "Cancelled");
            return ResponseEntity.ok("Order cancelled");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
