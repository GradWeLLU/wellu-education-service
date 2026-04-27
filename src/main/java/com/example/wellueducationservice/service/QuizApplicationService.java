package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.CsvQuestionRow;
import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.repository.QuizRepository;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class QuizApplicationService {
    private final CsvParserService parserService;
    private final QuizCreationService creationService;
    private final QuizRepository quizRepository;

    public QuizApplicationService(
            CsvParserService parserService,
            QuizCreationService creationService,
            QuizRepository quizRepository
    ) {
        this.parserService = parserService;
        this.creationService = creationService;
        this.quizRepository = quizRepository;
    }

    public Quiz createQuizFromCsv(String title, MultipartFile file) {

        // 1. Parse CSV
        List<CsvQuestionRow> rows = parserService.parse(file);

        // 2. Convert to entities
        Quiz quiz = creationService.createQuizFromRows(title, rows);

        // 3. Save ONCE (cascade handles questions)
        return quizRepository.save(quiz);
    }
}
