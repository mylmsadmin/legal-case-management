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
public class CaseActivityResponse {
    private UUID id;
    private String activityType;
    private String description;
    private String oldValue;
    private String newValue;
    private UUID caseId;
    private String caseTitle;
    private UserResponse performedBy;
    private LocalDateTime activityDate;
}