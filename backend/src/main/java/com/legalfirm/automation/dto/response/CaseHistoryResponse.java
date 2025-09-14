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
public class CaseHistoryResponse {
    private UUID id;
    private String action;
    private String description;
    private String performedBy;
    private LocalDateTime timestamp;
    private String oldValue;
    private String newValue;
    private String category;
}