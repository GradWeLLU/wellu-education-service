package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.CsvQuestionRow;
import com.example.wellueducationservice.entity.Question;
import com.example.wellueducationservice.entity.Quiz;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionCreationService {

    public Quiz createQuizFromRows(String title, List<CsvQuestionRow> rows) {

        Quiz quiz = new Quiz();
        quiz.setTitle(title);

        List<Question> questions = new ArrayList<>();

        for (CsvQuestionRow row : rows) {
            Question question = mapToQuestion(row, quiz);
            questions.add(question);
        }

        quiz.setQuestions(questions);

        return quiz;
    }

    private Question mapToQuestion(CsvQuestionRow row, Quiz quiz) {

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

        // Set relationship
        question.setQuiz(quiz);

        return question;
    }
}
