package com.legalfirm.automation.repository;

import com.legalfirm.automation.entity.CaseActivity;
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
public interface CaseActivityRepository extends JpaRepository<CaseActivity, UUID> {
    List<CaseActivity> findByCaseEntityIdOrderByActivityDateDesc(UUID caseId);
    
    Page<CaseActivity> findByCaseEntityId(UUID caseId, Pageable pageable);
    
    @Query("SELECT a FROM CaseActivity a WHERE a.caseEntity.id = :caseId AND a.activityDate BETWEEN :startDate AND :endDate ORDER BY a.activityDate DESC")
    List<CaseActivity> findByCaseIdAndDateRange(
            @Param("caseId") UUID caseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT a FROM CaseActivity a WHERE a.performedBy.id = :userId ORDER BY a.activityDate DESC")
    Page<CaseActivity> findByPerformedBy(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM CaseActivity a WHERE a.caseEntity.id = :caseId AND a.activityType = :activityType")
    long countByTypeForCase(@Param("caseId") UUID caseId, @Param("activityType") String activityType);
}