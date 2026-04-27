package com.example.wellueducationservice.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record QuizAttemptResponseDto(
        UUID attemptId,
        UUID userId,
        Double score,
        LocalDateTime completedAt,
        QuizResponseDto quiz,
        Map<UUID, Integer> answers
) {
}
