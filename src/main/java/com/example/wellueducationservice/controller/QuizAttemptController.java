package com.example.wellueducationservice.controller;

import com.example.wellueducationservice.dto.request.QuizAttemptStartRequestDto;
import com.example.wellueducationservice.dto.request.QuizAttemptSubmitRequestDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResponseDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResultResponseDto;
import com.example.wellueducationservice.service.QuizAttemptService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<QuizAttemptResponseDto> startAttempt(@RequestBody QuizAttemptStartRequestDto request) {
        QuizAttemptResponseDto response = quizAttemptService.startAttempt(request);

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<QuizAttemptResultResponseDto> submitAttempt(
            @PathVariable UUID attemptId,
            @RequestBody QuizAttemptSubmitRequestDto request
    ) {
        QuizAttemptResultResponseDto response = quizAttemptService.submitAttempt(attemptId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<QuizAttemptResponseDto>> getUserAttempts(@PathVariable UUID userId) {
        List<QuizAttemptResponseDto> response = quizAttemptService.getUserAttempts(userId);

        return ResponseEntity.ok(response);
    }
}
