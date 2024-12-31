package com.awake.ve.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.awake.ve")
public class VirtualEaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(VirtualEaseApplication.class, args);
    }
}
