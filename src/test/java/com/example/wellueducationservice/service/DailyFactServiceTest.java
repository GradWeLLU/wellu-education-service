package com.example.wellueducationservice.service;

import com.example.wellueducationservice.client.AiServiceClient;
import com.example.wellueducationservice.dto.response.AiFactResponse;
import com.example.wellueducationservice.dto.response.DailyFactDto;
import com.example.wellueducationservice.entity.DailyFact;
import com.example.wellueducationservice.exception.AiServiceException;
import com.example.wellueducationservice.repository.DailyFactRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyFactServiceTest {

    @Mock private DailyFactRepository repository;
    @Mock private AiServiceClient aiServiceClient;

    @InjectMocks
    private DailyFactService dailyFactService;

    private LocalDate today;
    private DailyFact existingFact;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();

        existingFact = new DailyFact();
        existingFact.setContent("Drinking water boosts metabolism.");
        existingFact.setCategory("Hydration");
        existingFact.setFactDate(today);
    }

    // ─── getTodayFact ─────────────────────────────────────────────────────────

    @Test
    void getTodayFact_factExistsInDb_returnsWithoutCallingAi() {
        when(repository.findByFactDate(today)).thenReturn(Optional.of(existingFact));

        DailyFactDto result = dailyFactService.getTodayFact();

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Drinking water boosts metabolism.");
        verifyNoInteractions(aiServiceClient);
    }

    @Test
    void getTodayFact_noFactInDb_callsAiAndPersists() {
        AiFactResponse aiResponse = new AiFactResponse(today,"Sleep improves muscle recovery.", "Sleep");
        DailyFact savedFact = new DailyFact();
        savedFact.setContent("Sleep improves muscle recovery.");
        savedFact.setCategory("Sleep");
        savedFact.setFactDate(today);

        when(repository.findByFactDate(today)).thenReturn(Optional.empty());
        when(aiServiceClient.generateDailyFact()).thenReturn(aiResponse);
        when(repository.save(any(DailyFact.class))).thenReturn(savedFact);

        DailyFactDto result = dailyFactService.getTodayFact();

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Sleep improves muscle recovery.");
        verify(aiServiceClient).generateDailyFact();
        verify(repository).save(any(DailyFact.class));
    }

    // ─── generateDailyFact ────────────────────────────────────────────────────

    @Test
    void generateDailyFact_factAlreadyExists_skipsGeneration() {
        when(repository.existsByFactDate(today)).thenReturn(true);

        dailyFactService.generateDailyFact();

        verifyNoInteractions(aiServiceClient);
        verify(repository, never()).save(any());
    }

    @Test
    void generateDailyFact_noFactExists_callsAiAndPersists() {
        AiFactResponse aiResponse = new AiFactResponse(today, "Exercise boosts mood.", "Fitness");
        DailyFact savedFact = new DailyFact();
        savedFact.setContent("Exercise boosts mood.");
        savedFact.setCategory("Fitness");
        savedFact.setFactDate(today);

        when(repository.existsByFactDate(today)).thenReturn(false);
        when(aiServiceClient.generateDailyFact()).thenReturn(aiResponse);
        when(repository.save(any(DailyFact.class))).thenReturn(savedFact);

        dailyFactService.generateDailyFact();

        verify(aiServiceClient).generateDailyFact();
        verify(repository).save(any(DailyFact.class));
    }

    // ─── generateAndPersist ──────────────────────────────────────────────────

    @Test
    void generateAndPersist_aiCallSucceeds_returnsPersistedFact() {
        AiFactResponse aiResponse = new AiFactResponse(today, "Fiber aids digestion.", "Nutrition");
        DailyFact savedFact = new DailyFact();
        savedFact.setContent("Fiber aids digestion.");
        savedFact.setCategory("Nutrition");
        savedFact.setFactDate(today);

        when(aiServiceClient.generateDailyFact()).thenReturn(aiResponse);
        when(repository.save(any(DailyFact.class))).thenReturn(savedFact);

        DailyFact result = dailyFactService.generateAndPersist(today);

        assertThat(result.getContent()).isEqualTo("Fiber aids digestion.");
        assertThat(result.getCategory()).isEqualTo("Nutrition");
        verify(repository).save(any(DailyFact.class));
    }

    @Test
    void generateAndPersist_raceCondition_readsExistingFactAfterConstraintViolation() {
        AiFactResponse aiResponse = new AiFactResponse(today, "Some fact.", "Health");

        when(aiServiceClient.generateDailyFact()).thenReturn(aiResponse);
        when(repository.save(any(DailyFact.class))).thenThrow(new DataIntegrityViolationException("duplicate"));
        when(repository.findByFactDate(today)).thenReturn(Optional.of(existingFact));

        DailyFact result = dailyFactService.generateAndPersist(today);

        assertThat(result.getContent()).isEqualTo("Drinking water boosts metabolism.");
        verify(repository).findByFactDate(today);
    }

    @Test
    void generateAndPersist_aiServiceDown_throwsAiServiceException() {
        FeignException feignException = mock(FeignException.class);
        when(aiServiceClient.generateDailyFact()).thenThrow(feignException);

        assertThatThrownBy(() -> dailyFactService.generateAndPersist(today))
                .isInstanceOf(AiServiceException.class)
                .hasMessageContaining("Failed to reach AI Service");

        verify(repository, never()).save(any());
    }
}