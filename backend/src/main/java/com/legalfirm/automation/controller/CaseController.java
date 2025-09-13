package com.legalfirm.automation.controller;

import com.legalfirm.automation.dto.request.CaseRequest;
import com.legalfirm.automation.dto.response.CaseResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.enums.CaseStatus;
import com.legalfirm.automation.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @GetMapping("/")
    public ResponseEntity<PageResponse<CaseResponse>> searchCases(
            @RequestParam String search,
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(caseService.searchCases(search, status, pageable));
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

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getCaseStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("open", caseService.getCountByStatus(CaseStatus.OPEN));
        stats.put("closed", caseService.getCountByStatus(CaseStatus.CLOSED));
        stats.put("pending", caseService.getCountByStatus(CaseStatus.PENDING));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable UUID id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    @PostMapping
    public ResponseEntity<CaseResponse> createCase(@Valid @RequestBody CaseRequest request) {
        return new ResponseEntity<>(caseService.createCase(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaseResponse> updateCase(
            @PathVariable UUID id,
            @Valid @RequestBody CaseRequest request) {
        return ResponseEntity.ok(caseService.updateCase(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable UUID id) {
        caseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }
}