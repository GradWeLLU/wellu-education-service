package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.CreateQuizRequestDto;
import com.example.wellueducationservice.dto.request.CsvQuestionRow;
import com.example.wellueducationservice.dto.response.QuestionImportResponseDto;
import com.example.wellueducationservice.dto.response.QuizResponseDto;
import com.example.wellueducationservice.entity.Question;
import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.exception.QuizException;
import com.example.wellueducationservice.exception.QuizValidationException;
import com.example.wellueducationservice.mapper.QuizMapper;
import com.example.wellueducationservice.repository.QuestionRepository;
import com.example.wellueducationservice.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class QuizApplicationService {
    private final CsvParserService parserService;
    private final QuizCreationService creationService;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    public QuizApplicationService(
            CsvParserService parserService,
            QuizCreationService creationService,
            QuestionRepository questionRepository,
            QuizRepository quizRepository,
            QuizMapper quizMapper
    ) {
        this.parserService = parserService;
        this.creationService = creationService;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    @Transactional
    public QuestionImportResponseDto importQuestions(MultipartFile file) {
        List<CsvQuestionRow> rows = parserService.parse(file);
        List<Question> questions = creationService.createUnassignedQuestions(rows);

        questionRepository.saveAll(questions);

        return new QuestionImportResponseDto(questions.size(), questionRepository.countByQuizIsNull());
    }

    @Transactional
    public QuizResponseDto createQuiz(CreateQuizRequestDto request) {
        validateCreateQuizRequest(request);

        List<Question> availableQuestions = new ArrayList<>(questionRepository.findAllByQuizIsNullOrderByContentAsc());
        if (availableQuestions.size() < request.questionCount()) {
            throw new QuizException(
                    "Cannot create quiz with %d questions: only %d unassigned questions are available"
                            .formatted(request.questionCount(), availableQuestions.size())
            );
        }

        List<Question> selectedQuestions = selectUniqueQuestions(availableQuestions, request.questionCount());
        if (selectedQuestions.size() < request.questionCount()) {
            throw new QuizException(
                    "Cannot create quiz with %d questions: only %d unique unassigned questions are available"
                            .formatted(request.questionCount(), selectedQuestions.size())
            );
        }

        int totalPoints = selectedQuestions.stream()
                .map(Question::getPoints)
                .map(points -> points == null ? 0 : points)
                .reduce(0, Integer::sum);

        Quiz quiz = creationService.createQuiz(request, totalPoints);
        Quiz savedQuiz = quizRepository.save(quiz);

        for (Question question : selectedQuestions) {
            question.setDifficulty(savedQuiz.getDifficulty());
            savedQuiz.addQuestion(question);
        }

        questionRepository.saveAll(selectedQuestions);

        return quizMapper.toDto(savedQuiz);
    }

    private void validateCreateQuizRequest(CreateQuizRequestDto request) {
        if (request == null) {
            throw new QuizValidationException("Quiz request body is required");
        }

        if (request.title() == null || request.title().isBlank()) {
            throw new QuizValidationException("Quiz title is required");
        }

        if (request.questionCount() == null || request.questionCount() <= 0) {
            throw new QuizValidationException("Quiz questionCount must be greater than zero");
        }
    }

    private List<Question> selectUniqueQuestions(List<Question> availableQuestions, int questionCount) {
        List<Question> selectedQuestions = new ArrayList<>();
        Set<String> seenContents = new HashSet<>();

        for (Question question : availableQuestions) {
            String normalizedContent = normalizeQuestionContent(question.getContent());
            if (!seenContents.add(normalizedContent)) {
                continue;
            }

            selectedQuestions.add(question);
            if (selectedQuestions.size() == questionCount) {
                break;
            }
        }

        return selectedQuestions;
    }

    private String normalizeQuestionContent(String content) {
        return content == null
                ? ""
                : content.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
