package com.example.wellueducationservice.controller;

import com.example.wellueducationservice.service.QuizApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<String> uploadQuiz(
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file
    ) {

        quizService.createQuizFromCsv(title, file);

        return ResponseEntity.ok("Quiz created successfully");
    }
}
