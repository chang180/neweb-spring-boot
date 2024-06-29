package com.neweb.web.controller;

import com.neweb.web.model.Order;
import com.neweb.web.service.NewebPayService;
import com.neweb.web.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private NewebPayService newebPayService;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestParam Long memberId, @RequestParam String email) {
        try {
            Order order = orderService.createOrder(memberId);
            // 返回訂單詳細頁面的URL
            String orderDetailUrl = "/orders/detail?id=" + order.getId();
            return ResponseEntity.ok(orderDetailUrl);
        } catch (RuntimeException e) {
            log.error("Checkout failed for memberId {}: {}", memberId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Checkout failed: " + e.getMessage());
        }
    }

    @GetMapping
    public String getOrderList(@RequestParam Long memberId, Model model, RedirectAttributes redirectAttributes) {
        List<Order> orders = orderService.getOrdersByMember(memberId);
        if (orders.isEmpty()) {
            redirectAttributes.addFlashAttribute("warning", "No orders found for the member.");
            return "redirect:/home";
        }
        model.addAttribute("orders", orders);
        return "orderList";
    }

    @GetMapping("/detail")
    public String getOrderDetail(@RequestParam Long id, Model model) {
        try {
            Order order = orderService.getOrderById(id);
            Map<String, String> paymentRequest = newebPayService.createPaymentRequest(
                    order.getOrderNumber(), 
                    order.getTotalPrice(), 
                    order.getOrderItems().get(0).getProduct().getName(), 
                    order.getMember().getEmail()
            );
            Map<String, String> originalParams = newebPayService.createOriginalParams(
                    order.getOrderNumber(), 
                    order.getTotalPrice(), 
                    order.getOrderItems().get(0).getProduct().getName()
            );
            model.addAttribute("order", order);
            model.addAttribute("paymentRequest", paymentRequest);
            model.addAttribute("originalParams", originalParams);
            model.addAttribute("key", newebPayService.getKey());
            model.addAttribute("iv", newebPayService.getIv());
            return "orderDetail";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
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

    // 新增用于管理订单的方法
    @GetMapping("/manage")
    public String manageOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", List.of("Pending", "Paid", "Shipped", "Delivered", "Cancelled"));
        return "manageOrders";
    }
}
