package com.example.wellueducationservice.dto.request;

public record CsvQuestionRow(
        String question,
        String correct,
        String wrong1,
        String wrong2,
        String wrong3,
        String explanation
) {}