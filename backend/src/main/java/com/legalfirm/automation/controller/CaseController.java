package com.legalfirm.automation.controller;

import com.legalfirm.automation.dto.request.CaseAssignmentRequest;
import com.legalfirm.automation.dto.request.CaseRequest;
import com.legalfirm.automation.dto.request.CaseStatusUpdateRequest;
import com.legalfirm.automation.dto.response.*;
import com.legalfirm.automation.enums.CaseStatus;
import com.legalfirm.automation.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CaseController {

    private final CaseService caseService;

    @GetMapping("/getAll")
    public ResponseEntity<PageResponse<CaseResponse>> getAllCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(caseService.getAllCases(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<CaseResponse>> searchCases(
            @RequestParam String search,
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(caseService.searchCases(search, status, pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<CaseResponse>> filterCases(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID lawyerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(caseService.filterCases(status, clientId, lawyerId, startDate, endDate, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<PageResponse<CaseResponse>> getCasesByStatus(
            @PathVariable CaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(caseService.getCasesByStatus(status, pageable));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<PageResponse<CaseResponse>> getCasesByClient(
            @PathVariable UUID clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(caseService.getCasesByClient(clientId, pageable));
    }

    @GetMapping("/lawyer/{lawyerId}")
    public ResponseEntity<PageResponse<CaseResponse>> getCasesByLawyer(
            @PathVariable UUID lawyerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(caseService.getCasesByLawyer(lawyerId, pageable));
    }

    @GetMapping("/my-cases")
    @PreAuthorize("hasAnyRole('LAWYER', 'ADMIN')")
    public ResponseEntity<PageResponse<CaseResponse>> getMyCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(caseService.getMyCases(pageable));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCaseStatistics() {
        return ResponseEntity.ok(caseService.getCaseStatistics());
    }

    @GetMapping("/statistics/lawyer/{lawyerId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getLawyerCaseStatistics(@PathVariable UUID lawyerId) {
        return ResponseEntity.ok(caseService.getLawyerCaseStatistics(lawyerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseDetailResponse> getCaseById(@PathVariable UUID id) {
        return ResponseEntity.ok(caseService.getCaseDetails(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CaseHistoryResponse>> getCaseHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(caseService.getCaseHistory(id));
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<List<DocumentResponse>> getCaseDocuments(@PathVariable UUID id) {
        return ResponseEntity.ok(caseService.getCaseDocuments(id));
    }

    @GetMapping("/{id}/hearings")
    public ResponseEntity<List<HearingResponse>> getCaseHearings(@PathVariable UUID id) {
        return ResponseEntity.ok(caseService.getCaseHearings(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LAWYER', 'ADMIN')")
    public ResponseEntity<CaseResponse> createCase(@Valid @RequestBody CaseRequest request) {
        return new ResponseEntity<>(caseService.createCase(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LAWYER', 'ADMIN')")
    public ResponseEntity<CaseResponse> updateCase(
            @PathVariable UUID id,
            @Valid @RequestBody CaseRequest request) {
        return ResponseEntity.ok(caseService.updateCase(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('LAWYER', 'ADMIN')")
    public ResponseEntity<CaseResponse> updateCaseStatus(
            @PathVariable UUID id,
            @Valid @RequestBody CaseStatusUpdateRequest request) {
        return ResponseEntity.ok(caseService.updateCaseStatus(id, request.getStatus(), request.getReason()));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CaseResponse> assignCase(
            @PathVariable UUID id,
            @Valid @RequestBody CaseAssignmentRequest request) {
        return ResponseEntity.ok(caseService.assignCase(id, request.getLawyerId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCase(@PathVariable UUID id) {
        caseService.deleteCase(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Case deleted successfully");
        response.put("caseId", id.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('LAWYER', 'ADMIN')")
    public ResponseEntity<byte[]> exportCases(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "PDF") String format) {

        byte[] data = caseService.exportCases(status, startDate, endDate, format);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=cases." + format.toLowerCase())
                .body(data);
    }
}