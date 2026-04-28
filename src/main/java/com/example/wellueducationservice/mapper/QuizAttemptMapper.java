package com.example.wellueducationservice.mapper;

import com.example.wellueducationservice.dto.request.QuizAttemptStartRequestDto;
import com.example.wellueducationservice.dto.response.QuizAttemptResponseDto;
import com.example.wellueducationservice.entity.Quiz;
import com.example.wellueducationservice.entity.QuizAttempt;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = QuizMapper.class)
public interface QuizAttemptMapper {

    @Mapping(target = "attemptId", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "quiz", source = "quiz")
    @Mapping(target = "userId", source = "request.userId")
    QuizAttempt toEntity(QuizAttemptStartRequestDto request, Quiz quiz);

    QuizAttemptResponseDto toDto(QuizAttempt attempt);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    @Mapping(target = "attemptId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "quiz", ignore = true)
    void updateAnswers(Map<UUID, Integer> answers, @MappingTarget QuizAttempt attempt);
}
