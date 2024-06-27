package com.neweb.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
public class ShoppingWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingWebsiteApplication.class, args);
    }
}
