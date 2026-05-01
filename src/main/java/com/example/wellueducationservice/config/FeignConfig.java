package com.example.wellueducationservice.config;

import com.example.wellueducationservice.client.AiServiceErrorDecoder;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new AiServiceErrorDecoder();
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5,  TimeUnit.MINUTES,   // connect timeout
                10, TimeUnit.MINUTES,   // read timeout
                true                    // follow redirects
        );
    }

    @Bean
    public Retryer retryer() {
        // 3 attempts: initial 500ms, max 2s between retries
        return new Retryer.Default(500, 2_000, 3);
    }
}
