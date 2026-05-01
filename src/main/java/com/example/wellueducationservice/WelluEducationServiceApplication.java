package com.example.wellueducationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class WelluEducationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WelluEducationServiceApplication.class, args);
    }

}
