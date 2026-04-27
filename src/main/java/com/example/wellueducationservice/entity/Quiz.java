package com.example.wellueducationservice.entity;

import com.example.wellueducationservice.enumeration.Difficulty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Quiz {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;  // EASY, MEDIUM, HARD

    private Integer timeLimit;   // in minutes (optional)
    private Boolean isDaily;     // for daily quizzes

    private Integer totalPoints; // optional (can also compute)

    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz")
    private List<QuizAttempt> attempts = new ArrayList<>();

    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }
}