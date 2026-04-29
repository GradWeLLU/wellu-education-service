package com.example.wellueducationservice.dto.request;

import com.example.wellueducationservice.enumeration.Difficulty;

public record CreateQuizRequestDto(
        String title,
        Difficulty difficulty,
        Integer timeLimit,
        Boolean isDaily,
        Integer questionCount
) {
}
