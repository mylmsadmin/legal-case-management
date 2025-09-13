package com.legalfirm.automation.repository;

import com.legalfirm.automation.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByCaseEntityId(UUID caseId);
    Page<Document> findByCaseEntityId(UUID caseId, Pageable pageable);
    List<Document> findByUploadedById(UUID userId);
}
