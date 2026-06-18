package com.example.wellueducationservice.dto.request;

import com.example.wellueducationservice.enumeration.Difficulty;

import java.util.UUID;

public record QuizAttemptStartRequestDto(
        Difficulty difficulty
) {
}
