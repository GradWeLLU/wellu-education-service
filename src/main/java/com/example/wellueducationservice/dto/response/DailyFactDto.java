package com.example.wellueducationservice.dto.response;


import com.example.wellueducationservice.entity.DailyFact;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyFactDto {

    private UUID id;
    private String content;
    private String category;
    private LocalDate factDate;
    private LocalDateTime createdAt;

    public static DailyFactDto from(DailyFact fact) {
        return DailyFactDto.builder()
                .id(fact.getId())
                .content(fact.getContent())
                .category(fact.getCategory())
                .factDate(fact.getFactDate())
                .createdAt(fact.getCreatedAt())
                .build();
    }
}