package com.legalfirm.automation.controller;

import com.legalfirm.automation.dto.response.CaseResponse;
import com.legalfirm.automation.dto.response.HearingResponse;
import com.legalfirm.automation.service.CaseService;
import com.legalfirm.automation.service.ClientService;
import com.legalfirm.automation.service.HearingService;
import com.legalfirm.automation.repository.CaseRepository;
import com.legalfirm.automation.repository.ClientRepository;
import com.legalfirm.automation.repository.HearingRepository;
import com.legalfirm.automation.enums.CaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    private final CaseRepository caseRepository;
    private final ClientRepository clientRepository;
    private final HearingRepository hearingRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Get case statistics
        long totalCases = caseRepository.count();
        long openCases = caseRepository.countByStatus(CaseStatus.OPEN);
        long pendingCases = caseRepository.countByStatus(CaseStatus.PENDING);
        long closedCases = caseRepository.countByStatus(CaseStatus.CLOSED);

        // Get client count
        long totalClients = clientRepository.count();

        // Get upcoming hearings count
        long upcomingHearings = hearingRepository.findUpcomingHearings().size();

        stats.put("totalCases", totalCases);
        stats.put("openCases", openCases);
        stats.put("pendingCases", pendingCases);
        stats.put("closedCases", closedCases);
        stats.put("totalClients", totalClients);
        stats.put("upcomingHearings", upcomingHearings);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-cases")
    public ResponseEntity<List<Map<String, Object>>> getRecentCases() {
        List<com.legalfirm.automation.entity.Case> recentCases = caseRepository.findTop5ByOrderByCreatedAtDesc();

        List<Map<String, Object>> response = recentCases.stream().map(case_ -> {
            Map<String, Object> caseMap = new HashMap<>();
            caseMap.put("id", case_.getId());
            caseMap.put("title", case_.getTitle());
            caseMap.put("status", case_.getStatus());
            caseMap.put("client", case_.getClient().getName());
            caseMap.put("createdAt", case_.getCreatedAt());
            return caseMap;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming-hearings")
    public ResponseEntity<List<Map<String, Object>>> getUpcomingHearings() {
        List<com.legalfirm.automation.entity.Hearing> upcomingHearings = hearingRepository.findUpcomingHearings()
                .stream()
                .limit(5)
                .toList();

        List<Map<String, Object>> response = upcomingHearings.stream().map(hearing -> {
            Map<String, Object> hearingMap = new HashMap<>();
            hearingMap.put("id", hearing.getId());
            hearingMap.put("date", hearing.getDate());
            hearingMap.put("case", hearing.getCaseEntity().getTitle());
            hearingMap.put("client", hearing.getCaseEntity().getClient().getName());
            hearingMap.put("notes", hearing.getNotes());
            return hearingMap;
        }).toList();

        return ResponseEntity.ok(response);
    }
}