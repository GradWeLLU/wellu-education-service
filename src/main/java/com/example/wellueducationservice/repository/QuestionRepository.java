package com.example.wellueducationservice.repository;

import com.example.wellueducationservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
