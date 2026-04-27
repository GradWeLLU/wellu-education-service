package com.example.wellueducationservice.mapper;

import com.example.wellueducationservice.dto.response.QuizResponseDto;
import com.example.wellueducationservice.entity.Quiz;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = QuestionMapper.class)
public interface QuizMapper {

    QuizResponseDto toDto(Quiz quiz);
}
