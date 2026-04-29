package com.example.wellueducationservice.dto.response;

public record QuestionImportResponseDto(
        int importedCount,
        long availableQuestionCount
) {
}
