package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.QuizAttemptStartRequestDto;
import com.example.wellueducationservice.dto.request.QuizAttemptSubmitRequestDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResponseDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResultResponseDto;
import com.example.wellueducationservice.entity.Question;
import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.entity.QuizAttempt;
import com.example.wellueducationservice.enumeration.Difficulty;
import com.example.wellueducationservice.mapper.QuizAttemptMapper;
import com.example.wellueducationservice.mapper.QuizMapper;
import com.example.wellueducationservice.repository.QuizAttemptRepository;
import com.example.wellueducationservice.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceTest {

    @Mock private QuizRepository quizRepository;
    @Mock private QuizAttemptRepository quizAttemptRepository;
    @Mock private QuizAttemptMapper quizAttemptMapper;
    @Mock private QuizMapper quizMapper;

    @InjectMocks
    private QuizAttemptService quizAttemptService;

    private UUID userId;
    private UUID attemptId;
    private Quiz quiz;
    private Question question;
    private QuizAttempt attempt;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        attemptId = UUID.randomUUID();

        question = new Question();
        question.setContent("What is protein?");
        question.setChoices(List.of("A macro", "A vitamin", "A mineral", "A lipid"));
        question.setCorrectAnswerIndex(0);
        question.setPoints(1);

        quiz = new Quiz();
        quiz.setTitle("Nutrition Quiz");
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setQuestions(new ArrayList<>(List.of(question)));

        attempt = new QuizAttempt();
        attempt.setUserId(userId);
        attempt.setQuiz(quiz);
    }

    // ─── startAttempt ────────────────────────────────────────────────────────

    @Test
    void startAttempt_validRequest_returnsDto() {
        QuizAttemptStartRequestDto request = new QuizAttemptStartRequestDto(userId, Difficulty.EASY);
        QuizAttemptResponseDto responseDto = mock(QuizAttemptResponseDto.class);

        when(quizRepository.findByDifficulty(Difficulty.EASY)).thenReturn(List.of(quiz));
        when(quizAttemptMapper.toEntity(request, quiz)).thenReturn(attempt);
        when(quizAttemptRepository.save(attempt)).thenReturn(attempt);
        when(quizAttemptMapper.toDto(attempt)).thenReturn(responseDto);

        QuizAttemptResponseDto result = quizAttemptService.startAttempt(request);

        assertThat(result).isNotNull();
        verify(quizAttemptRepository).save(attempt);
    }

    @Test
    void startAttempt_noQuizzesForDifficulty_throwsNotFound() {
        QuizAttemptStartRequestDto request = new QuizAttemptStartRequestDto(userId, Difficulty.HARD);

        when(quizRepository.findByDifficulty(Difficulty.HARD)).thenReturn(List.of());

        assertThatThrownBy(() -> quizAttemptService.startAttempt(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No quizzes available for this difficulty");

        verify(quizAttemptRepository, never()).save(any());
    }

    @Test
    void startAttempt_quizWithNoQuestions_throwsNotFound() {
        QuizAttemptStartRequestDto request = new QuizAttemptStartRequestDto(userId, Difficulty.EASY);

        Quiz emptyQuiz = new Quiz();
        emptyQuiz.setDifficulty(Difficulty.EASY);
        emptyQuiz.setQuestions(new ArrayList<>());

        when(quizRepository.findByDifficulty(Difficulty.EASY)).thenReturn(List.of(emptyQuiz));

        assertThatThrownBy(() -> quizAttemptService.startAttempt(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No quizzes available for this difficulty");
    }

    @Test
    void startAttempt_nullUserId_throwsBadRequest() {
        QuizAttemptStartRequestDto request = new QuizAttemptStartRequestDto(null, Difficulty.EASY);

        assertThatThrownBy(() -> quizAttemptService.startAttempt(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("userId is required");
    }

    @Test
    void startAttempt_nullDifficulty_throwsBadRequest() {
        QuizAttemptStartRequestDto request = new QuizAttemptStartRequestDto(userId, null);

        assertThatThrownBy(() -> quizAttemptService.startAttempt(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("difficulty is required");
    }

    // ─── submitAttempt ───────────────────────────────────────────────────────

    @Test
    void submitAttempt_validAnswers_returnsResult() {
        UUID questionId = UUID.randomUUID();
        question.setChoices(List.of("A macro", "B", "C", "D"));

        // Give the question a real ID
        Question q = spy(question);
        doReturn(questionId).when(q).getId();
        quiz.setQuestions(new ArrayList<>(List.of(q)));
        attempt.setQuiz(quiz);
        attempt.getAnswers().put(questionId, 0);

        QuizAttemptSubmitRequestDto request = new QuizAttemptSubmitRequestDto(Map.of(questionId, 0));

        when(quizAttemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        doNothing().when(quizAttemptMapper).updateAnswers(request.answers(), attempt);
        when(quizAttemptRepository.save(attempt)).thenReturn(attempt);
        when(quizMapper.toDto(quiz)).thenReturn(null);

        QuizAttemptResultResponseDto result = quizAttemptService.submitAttempt(attemptId, request);

        assertThat(result).isNotNull();
        verify(quizAttemptRepository).save(attempt);
    }

    @Test
    void submitAttempt_attemptNotFound_throwsNotFound() {
        QuizAttemptSubmitRequestDto request = new QuizAttemptSubmitRequestDto(Map.of());

        when(quizAttemptRepository.findById(attemptId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizAttemptService.submitAttempt(attemptId, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Attempt not found");
    }

    @Test
    void submitAttempt_alreadySubmitted_throwsConflict() {
        attempt.setCompletedAt(LocalDateTime.now());

        QuizAttemptSubmitRequestDto request = new QuizAttemptSubmitRequestDto(Map.of());

        when(quizAttemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> quizAttemptService.submitAttempt(attemptId, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already been submitted");
    }

    @Test
    void submitAttempt_nullAnswers_throwsBadRequest() {
        QuizAttemptSubmitRequestDto request = new QuizAttemptSubmitRequestDto(null);

        assertThatThrownBy(() -> quizAttemptService.submitAttempt(attemptId, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("answers are required");
    }

    @Test
    void submitAttempt_questionNotInQuiz_throwsBadRequest() {
        UUID foreignQuestionId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        Question q = spy(question);
        doReturn(questionId).when(q).getId();
        quiz.setQuestions(new ArrayList<>(List.of(q)));
        attempt.setQuiz(quiz);

        // Answer references a question ID not in the quiz
        QuizAttemptSubmitRequestDto request = new QuizAttemptSubmitRequestDto(Map.of(foreignQuestionId, 0));

        when(quizAttemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> quizAttemptService.submitAttempt(attemptId, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("does not belong to this quiz");
    }

    @Test
    void submitAttempt_answerIndexOutOfRange_throwsBadRequest() {
        UUID questionId = UUID.randomUUID();

        Question q = spy(question);
        doReturn(questionId).when(q).getId();
        q.setChoices(List.of("A", "B", "C", "D")); // 4 choices, valid indices 0-3
        quiz.setQuestions(new ArrayList<>(List.of(q)));
        attempt.setQuiz(quiz);

        // Index 10 is out of range
        QuizAttemptSubmitRequestDto request = new QuizAttemptSubmitRequestDto(Map.of(questionId, 10));

        when(quizAttemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> quizAttemptService.submitAttempt(attemptId, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("out of range");
    }

    // ─── getUserAttempts ─────────────────────────────────────────────────────

    @Test
    void getUserAttempts_returnsListForUser() {
        QuizAttemptResponseDto dto = mock(QuizAttemptResponseDto.class);

        when(quizAttemptRepository.findByUserId(userId)).thenReturn(List.of(attempt));
        when(quizAttemptMapper.toDto(attempt)).thenReturn(dto);

        List<QuizAttemptResponseDto> result = quizAttemptService.getUserAttempts(userId);

        assertThat(result).hasSize(1);
    }

    @Test
    void getUserAttempts_nullUserId_throwsBadRequest() {
        assertThatThrownBy(() -> quizAttemptService.getUserAttempts(null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("userId is required");
    }
}