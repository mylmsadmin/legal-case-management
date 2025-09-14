package com.legalfirm.automation.repository;

import com.legalfirm.automation.entity.CaseNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseNoteRepository extends JpaRepository<CaseNote, UUID> {
    List<CaseNote> findByCaseEntityIdOrderByCreatedAtDesc(UUID caseId);
    
    Page<CaseNote> findByCaseEntityId(UUID caseId, Pageable pageable);
    
    @Query("SELECT n FROM CaseNote n WHERE n.caseEntity.id = :caseId AND (n.isPrivate = false OR n.createdBy.id = :userId)")
    List<CaseNote> findVisibleNotes(@Param("caseId") UUID caseId, @Param("userId") UUID userId);
    
    List<CaseNote> findByCreatedByIdOrderByCreatedAtDesc(UUID userId);
}
