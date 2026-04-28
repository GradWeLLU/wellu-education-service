package com.example.wellueducationservice.mapper;

import com.example.wellueducationservice.dto.response.QuestionResponseDto;
import com.example.wellueducationservice.entity.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionResponseDto toDto(Question question);
}
