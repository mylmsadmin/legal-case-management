package com.legalfirm.automation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseNoteResponse {
    private UUID id;
    private String content;
    private Boolean isPrivate;
    private UserResponse createdBy;
    private UUID caseId;
    private String caseTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}