package com.example.wellueducationservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wellu.common.exception.ApiError;
import org.wellu.common.exception.GlobalExceptionHandler;

import static org.assertj.core.api.Assertions.assertThat;

class AiServiceExceptionTest {

    @Test
    void globalExceptionHandlerUsesBaseExceptionStatusAndErrorCode() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<ApiError> response = handler.handleBaseException(
                new AiServiceException("Failed to reach AI Service")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getBody().getMessage()).isEqualTo("Failed to reach AI Service");
        assertThat(response.getBody().getErrorCode()).isEqualTo("KOKOMAMA");
    }
}
