package com.example.wellueducationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.wellu.common.exception.GlobalExceptionHandler;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@Import(GlobalExceptionHandler.class)
public class WelluEducationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WelluEducationServiceApplication.class, args);
    }

}
