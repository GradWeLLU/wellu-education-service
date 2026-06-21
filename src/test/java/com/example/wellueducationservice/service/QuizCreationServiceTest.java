package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.CreateQuizRequestDto;
import com.example.wellueducationservice.dto.request.CsvQuestionRow;
import com.example.wellueducationservice.entity.Question;
import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.enumeration.Difficulty;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuizCreationServiceTest {

    private final QuizCreationService quizCreationService = new QuizCreationService();

    // ─── createUnassignedQuestions ────────────────────────────────────────────

    @Test
    void createUnassignedQuestions_validRows_returnsQuestions() {
        CsvQuestionRow row = new CsvQuestionRow("What is protein?", "A macronutrient", "A vitamin", "A mineral", "A lipid", "Protein is a macronutrient.");

        List<Question> questions = quizCreationService.createUnassignedQuestions(List.of(row));

        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getContent()).isEqualTo("What is protein?");
        assertThat(questions.get(0).getChoices()).hasSize(4);
        assertThat(questions.get(0).getChoices().get(0)).isEqualTo("A macronutrient");
        assertThat(questions.get(0).getCorrectAnswerIndex()).isEqualTo(0);
        assertThat(questions.get(0).getExplanation()).isEqualTo("Protein is a macronutrient.");
        assertThat(questions.get(0).getPoints()).isEqualTo(1);
        assertThat(questions.get(0).getQuiz()).isNull();
    }

    @Test
    void createUnassignedQuestions_multipleRows_returnsAllQuestions() {
        List<CsvQuestionRow> rows = List.of(
                new CsvQuestionRow("Q1?", "A", "B", "C", "D", "Exp1"),
                new CsvQuestionRow("Q2?", "X", "Y", "Z", "W", "Exp2")
        );

        List<Question> questions = quizCreationService.createUnassignedQuestions(rows);

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).getContent()).isEqualTo("Q1?");
        assertThat(questions.get(1).getContent()).isEqualTo("Q2?");
    }

    @Test
    void createUnassignedQuestions_emptyList_returnsEmptyList() {
        List<Question> questions = quizCreationService.createUnassignedQuestions(List.of());

        assertThat(questions).isEmpty();
    }

    @Test
    void createUnassignedQuestions_correctAnswerAlwaysAtIndexZero() {
        CsvQuestionRow row = new CsvQuestionRow("Q?", "CorrectAnswer", "Wrong1", "Wrong2", "Wrong3", "Exp");

        List<Question> questions = quizCreationService.createUnassignedQuestions(List.of(row));

        assertThat(questions.get(0).getCorrectAnswerIndex()).isEqualTo(0);
        assertThat(questions.get(0).getChoices().get(0)).isEqualTo("CorrectAnswer");
    }

    // ─── createQuiz ──────────────────────────────────────────────────────────

    @Test
    void createQuiz_validRequest_returnsConfiguredQuiz() {
        CreateQuizRequestDto request = new CreateQuizRequestDto("Nutrition Basics", Difficulty.MEDIUM, 30, false, 5);

        Quiz quiz = quizCreationService.createQuiz(request, 10);

        assertThat(quiz.getTitle()).isEqualTo("Nutrition Basics");
        assertThat(quiz.getDifficulty()).isEqualTo(Difficulty.MEDIUM);
        assertThat(quiz.getTimeLimit()).isEqualTo(30);
        assertThat(quiz.getIsDaily()).isFalse();
        assertThat(quiz.getTotalPoints()).isEqualTo(10);
        assertThat(quiz.getQuestions()).isEmpty();
    }

    @Test
    void createQuiz_nullDifficulty_defaultsToEasy() {
        CreateQuizRequestDto request = new CreateQuizRequestDto("No Difficulty Quiz", null, 20, false, 3);

        Quiz quiz = quizCreationService.createQuiz(request, 5);

        assertThat(quiz.getDifficulty()).isEqualTo(Difficulty.EASY);
    }

    @Test
    void createQuiz_nullIsDaily_defaultsToFalse() {
        CreateQuizRequestDto request = new CreateQuizRequestDto("Quiz", Difficulty.EASY, 20, null, 3);

        Quiz quiz = quizCreationService.createQuiz(request, 5);

        assertThat(quiz.getIsDaily()).isFalse();
    }

    @Test
    void createQuiz_titleTrimmed() {
        CreateQuizRequestDto request = new CreateQuizRequestDto("  Trimmed Title  ", Difficulty.EASY, 20, false, 3);

        Quiz quiz = quizCreationService.createQuiz(request, 5);

        assertThat(quiz.getTitle()).isEqualTo("Trimmed Title");
    }
}