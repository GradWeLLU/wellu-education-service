package com.example.wellueducationservice.repository;

import com.example.wellueducationservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findAllByQuizIsNull();

    List<Question> findAllByQuizIsNullOrderByContentAsc();

    long countByQuizIsNull();
}
