package com.example.wellueducationservice.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


public record AiFactResponse (
        @JsonProperty("date")
         LocalDate date,

        @JsonProperty("content")
        String content,

        @JsonProperty("category")
        String category
){}

