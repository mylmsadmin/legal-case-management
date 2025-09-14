package com.legalfirm.automation.repository;

import com.legalfirm.automation.entity.Case;
import com.legalfirm.automation.enums.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<Case, UUID> {
    Page<Case> findByStatus(CaseStatus status, Pageable pageable);
    Page<Case> findByClientId(UUID clientId, Pageable pageable);
    Page<Case> findByAssignedLawyerId(UUID lawyerId, Pageable pageable);
    
    @Query("SELECT c FROM Case c WHERE " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.client.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR c.status = :status)")
    Page<Case> searchCases(@Param("search") String search, 
                          @Param("status") CaseStatus status, 
                          Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Case c WHERE c.status = :status")
    long countByStatus(@Param("status") CaseStatus status);
    
    List<Case> findTop5ByOrderByCreatedAtDesc();

    Page<Case> findAll(Specification<Case> spec, Pageable pageable);

    Object countByCreatedAtAfter(LocalDateTime startOfMonth);
}
