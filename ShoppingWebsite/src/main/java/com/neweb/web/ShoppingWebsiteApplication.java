package com.neweb.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.neweb.web.repository")
@EnableElasticsearchRepositories(basePackages = "com.neweb.web.repository.elasticsearch")
@EnableEncryptableProperties
public class ShoppingWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingWebsiteApplication.class, args);
    }
}
