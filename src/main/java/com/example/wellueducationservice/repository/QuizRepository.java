package com.example.wellueducationservice.repository;

import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.enumeration.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findByDifficulty(Difficulty difficulty);
}
