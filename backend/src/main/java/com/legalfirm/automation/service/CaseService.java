package com.legalfirm.automation.service;

import com.legalfirm.automation.dto.request.CaseRequest;
import com.legalfirm.automation.dto.request.CaseStatistics;
import com.legalfirm.automation.dto.response.*;
import com.legalfirm.automation.entity.*;
import com.legalfirm.automation.enums.CaseStatus;
import com.legalfirm.automation.enums.Role;
import com.legalfirm.automation.exception.BadRequestException;
import com.legalfirm.automation.exception.ResourceNotFoundException;
import com.legalfirm.automation.exception.UnauthorizedException;
import com.legalfirm.automation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseService {

    private final CaseRepository caseRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final CaseHistoryRepository caseHistoryRepository;
    private final DocumentRepository documentRepository;
    private final HearingRepository hearingRepository;

    public PageResponse<CaseResponse> getAllCases(Pageable pageable) {
        Page<Case> casePage = caseRepository.findAll(pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> searchCases(String search, CaseStatus status, Pageable pageable) {
        Page<Case> casePage = caseRepository.searchCases(search, status, pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> filterCases(
            CaseStatus status,
            UUID clientId,
            UUID lawyerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        Specification<Case> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        if (clientId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("client").get("id"), clientId));
        }

        if (lawyerId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("assignedLawyer").get("id"), lawyerId));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("createdAt"), startDate, endDate));
        }

        Page<Case> casePage = caseRepository.findAll(spec, pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> getCasesByStatus(CaseStatus status, Pageable pageable) {
        Page<Case> casePage = caseRepository.findByStatus(status, pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> getCasesByClient(UUID clientId, Pageable pageable) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }
        Page<Case> casePage = caseRepository.findByClientId(clientId, pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> getCasesByLawyer(UUID lawyerId, Pageable pageable) {
        if (!userRepository.existsById(lawyerId)) {
            throw new ResourceNotFoundException("Lawyer not found with id: " + lawyerId);
        }
        Page<Case> casePage = caseRepository.findByAssignedLawyerId(lawyerId, pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> getMyCases(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Case> casePage = caseRepository.findByAssignedLawyerId(currentUser.getId(), pageable);
        return mapToPageResponse(casePage);
    }

    public CaseDetailResponse getCaseDetails(UUID id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));

        List<Document> documents = documentRepository.findByCaseEntityId(id);
        List<Hearing> hearings = hearingRepository.findByCaseEntityId(id);
        List<CaseHistory> recentHistory = caseHistoryRepository.findByCaseEntityIdOrderByTimestampDesc(id)
                .stream().limit(10).collect(Collectors.toList());

        LocalDateTime nextHearingDate = hearings.stream()
                .filter(h -> h.getDate().isAfter(LocalDateTime.now()))
                .map(Hearing::getDate)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        CaseStatistics statistics = buildCaseStatistics(caseEntity, documents, hearings);

        return CaseDetailResponse.builder()
                .id(caseEntity.getId())
                .title(caseEntity.getTitle())
                .description(caseEntity.getDescription())
                .status(caseEntity.getStatus())
                .client(mapToClientResponse(caseEntity.getClient()))
                .assignedLawyer(caseEntity.getAssignedLawyer() != null ?
                        mapToUserResponse(caseEntity.getAssignedLawyer()) : null)
                .hearings(hearings.stream().map(this::mapToHearingResponse).collect(Collectors.toList()))
                .documents(documents.stream().map(this::mapToDocumentResponse).collect(Collectors.toList()))
                .recentHistory(recentHistory.stream().map(this::mapToHistoryResponse).collect(Collectors.toList()))
                .totalHearings(hearings.size())
                .totalDocuments(documents.size())
                .createdAt(caseEntity.getCreatedAt())
                .updatedAt(caseEntity.getUpdatedAt())
                .nextHearingDate(nextHearingDate)
                .statistics(statistics)
                .build();
    }

    public List<CaseHistoryResponse> getCaseHistory(UUID caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case not found with id: " + caseId);
        }
        return caseHistoryRepository.findByCaseEntityIdOrderByTimestampDesc(caseId)
                .stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());
    }

    public List<DocumentResponse> getCaseDocuments(UUID caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case not found with id: " + caseId);
        }
        return documentRepository.findByCaseEntityId(caseId)
                .stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    public List<HearingResponse> getCaseHearings(UUID caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case not found with id: " + caseId);
        }
        return hearingRepository.findByCaseEntityId(caseId)
                .stream()
                .map(this::mapToHearingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CaseResponse createCase(CaseRequest request) {
        validateCaseRequest(request);

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + request.getClientId()));

        User assignedLawyer = null;
        if (request.getAssignedLawyerId() != null) {
            assignedLawyer = userRepository.findById(request.getAssignedLawyerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found with id: " + request.getAssignedLawyerId()));
            validateLawyerRole(assignedLawyer);
        }

        Case caseEntity = Case.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .client(client)
                .assignedLawyer(assignedLawyer)
                .build();

        Case savedCase = caseRepository.save(caseEntity);

        // Create history entry
        createHistoryEntry(savedCase, "CASE_CREATED", "Case created", null, savedCase.getTitle());

        return mapToCaseResponse(savedCase);
    }

    @Transactional
    public CaseResponse updateCase(UUID id, CaseRequest request) {
        validateCaseRequest(request);

        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));

        // Track changes
        String oldTitle = caseEntity.getTitle();
        String oldStatus = caseEntity.getStatus().toString();
        UUID oldLawyerId = caseEntity.getAssignedLawyer() != null ? caseEntity.getAssignedLawyer().getId() : null;

        if (request.getClientId() != null && !request.getClientId().equals(caseEntity.getClient().getId())) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + request.getClientId()));
            caseEntity.setClient(client);
            createHistoryEntry(caseEntity, "CLIENT_CHANGED", "Client changed",
                    caseEntity.getClient().getName(), client.getName());
        }

        if (request.getAssignedLawyerId() != null && !request.getAssignedLawyerId().equals(oldLawyerId)) {
            User assignedLawyer = userRepository.findById(request.getAssignedLawyerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found with id: " + request.getAssignedLawyerId()));
            validateLawyerRole(assignedLawyer);

            String oldLawyerName = caseEntity.getAssignedLawyer() != null ?
                    caseEntity.getAssignedLawyer().getName() : "Unassigned";
            caseEntity.setAssignedLawyer(assignedLawyer);
            createHistoryEntry(caseEntity, "LAWYER_ASSIGNED", "Lawyer assignment changed",
                    oldLawyerName, assignedLawyer.getName());
        }

        if (!oldTitle.equals(request.getTitle())) {
            createHistoryEntry(caseEntity, "TITLE_CHANGED", "Case title updated",
                    oldTitle, request.getTitle());
        }

        if (!oldStatus.equals(request.getStatus().toString())) {
            createHistoryEntry(caseEntity, "STATUS_CHANGED", "Status updated",
                    oldStatus, request.getStatus().toString());
        }

        caseEntity.setTitle(request.getTitle());
        caseEntity.setDescription(request.getDescription());
        caseEntity.setStatus(request.getStatus());

        Case updatedCase = caseRepository.save(caseEntity);
        return mapToCaseResponse(updatedCase);
    }

    @Transactional
    public CaseResponse updateCaseStatus(UUID id, CaseStatus newStatus, String reason) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));

        validateStatusTransition(caseEntity.getStatus(), newStatus);

        String oldStatus = caseEntity.getStatus().toString();
        caseEntity.setStatus(newStatus);
        Case updatedCase = caseRepository.save(caseEntity);

        String description = "Status changed from " + oldStatus + " to " + newStatus;
        if (reason != null && !reason.isEmpty()) {
            description += ". Reason: " + reason;
        }

        createHistoryEntry(updatedCase, "STATUS_CHANGED", description, oldStatus, newStatus.toString());

        return mapToCaseResponse(updatedCase);
    }

    @Transactional
    public CaseResponse assignCase(UUID id, UUID lawyerId) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));

        User lawyer = userRepository.findById(lawyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found with id: " + lawyerId));

        validateLawyerRole(lawyer);

        String oldLawyerName = caseEntity.getAssignedLawyer() != null ?
                caseEntity.getAssignedLawyer().getName() : "Unassigned";

        caseEntity.setAssignedLawyer(lawyer);
        Case updatedCase = caseRepository.save(caseEntity);

        createHistoryEntry(updatedCase, "LAWYER_ASSIGNED",
                "Case assigned to " + lawyer.getName(), oldLawyerName, lawyer.getName());

        return mapToCaseResponse(updatedCase);
    }

    @Transactional
    public void deleteCase(UUID id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));

        // Validate deletion
        if (caseEntity.getStatus() != CaseStatus.CLOSED) {
            throw new BadRequestException("Only closed cases can be deleted");
        }

        if (!caseEntity.getHearings().isEmpty()) {
            throw new BadRequestException("Cannot delete case with scheduled hearings");
        }

        caseRepository.deleteById(id);
        log.info("Case {} deleted by user {}", id, getCurrentUser().getEmail());
    }

    public Map<String, Object> getCaseStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", caseRepository.count());
        stats.put("open", caseRepository.countByStatus(CaseStatus.OPEN));
        stats.put("pending", caseRepository.countByStatus(CaseStatus.PENDING));
        stats.put("closed", caseRepository.countByStatus(CaseStatus.CLOSED));

        // Monthly statistics
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        stats.put("casesThisMonth", caseRepository.countByCreatedAtAfter(startOfMonth));

        // Average case duration
        stats.put("averageDuration", calculateAverageCaseDuration());

        return stats;
    }

    public Map<String, Object> getLawyerCaseStatistics(UUID lawyerId) {
        Map<String, Object> stats = new HashMap<>();

//        stats.put("totalAssigned", caseRepository.countByAssignedLawyerId(lawyerId));
//        stats.put("openCases", caseRepository.countByAssignedLawyerIdAndStatus(lawyerId, CaseStatus.OPEN));
//        stats.put("pendingCases", caseRepository.countByAssignedLawyerIdAndStatus(lawyerId, CaseStatus.PENDING));
//        stats.put("closedCases", caseRepository.countByAssignedLawyerIdAndStatus(lawyerId, CaseStatus.CLOSED));

        return stats;
    }

    public byte[] exportCases(CaseStatus status, LocalDateTime startDate, LocalDateTime endDate, String format) {
        // Implementation for export functionality
        // This would typically use a library like Apache POI for Excel or iText for PDF
        log.info("Exporting cases in {} format", format);

        // Placeholder implementation
        return "Export data".getBytes();
    }

    // Helper methods
    private void validateCaseRequest(CaseRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Case title is required");
        }
        if (request.getClientId() == null) {
            throw new BadRequestException("Client ID is required");
        }
        if (request.getStatus() == null) {
            throw new BadRequestException("Case status is required");
        }
    }

    private void validateLawyerRole(User user) {
        if (!Role.LAWYER.equals(user.getRole()) && !Role.ADMIN.equals(user.getRole())) {
            throw new BadRequestException("Assigned user must be a lawyer or admin");
        }
    }

    private void validateStatusTransition(CaseStatus currentStatus, CaseStatus newStatus) {
        // Define allowed transitions
        if (currentStatus == CaseStatus.CLOSED && newStatus != CaseStatus.CLOSED) {
            throw new BadRequestException("Cannot reopen a closed case");
        }

        if (currentStatus == CaseStatus.OPEN && newStatus == CaseStatus.CLOSED) {
            throw new BadRequestException("Case must be in PENDING status before closing");
        }
    }

    @Transactional
    private void createHistoryEntry(Case caseEntity, String action, String description,
                                    String oldValue, String newValue) {
        CaseHistory history = CaseHistory.builder()
                .caseEntity(caseEntity)
                .action(action)
                .description(description)
                .performedBy(getCurrentUser())
                .oldValue(oldValue)
                .newValue(newValue)
                .category("CASE_UPDATE")
                .build();

        caseHistoryRepository.save(history);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    private CaseStatistics buildCaseStatistics(Case caseEntity, List<Document> documents, List<Hearing> hearings) {
        long daysOpen = ChronoUnit.DAYS.between(caseEntity.getCreatedAt(), LocalDateTime.now());
        long upcomingHearings = hearings.stream()
                .filter(h -> h != null && h.getDate().isAfter(LocalDateTime.now()))
                .count();
        long completedHearings = hearings.size() - upcomingHearings;

        return CaseStatistics.builder()
                .totalDocuments(documents.size())
                .totalHearings(hearings.size())
                .completedHearings((int) completedHearings)
                .upcomingHearings((int) upcomingHearings)
                .daysOpen((int) daysOpen)
                .estimatedCompletion(null) // Could be calculated based on similar cases
                .priority(calculatePriority(caseEntity))
                .relatedCases(0) // Could be implemented to find related cases
                .build();
    }

    private String calculatePriority(Case caseEntity) {
        if (caseEntity.getStatus() == CaseStatus.OPEN) {
            long daysOpen = ChronoUnit.DAYS.between(caseEntity.getCreatedAt(), LocalDateTime.now());
            if (daysOpen > 90) return "HIGH";
            if (daysOpen > 30) return "MEDIUM";
            return "LOW";
        }
        return "NORMAL";
    }

    private Double calculateAverageCaseDuration() {
        List<Case> closedCases = caseRepository.findByStatus(CaseStatus.CLOSED, Pageable.unpaged()).getContent();
        if (closedCases.isEmpty()) return 0.0;

        double totalDays = closedCases.stream()
                .mapToLong(c -> ChronoUnit.DAYS.between(c.getCreatedAt(), c.getUpdatedAt()))
                .sum();

        return totalDays / closedCases.size();
    }

    // Mapping methods
    private CaseResponse mapToCaseResponse(Case caseEntity) {
        return CaseResponse.builder()
                .id(caseEntity.getId())
                .title(caseEntity.getTitle())
                .description(caseEntity.getDescription())
                .status(caseEntity.getStatus())
                .client(mapToClientResponse(caseEntity.getClient()))
                .assignedLawyer(caseEntity.getAssignedLawyer() != null ?
                        mapToUserResponse(caseEntity.getAssignedLawyer()) : null)
                .totalHearings(caseEntity.getHearings() != null ? caseEntity.getHearings().size() : 0)
                .totalDocuments(caseEntity.getDocuments() != null ? caseEntity.getDocuments().size() : 0)
                .createdAt(caseEntity.getCreatedAt())
                .updatedAt(caseEntity.getUpdatedAt())
                .build();
    }

    private ClientResponse mapToClientResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .contactInfo(client.getContactInfo())
                .address(client.getAddress())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .contactNumber(user.getContactNumber())
                .build();
    }

    private HearingResponse mapToHearingResponse(Hearing hearing) {
        return HearingResponse.builder()
                .id(hearing.getId())
                .date(hearing.getDate())
                .notes(hearing.getNotes())
                .caseId(hearing.getCaseEntity().getId())
                .caseTitle(hearing.getCaseEntity().getTitle())
                .clientName(hearing.getCaseEntity().getClient().getName())
                .createdAt(hearing.getCreatedAt())
                .build();
    }

    private DocumentResponse mapToDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .type(document.getType())
                .fileSize(document.getFileSize())
                .uploadDate(document.getUploadDate())
                .caseId(document.getCaseEntity().getId())
                .caseTitle(document.getCaseEntity().getTitle())
                .uploadedBy(document.getUploadedBy() != null ?
                        mapToUserResponse(document.getUploadedBy()) : null)
                .build();
    }

    private CaseHistoryResponse mapToHistoryResponse(CaseHistory history) {
        return CaseHistoryResponse.builder()
                .id(history.getId())
                .action(history.getAction())
                .description(history.getDescription())
                .performedBy(history.getPerformedBy() != null ?
                        history.getPerformedBy().getName() : "System")
                .timestamp(history.getTimestamp())
                .oldValue(history.getOldValue())
                .newValue(history.getNewValue())
                .category(history.getCategory())
                .build();
    }

    private PageResponse<CaseResponse> mapToPageResponse(Page<Case> page) {
        return PageResponse.<CaseResponse>builder()
                .content(page.getContent().stream().map(this::mapToCaseResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}