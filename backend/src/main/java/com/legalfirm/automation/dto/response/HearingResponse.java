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
public class HearingResponse {
    private UUID id;
    private LocalDateTime date;
    private String notes;
    private UUID caseId;
    private String caseTitle;
    private String clientName;
    private LocalDateTime createdAt;
}