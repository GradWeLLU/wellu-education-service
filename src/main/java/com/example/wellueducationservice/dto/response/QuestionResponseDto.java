package com.example.wellueducationservice.dto.response;

import com.example.wellueducationservice.enumeration.Difficulty;

import java.util.List;
import java.util.UUID;

public record QuestionResponseDto(
        UUID id,
        String content,
        List<String> choices,
        Difficulty difficulty,
        Integer points
) {
}
