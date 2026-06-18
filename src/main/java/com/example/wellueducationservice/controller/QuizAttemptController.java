package com.example.wellueducationservice.controller;

import com.example.wellueducationservice.dto.request.QuizAttemptStartRequestDto;
import com.example.wellueducationservice.dto.request.QuizAttemptSubmitRequestDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResponseDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResultResponseDto;
import com.example.wellueducationservice.service.QuizAttemptService;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @PostMapping("/start")
    public ResponseEntity<QuizAttemptResponseDto> startAttempt(@RequestBody QuizAttemptStartRequestDto request, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();

        QuizAttemptResponseDto response = quizAttemptService.startAttempt(request, userId);

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<QuizAttemptResultResponseDto> submitAttempt(
            @PathVariable UUID attemptId,
            @RequestBody QuizAttemptSubmitRequestDto request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();
        QuizAttemptResultResponseDto response = quizAttemptService.submitAttempt(attemptId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-attempts")
    public ResponseEntity<List<QuizAttemptResponseDto>>
    getUserAttempts(Authentication authentication) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        List<QuizAttemptResponseDto> response =
                quizAttemptService.getUserAttempts(userId);

        return ResponseEntity.ok(response);
    }
}
