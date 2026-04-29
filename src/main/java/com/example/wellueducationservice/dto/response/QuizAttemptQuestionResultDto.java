package com.example.wellueducationservice.dto.response;

import java.util.List;
import java.util.UUID;

public record QuizAttemptQuestionResultDto(
        UUID questionId,
        String content,
        List<String> choices,
        Integer selectedAnswerIndex,
        Integer correctAnswerIndex,
        boolean correct,
        String explanation,
        Integer points
) {
}
