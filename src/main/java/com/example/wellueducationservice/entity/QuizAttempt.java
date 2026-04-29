package com.example.wellueducationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class QuizAttempt {

    @Id
    @GeneratedValue
    private UUID attemptId;

    private UUID userId;

    private Double score;

    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ElementCollection
    @CollectionTable(name = "attempt_answers")
    @MapKeyColumn(name = "question_id")
    @Column(name = "selected_answer")
    private Map<UUID, Integer> answers = new HashMap<>();

    public double calculateScore() {
        double total = 0;

        for (Question q : quiz.getQuestions()) {
            Integer userAnswer = answers.get(q.getId());

            if (userAnswer != null && userAnswer == q.getCorrectAnswerIndex()) {
                total += q.getPoints() == null ? 0 : q.getPoints();
            }
        }

        this.score = total;
        return total;
    }
}
