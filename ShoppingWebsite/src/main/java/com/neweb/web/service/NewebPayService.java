package com.neweb.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class NewebPayService {

    @Value("${newebpay.hashKey}")
    private String hashKey;

    @Value("${newebpay.hashIV}")
    private String hashIV;

    @Value("${newebpay.merchantId}")
    private String merchantId;

    @Value("${newebpay.returnUrl}")
    private String returnUrl;

    @Value("${newebpay.notifyUrl}")
    private String notifyUrl;

    public Map<String, String> createPaymentRequest(String orderNo, double amount, String itemDesc, String email) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", merchantId);
        params.put("RespondType", "JSON");
        params.put("TimeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("Version", "2.0");
        params.put("MerchantOrderNo", orderNo);
        params.put("Amt", String.valueOf((int) amount));
        params.put("ItemDesc", itemDesc);
        params.put("Email", email);
        if (returnUrl != null && !returnUrl.isEmpty()) {
            params.put("ReturnURL", returnUrl);
        }
        if (notifyUrl != null && !notifyUrl.isEmpty()) {
            params.put("NotifyURL", notifyUrl);
        }

        String tradeInfo = encrypt(params, hashKey, hashIV);
        String tradeSha = sha256("HashKey=" + hashKey + "&" + tradeInfo + "&HashIV=" + hashIV);

        Map<String, String> paymentRequest = new HashMap<>();
        paymentRequest.put("MerchantID", merchantId);
        paymentRequest.put("TradeInfo", tradeInfo);
        paymentRequest.put("TradeSha", tradeSha);
        paymentRequest.put("Version", "2.0");

        return paymentRequest;
    }

    public Map<String, String> createOriginalParams(String orderNo, double amount, String itemDesc) {
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", merchantId);
        params.put("RespondType", "JSON");
        params.put("TimeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("Version", "2.0");
        params.put("MerchantOrderNo", orderNo);
        params.put("Amt", String.valueOf((int) amount));
        params.put("ItemDesc", itemDesc);
        if (returnUrl != null && !returnUrl.isEmpty()) {
            params.put("ReturnURL", returnUrl);
        }
        if (notifyUrl != null && !notifyUrl.isEmpty()) {
            params.put("NotifyURL", notifyUrl);
        }

        return params;
    }

    public String getKey() {
        return hashKey;
    }

    public String getIv() {
        return hashIV;
    }

    private String encrypt(Map<String, String> params, String key, String iv) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() != 0) {
                sb.append('&');
            }
            sb.append(entry.getKey()).append('=').append(entry.getValue());
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(sb.toString().getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encrypted);
    }

    private String sha256(String str) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

