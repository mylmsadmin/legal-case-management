package com.legalfirm.automation.dto.response;

import com.legalfirm.automation.enums.CaseStatus;
import com.legalfirm.automation.dto.request.CaseStatistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDetailResponse {
    private UUID id;
    private String title;
    private String description;
    private CaseStatus status;
    private ClientResponse client;
    private UserResponse assignedLawyer;
    private List<HearingResponse> hearings;
    private List<DocumentResponse> documents;
    private List<CaseHistoryResponse> recentHistory;
    private Integer totalHearings;
    private Integer totalDocuments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime nextHearingDate;
    private CaseStatistics statistics;
}