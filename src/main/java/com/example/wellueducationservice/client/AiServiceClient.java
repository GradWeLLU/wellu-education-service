package com.example.wellueducationservice.client;

import com.example.wellueducationservice.config.FeignConfig;
import com.example.wellueducationservice.dto.response.AiFactResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name  = "ai-service",
        url   = "${ai-service.base-url}",
        configuration = FeignConfig.class
)
public interface AiServiceClient {

    @PostMapping("/generate-daily-fact")
    AiFactResponse generateDailyFact();
}
