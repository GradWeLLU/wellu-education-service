package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.CreateQuizRequestDto;
import com.example.wellueducationservice.dto.request.CsvQuestionRow;
import com.example.wellueducationservice.dto.response.QuizResponseDto;
import com.example.wellueducationservice.entity.Question;
import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.enumeration.Difficulty;
import com.example.wellueducationservice.mapper.QuizMapper;
import com.example.wellueducationservice.repository.QuestionRepository;
import com.example.wellueducationservice.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizApplicationServiceTest {

    @Mock private CsvParserService parserService;
    @Mock private QuizCreationService creationService;
    @Mock private QuestionRepository questionRepository;
    @Mock private QuizRepository quizRepository;
    @Mock private QuizMapper quizMapper;

    @InjectMocks
    private QuizApplicationService quizApplicationService;

    private Question makeQuestion(String content) {
        Question q = new Question();
        q.setContent(content);
        q.setChoices(List.of("A", "B", "C", "D"));
        q.setCorrectAnswerIndex(0);
        q.setPoints(1);
        return q;
    }

    // ─── createQuiz ──────────────────────────────────────────────────────────

    @Test
    void createQuiz_validRequest_returnsDto() {
        CreateQuizRequestDto request = new CreateQuizRequestDto("Nutrition Basics", Difficulty.EASY, 30, false, 2);

        Question q1 = makeQuestion("What is protein?");
        Question q2 = makeQuestion("What is fiber?");

        Quiz quiz = new Quiz();
        quiz.setTitle("Nutrition Basics");
        quiz.setQuestions(new ArrayList<>());

        QuizResponseDto responseDto = new QuizResponseDto(UUID.randomUUID(), "Nutrition Basics", Difficulty.EASY, 30, false, 2, List.of());

        when(questionRepository.findAllByQuizIsNullOrderByContentAsc()).thenReturn(List.of(q1, q2));
        when(creationService.createQuiz(request, 2)).thenReturn(quiz);
        when(quizRepository.save(quiz)).thenReturn(quiz);
        when(questionRepository.saveAll(any())).thenReturn(List.of(q1, q2));
        when(quizMapper.toDto(quiz)).thenReturn(responseDto);

        QuizResponseDto result = quizApplicationService.createQuiz(request);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Nutrition Basics");
        verify(quizRepository).save(quiz);
        verify(questionRepository).saveAll(any());
    }

    @Test
    void createQuiz_notEnoughQuestions_throwsConflict() {
        CreateQuizRequestDto request = new CreateQuizRequestDto("Hard Quiz", Difficulty.HARD, 30, false, 5);

        when(questionRepository.findAllByQuizIsNullOrderByContentAsc()).thenReturn(List.of(makeQuestion("Q1")));

        assertThatThrownBy(() -> quizApplicationService.createQuiz(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not enough unassigned questions");

        verify(quizRepository, never()).save(any());
    }

    @Test
    void createQuiz_nullTitle_throwsBadRequest() {
        CreateQuizRequestDto request = new CreateQuizRequestDto(null, Difficulty.EASY, 30, false, 2);

        assertThatThrownBy(() -> quizApplicationService.createQuiz(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Quiz title is required");

        verify(quizRepository, never()).save(any());
    }

    @Test
    void createQuiz_blankTitle_throwsBadRequest() {
        CreateQuizRequestDto request = new CreateQuizRequestDto("   ", Difficulty.EASY, 30, false, 2);

        assertThatThrownBy(() -> quizApplicationService.createQuiz(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Quiz title is required");
    }

    @Test
    void createQuiz_zeroQuestionCount_throwsBadRequest() {
        CreateQuizRequestDto request = new CreateQuizRequestDto("Valid Title", Difficulty.EASY, 30, false, 0);

        assertThatThrownBy(() -> quizApplicationService.createQuiz(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("questionCount must be greater than zero");
    }

    @Test
    void createQuiz_duplicateQuestions_deduplicatesBeforeSelecting() {
        CreateQuizRequestDto request = new CreateQuizRequestDto("Dedup Quiz", Difficulty.EASY, 30, false, 1);

        // Two questions with identical content — only one should be selected
        Question q1 = makeQuestion("What is protein?");
        Question q2 = makeQuestion("What is protein?"); // duplicate

        Quiz quiz = new Quiz();
        quiz.setTitle("Dedup Quiz");
        quiz.setQuestions(new ArrayList<>());

        QuizResponseDto responseDto = new QuizResponseDto(UUID.randomUUID(), "Dedup Quiz", Difficulty.EASY, 30, false, 1, List.of());

        when(questionRepository.findAllByQuizIsNullOrderByContentAsc()).thenReturn(List.of(q1, q2));
        when(creationService.createQuiz(request, 1)).thenReturn(quiz);
        when(quizRepository.save(quiz)).thenReturn(quiz);
        when(questionRepository.saveAll(any())).thenReturn(List.of(q1));
        when(quizMapper.toDto(quiz)).thenReturn(responseDto);

        QuizResponseDto result = quizApplicationService.createQuiz(request);

        assertThat(result).isNotNull();
        // Only 1 unique question selected despite 2 available
        verify(questionRepository).saveAll(argThat(list -> ((List<?>) list).size() == 1));
    }

    // ─── importQuestions ─────────────────────────────────────────────────────

    @Test
    void importQuestions_validFile_savesAndReturnsCount() {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);

        CsvQuestionRow row = new CsvQuestionRow("Q1?", "Correct", "Wrong1", "Wrong2", "Wrong3", "Because.");
        Question q = makeQuestion("Q1?");

        when(parserService.parse(file)).thenReturn(List.of(row));
        when(creationService.createUnassignedQuestions(List.of(row))).thenReturn(List.of(q));
        when(questionRepository.saveAll(List.of(q))).thenReturn(List.of(q));
        when(questionRepository.countByQuizIsNull()).thenReturn(1L);

        var result = quizApplicationService.importQuestions(file);

        assertThat(result.importedCount()).isEqualTo(1);
        assertThat(result.availableQuestionCount()).isEqualTo(1L);
        verify(questionRepository).saveAll(List.of(q));
    }
}