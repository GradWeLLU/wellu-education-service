package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.QuizAttemptStartRequestDto;
import com.example.wellueducationservice.dto.request.QuizAttemptSubmitRequestDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResponseDto;
import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.entity.QuizAttempt;
import com.example.wellueducationservice.mapper.QuizAttemptMapper;
import com.example.wellueducationservice.repository.QuizAttemptRepository;
import com.example.wellueducationservice.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class QuizAttemptService {
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptMapper quizAttemptMapper;


    public ResponseEntity<QuizAttemptResponseDto> startAttempt(QuizAttemptStartRequestDto request) {
        List<Quiz> quizzes = quizRepository.findByDifficulty(request.difficulty());

        if (quizzes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Quiz quiz = quizzes.get(ThreadLocalRandom.current().nextInt(quizzes.size()));

        QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttemptMapper.toEntity(request, quiz));
        return ResponseEntity.status(HttpStatus.CREATED).body(quizAttemptMapper.toDto(savedAttempt));
    }

    public ResponseEntity<QuizAttemptResponseDto> submitAttempt(UUID attemptId, QuizAttemptSubmitRequestDto request) {

        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElse(null);

        if (attempt == null) {
            return ResponseEntity.notFound().build();
        }

        quizAttemptMapper.updateAnswers(request.answers(), attempt);

        attempt.calculateScore();

        attempt.setCompletedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        return ResponseEntity.ok(quizAttemptMapper.toDto(savedAttempt));
    }

    public ResponseEntity<List<QuizAttemptResponseDto>> getUserAttempts(UUID userId) {
        List<QuizAttemptResponseDto> attempts = quizAttemptRepository.findByUserId(userId).stream()
                .map(quizAttemptMapper::toDto)
                .toList();

        return ResponseEntity.ok(attempts);
    }

}
