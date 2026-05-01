package com.example.wellueducationservice.repository;

import com.example.wellueducationservice.entity.DailyFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyFactRepository extends JpaRepository<DailyFact, UUID> {

    Optional<DailyFact> findByFactDate(LocalDate factDate);

    boolean existsByFactDate(LocalDate factDate);
}