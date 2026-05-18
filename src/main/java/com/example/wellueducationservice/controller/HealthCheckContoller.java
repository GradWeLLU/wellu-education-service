package com.example.wellueducationservice.controller;


import com.example.wellueducationservice.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckContoller {

    @GetMapping("/me")
    public String me(Authentication authentication) {

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        return user.getUserId().toString();
    }
}
