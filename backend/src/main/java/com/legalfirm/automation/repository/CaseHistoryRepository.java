package com.legalfirm.automation.repository;

import com.legalfirm.automation.entity.CaseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CaseHistoryRepository extends JpaRepository<CaseHistory, UUID> {
    
    List<CaseHistory> findByCaseEntityIdOrderByTimestampDesc(UUID caseId);
    
    Page<CaseHistory> findByCaseEntityId(UUID caseId, Pageable pageable);
    
    @Query("SELECT ch FROM CaseHistory ch WHERE ch.caseEntity.id = :caseId " +
           "AND ch.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY ch.timestamp DESC")
    List<CaseHistory> findByCaseIdAndDateRange(
            @Param("caseId") UUID caseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ch FROM CaseHistory ch WHERE ch.performedBy.id = :userId " +
           "ORDER BY ch.timestamp DESC")
    Page<CaseHistory> findByPerformedBy(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT ch FROM CaseHistory ch WHERE ch.caseEntity.id = :caseId " +
           "AND ch.category = :category ORDER BY ch.timestamp DESC")
    List<CaseHistory> findByCaseIdAndCategory(
            @Param("caseId") UUID caseId,
            @Param("category") String category);
}