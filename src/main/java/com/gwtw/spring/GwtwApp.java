package com.gwtw.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GwtwApp {

    public static void main(String[] args) {
        SpringApplication.run(GwtwApp.class, args);
    }

}
