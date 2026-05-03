package com.example.wellueducationservice.service;

import com.example.wellueducationservice.client.AiServiceClient;
import com.example.wellueducationservice.dto.response.AiFactResponse;
import com.example.wellueducationservice.dto.response.DailyFactDto;
import com.example.wellueducationservice.entity.DailyFact;
import com.example.wellueducationservice.exception.AiServiceException;
import com.example.wellueducationservice.exception.FactNotFoundException;
import com.example.wellueducationservice.repository.DailyFactRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

// service/DailyFactService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyFactService {

    private final DailyFactRepository repository;
    private final AiServiceClient aiServiceClient;

    /**
     * Fetch today's fact. Returns from DB if available; otherwise generates a new one.
     * Safe to call from multiple threads simultaneously.
     */
    @Transactional()
    public DailyFactDto getTodayFact() {
        LocalDate today = LocalDate.now();
        return repository.findByFactDate(today)
                .map(DailyFactDto::from)
                .orElseGet(() -> DailyFactDto.from(generateAndPersist(today)));
    }

    /**
     * Called by the scheduler. Idempotent — skips generation if today's fact already exists.
     */
    public void generateDailyFact() {
        LocalDate today = LocalDate.now();
        if (repository.existsByFactDate(today)) {
            log.info("Daily fact for {} already exists — skipping generation.", today);
            return;
        }
        System.out.println("Generating");
        generateAndPersist(today);
    }

    /**
     * Calls the AI service, persists the result, and returns it.
     *
     * Race-condition strategy:
     *   Two threads can both pass the existsByFactDate check above before either writes.
     *   The unique DB constraint ensures only ONE insert succeeds.
     *   The losing thread catches DataIntegrityViolationException and re-reads the winner's row.
     */
    @Transactional
    public DailyFact generateAndPersist(LocalDate date) {
        System.out.println("Starting the process");
        try {
            log.info("Calling AI service to generate fact for {}.", date);
            AiFactResponse response = aiServiceClient.generateDailyFact();

            DailyFact fact = new DailyFact();
            fact.setContent(response.content());
            fact.setCategory(response.category());
            fact.setFactDate(date);
            // createdAt and factDate filled by @PrePersist if not set

            DailyFact saved = repository.save(fact);
            log.info("Persisted daily fact [{}] for {}.", saved.getId(), date);
            return saved;

        } catch (DataIntegrityViolationException ex) {
            // Another thread won the race — read what it inserted
            log.warn("Race condition detected for {}; reading existing fact.", date);
            return repository.findByFactDate(date)
                    .orElseThrow(() -> new FactNotFoundException(
                            "Expected fact for %s after constraint violation, but none found.".formatted(date)
                    ));
        } catch (FeignException ex) {
            log.error("Feign exception calling AI service: {}", ex.getMessage(), ex);
            throw new AiServiceException("Failed to reach AI Service"+ ex);
        }
    }
}