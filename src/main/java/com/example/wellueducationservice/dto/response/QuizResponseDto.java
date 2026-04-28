package com.example.wellueducationservice.dto.response;

import com.example.wellueducationservice.enumeration.Difficulty;

import java.util.List;
import java.util.UUID;

public record QuizResponseDto(
        UUID id,
        String title,
        Difficulty difficulty,
        Integer timeLimit,
        Boolean isDaily,
        Integer totalPoints,
        List<QuestionResponseDto> questions
) {
}
