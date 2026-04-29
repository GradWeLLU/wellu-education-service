package com.example.wellueducationservice.controller;

import com.example.wellueducationservice.dto.request.CreateQuizRequestDto;
import com.example.wellueducationservice.dto.response.QuestionImportResponseDto;
import com.example.wellueducationservice.dto.response.QuizResponseDto;
import com.example.wellueducationservice.service.QuizApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizApplicationService quizService;

    public QuizController(QuizApplicationService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/upload")
    public ResponseEntity<QuestionImportResponseDto> uploadQuestions(
            @RequestParam("file") MultipartFile file
    ) {
        QuestionImportResponseDto response = quizService.importQuestions(file);

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping
    public ResponseEntity<QuizResponseDto> createQuiz(@RequestBody CreateQuizRequestDto request) {
        QuizResponseDto response = quizService.createQuiz(request);

        return ResponseEntity.status(201).body(response);
    }
}
