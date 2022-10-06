package com.demo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {

    @GetMapping(value = "/")
    public String index() {
        return "Hello";
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}