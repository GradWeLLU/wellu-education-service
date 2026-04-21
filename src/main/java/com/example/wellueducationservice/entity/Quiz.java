package com.example.wellueducationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    private String difficulty;   // EASY, MEDIUM, HARD

    private Integer timeLimit;   // in minutes (optional)
    private Boolean isDaily;     // for daily quizzes

    private Integer totalPoints; // optional (can also compute)

    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }
}