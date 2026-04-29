package com.example.wellueducationservice.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuizAttemptResultResponseDto(
        UUID attemptId,
        UUID userId,
        Double score,
        LocalDateTime completedAt,
        QuizResponseDto quiz,
        List<QuizAttemptQuestionResultDto> questionResults
) {
}
