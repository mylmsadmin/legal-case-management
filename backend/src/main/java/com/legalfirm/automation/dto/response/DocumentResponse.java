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
public class DocumentResponse {
    private UUID id;
    private String name;
    private String type;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private UUID caseId;
    private String caseTitle;
    private UserResponse uploadedBy;
}