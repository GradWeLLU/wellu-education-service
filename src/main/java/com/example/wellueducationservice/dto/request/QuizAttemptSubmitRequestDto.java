package com.example.wellueducationservice.dto.request;

import java.util.Map;
import java.util.UUID;

public record QuizAttemptSubmitRequestDto(
        Map<UUID, Integer> answers
) {
}
