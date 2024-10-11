package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.bookstore"})
@EnableJpaRepositories(basePackages = "com.bookstore.repository")
@EntityScan(basePackages = "entity")
public class OnlineBookstoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineBookstoreApplication.class, args);
    }
}

