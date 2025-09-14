package com.legalfirm.automation.dto.response;

import com.legalfirm.automation.enums.CaseStatus;
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
    private List<CaseNoteResponse> notes;
    private List<CaseActivityResponse> activities;
    private Integer totalHearings;
    private Integer totalDocuments;
    private Integer totalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}