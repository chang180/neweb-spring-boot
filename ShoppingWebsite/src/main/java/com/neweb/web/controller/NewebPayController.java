package com.neweb.web.controller;

import com.neweb.web.service.NewebPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/newebpay")
public class NewebPayController {

    @Autowired
    private NewebPayService newebPayService;

    @PostMapping("/pay")
    public String pay(@RequestParam("orderNo") String orderNo, 
                      @RequestParam("amount") double amount, 
                      @RequestParam("itemDesc") String itemDesc, 
                      @RequestParam("email") String email,
                      Model model) throws Exception {
        Map<String, String> paymentRequest = newebPayService.createPaymentRequest(orderNo, amount, itemDesc, email);
        Map<String, String> originalParams = newebPayService.createOriginalParams(orderNo, amount, itemDesc);
        
        model.addAttribute("paymentRequest", paymentRequest);
        model.addAttribute("originalParams", originalParams);
        model.addAttribute("key", newebPayService.getKey());
        model.addAttribute("iv", newebPayService.getIv());
        
        return "newebpay/pay";
    }

}
