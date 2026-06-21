package com.example.wellueducationservice.controller;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class HealthCheckContoller {

    @GetMapping("/me")
    public String me(Authentication authentication) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return userId.toString();
    }
}
