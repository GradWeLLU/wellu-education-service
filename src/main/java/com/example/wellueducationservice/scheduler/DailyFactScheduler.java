package com.example.wellueducationservice.scheduler;

import com.example.wellueducationservice.exception.AiServiceException;
import com.example.wellueducationservice.service.DailyFactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// scheduler/DailyFactScheduler.java
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyFactScheduler {

    private final DailyFactService dailyFactService;

    /**
     * Fires every day at midnight (00:00:00) server time.
     * zone = "UTC" keeps behaviour consistent across environments.
     *
     * For distributed deployments (multiple instances), couple this with
     * ShedLock (@SchedulerLock) so only ONE pod executes per trigger.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void scheduleDailyFactGeneration() {
        log.info("Scheduler triggered: generating daily fact.");
        try {
            dailyFactService.generateDailyFact();
            log.info("Scheduler: daily fact generation completed.");
        } catch (AiServiceException ex) {
            // Log and swallow — scheduler must not crash; alert monitoring instead
            log.error("Scheduler: AI service unavailable. Fact generation failed. " +
                    "Fallback will apply when first user requests today's fact.", ex);
        } catch (Exception ex) {
            log.error("Scheduler: unexpected error during fact generation.", ex);
        }
    }
}