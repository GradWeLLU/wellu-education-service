package com.example.wellueducationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Question {

    @Id
    @GeneratedValue
    private UUID id;

    private String content;

    @ElementCollection
    private List<String> choices;

    private int correctAnswerIndex;

    private String explanation;

    private Integer points;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
