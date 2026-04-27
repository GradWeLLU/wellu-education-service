package com.example.wellueducationservice.repository;

import com.example.wellueducationservice.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    List<QuizAttempt> findByUserId(UUID userId);
    List<QuizAttempt> findByQuizId(UUID quizId);
}