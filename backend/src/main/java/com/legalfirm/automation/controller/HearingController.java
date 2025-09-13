package com.legalfirm.automation.controller;

import com.legalfirm.automation.dto.request.HearingRequest;
import com.legalfirm.automation.dto.response.HearingResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.service.HearingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hearings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HearingController {

    private final HearingService hearingService;

    @GetMapping
    public ResponseEntity<List<HearingResponse>> getAllHearings() {
        return ResponseEntity.ok(hearingService.getAllHearings());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<PageResponse<HearingResponse>> getUpcomingHearings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "date"));
        return ResponseEntity.ok(hearingService.getUpcomingHearings(pageable));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<HearingResponse>> getHearingsByCase(@PathVariable UUID caseId) {
        return ResponseEntity.ok(hearingService.getHearingsByCase(caseId));
    }

    @GetMapping("/between")
    public ResponseEntity<List<HearingResponse>> getHearingsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(hearingService.getHearingsBetweenDates(startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HearingResponse> getHearingById(@PathVariable UUID id) {
        return ResponseEntity.ok(hearingService.getHearingById(id));
    }

    @PostMapping
    public ResponseEntity<HearingResponse> createHearing(@Valid @RequestBody HearingRequest request) {
        return new ResponseEntity<>(hearingService.createHearing(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HearingResponse> updateHearing(
            @PathVariable UUID id,
            @Valid @RequestBody HearingRequest request) {
        return ResponseEntity.ok(hearingService.updateHearing(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHearing(@PathVariable UUID id) {
        hearingService.deleteHearing(id);
        return ResponseEntity.noContent().build();
    }
}