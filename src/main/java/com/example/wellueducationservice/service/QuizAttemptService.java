package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.QuizAttemptStartRequestDto;
import com.example.wellueducationservice.dto.request.QuizAttemptSubmitRequestDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResponseDto;
import com.example.wellueducationservice.dto.response.QuizAttemptQuestionResultDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResultResponseDto;
import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.entity.QuizAttempt;
import com.example.wellueducationservice.entity.Question;
import com.example.wellueducationservice.exception.QuizAttemptConflictException;
import com.example.wellueducationservice.exception.QuizAttemptNotFoundException;
import com.example.wellueducationservice.exception.QuizAttemptValidationException;
import com.example.wellueducationservice.mapper.QuizAttemptMapper;
import com.example.wellueducationservice.mapper.QuizMapper;
import com.example.wellueducationservice.repository.QuizAttemptRepository;
import com.example.wellueducationservice.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class QuizAttemptService {
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptMapper quizAttemptMapper;
    private final QuizMapper quizMapper;


    @Transactional
    public QuizAttemptResponseDto startAttempt(QuizAttemptStartRequestDto request) {
        validateStartRequest(request);

        List<Quiz> quizzes = quizRepository.findByDifficulty(request.difficulty()).stream()
                .filter(quiz -> quiz.getQuestions() != null && !quiz.getQuestions().isEmpty())
                .toList();

        if (quizzes.isEmpty()) {
            throw new QuizAttemptNotFoundException("No quizzes available for the requested difficulty");
        }
        Quiz quiz = quizzes.get(ThreadLocalRandom.current().nextInt(quizzes.size()));
        QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttemptMapper.toEntity(request, quiz));

        return quizAttemptMapper.toDto(savedAttempt);
    }

    @Transactional
    public QuizAttemptResultResponseDto submitAttempt(UUID attemptId, QuizAttemptSubmitRequestDto request) {
        validateSubmitRequest(request);

        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new QuizAttemptNotFoundException("Quiz attempt not found"));

        if (attempt.getCompletedAt() != null) {
            throw new QuizAttemptConflictException("Quiz attempt has already been submitted");
        }

        validateSubmittedAnswers(attempt.getQuiz(), request.answers());
        quizAttemptMapper.updateAnswers(request.answers(), attempt);

        attempt.calculateScore();
        attempt.setCompletedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        List<QuizAttemptQuestionResultDto> questionResults = buildQuestionResults(savedAttempt);

        return new QuizAttemptResultResponseDto(
                savedAttempt.getAttemptId(),
                savedAttempt.getUserId(),
                savedAttempt.getScore(),
                savedAttempt.getCompletedAt(),
                quizMapper.toDto(savedAttempt.getQuiz()),
                questionResults
        );
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptResponseDto> getUserAttempts(UUID userId) {
        if (userId == null) {
            throw new QuizAttemptValidationException("User id is required");
        }

        return quizAttemptRepository.findByUserId(userId).stream()
                .map(quizAttemptMapper::toDto)
                .toList();
    }

    private void validateStartRequest(QuizAttemptStartRequestDto request) {
        if (request == null) {
            throw new QuizAttemptValidationException("Quiz attempt start request is required");
        }

        if (request.userId() == null) {
            throw new QuizAttemptValidationException("User id is required");
        }

        if (request.difficulty() == null) {
            throw new QuizAttemptValidationException("Difficulty is required");
        }
    }

    private void validateSubmitRequest(QuizAttemptSubmitRequestDto request) {
        if (request == null || request.answers() == null) {
            throw new QuizAttemptValidationException("Answers are required");
        }
    }

    private void validateSubmittedAnswers(Quiz quiz, Map<UUID, Integer> answers) {
        Set<UUID> quizQuestionIds = new HashSet<>();
        for (Question question : quiz.getQuestions()) {
            quizQuestionIds.add(question.getId());
        }

        for (Map.Entry<UUID, Integer> answer : answers.entrySet()) {
            UUID questionId = answer.getKey();
            Integer selectedAnswerIndex = answer.getValue();

            if (!quizQuestionIds.contains(questionId)) {
                throw new QuizAttemptValidationException("Answer contains a question outside this quiz");
            }

            Question quizQuestion = quiz.getQuestions().stream()
                    .filter(question -> question.getId().equals(questionId))
                    .findFirst()
                    .orElseThrow(() -> new QuizAttemptValidationException("Answer references an unknown quiz question"));

            if (selectedAnswerIndex == null) {
                throw new QuizAttemptValidationException("Selected answer index is required");
            }

            if (selectedAnswerIndex < 0 || selectedAnswerIndex >= quizQuestion.getChoices().size()) {
                throw new QuizAttemptValidationException("Selected answer index is out of range");
            }
        }
    }

    private List<QuizAttemptQuestionResultDto> buildQuestionResults(QuizAttempt attempt) {
        List<QuizAttemptQuestionResultDto> questionResults = new ArrayList<>();

        for (Question question : attempt.getQuiz().getQuestions()) {
            Integer selectedAnswerIndex = attempt.getAnswers().get(question.getId());
            boolean correct = selectedAnswerIndex != null && selectedAnswerIndex == question.getCorrectAnswerIndex();

            questionResults.add(new QuizAttemptQuestionResultDto(
                    question.getId(),
                    question.getContent(),
                    question.getChoices(),
                    selectedAnswerIndex,
                    question.getCorrectAnswerIndex(),
                    correct,
                    question.getExplanation(),
                    question.getPoints()
            ));
        }

        return questionResults;
    }
}
