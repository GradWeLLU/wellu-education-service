package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.CreateQuizRequestDto;
import com.example.wellueducationservice.dto.request.CsvQuestionRow;
import com.example.wellueducationservice.entity.Question;
import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.enumeration.Difficulty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizCreationService {
    public List<Question> createUnassignedQuestions(List<CsvQuestionRow> rows) {
        List<Question> questions = new ArrayList<>();

        for (CsvQuestionRow row : rows) {
            questions.add(mapToQuestion(row));
        }

        return questions;
    }

    public Quiz createQuiz(CreateQuizRequestDto request, int totalPoints) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.title().trim());
        quiz.setDifficulty(request.difficulty() == null ? Difficulty.EASY : request.difficulty());
        quiz.setTimeLimit(request.timeLimit());
        quiz.setIsDaily(request.isDaily() != null && request.isDaily());
        quiz.setTotalPoints(totalPoints);
        quiz.setQuestions(new ArrayList<>());

        return quiz;
    }

    private Question mapToQuestion(CsvQuestionRow row) {
        Question question = new Question();
        question.setContent(row.question());

        // Build choices (correct ALWAYS first)
        List<String> choices = List.of(
                row.correct(),
                row.wrong1(),
                row.wrong2(),
                row.wrong3()
        );

        question.setChoices(choices);

        // Enforce rule: correct answer index = 0
        question.setCorrectAnswerIndex(0);

        question.setExplanation(row.explanation());

        // Optional: default points
        question.setPoints(1);
        question.setDifficulty(null);

        return question;
    }
}
