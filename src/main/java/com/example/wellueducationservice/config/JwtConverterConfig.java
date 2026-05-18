package com.example.wellueducationservice.config;

import com.example.wellueducationservice.security.AuthenticatedUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.wellu.common.security.JwtAuthenticationConverter;

import java.util.Collections;

@Configuration
public class JwtConverterConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        return userId -> {

            AuthenticatedUser principal =
                    new AuthenticatedUser(userId);

            return new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    Collections.emptyList()
            );
        };
    }
}