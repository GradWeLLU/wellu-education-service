package com.example.wellueducationservice.client;

import com.example.wellueducationservice.exception.AiServiceException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AiServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "AI Service error [%s]: HTTP %d".formatted(methodKey, response.status());
        log.error(message);

        return switch (response.status()) {
            case 408, 503, 504 -> new RetryableException(
                    response.status(), message, response.request().httpMethod(),
                    (Long) null, response.request()
            );
            default -> new AiServiceException(message);
        };
    }
}
