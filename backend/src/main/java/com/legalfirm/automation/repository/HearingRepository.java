package com.legalfirm.automation.repository;

import com.legalfirm.automation.entity.Hearing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HearingRepository extends JpaRepository<Hearing, UUID> {
    List<Hearing> findByCaseEntityId(UUID caseId);
    Page<Hearing> findByCaseEntityId(UUID caseId, Pageable pageable);
    
    @Query("SELECT h FROM Hearing h WHERE h.date >= :startDate AND h.date <= :endDate ORDER BY h.date")
    List<Hearing> findHearingsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT h FROM Hearing h WHERE h.date >= CURRENT_TIMESTAMP ORDER BY h.date")
    List<Hearing> findUpcomingHearings();
    
    @Query("SELECT h FROM Hearing h WHERE h.date >= CURRENT_TIMESTAMP ORDER BY h.date")
    Page<Hearing> findUpcomingHearings(Pageable pageable);
}