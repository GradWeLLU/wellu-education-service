package com.example.wellueducationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String content;

    @ElementCollection
    @CollectionTable(
            name = "question_choices",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "choice_text")
    @OrderColumn(name = "choice_order")
    private List<String> choices;

    private int correctAnswerIndex;

    private String explanation;

    private Integer points;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
