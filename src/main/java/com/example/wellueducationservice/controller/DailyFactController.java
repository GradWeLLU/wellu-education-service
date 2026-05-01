package com.example.wellueducationservice.controller;

import com.example.wellueducationservice.dto.response.DailyFactDto;
import com.example.wellueducationservice.service.DailyFactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/daily-facts")
@RequiredArgsConstructor
public class DailyFactController {

    private final DailyFactService dailyFactService;

    /**
     * GET /api/v1/daily-facts/today
     * Returns today's fact (from DB or freshly generated).
     */
    @GetMapping("/today")
    public ResponseEntity<DailyFactDto> getTodayFact() {
        DailyFactDto fact = dailyFactService.getTodayFact();
        return ResponseEntity.ok(fact);
    }
}
