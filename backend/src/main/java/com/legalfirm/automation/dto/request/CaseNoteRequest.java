package com.legalfirm.automation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseNoteRequest {
    @NotNull(message = "Case ID is required")
    private UUID caseId;
    
    @NotBlank(message = "Note content is required")
    private String content;
    
    @Builder.Default
    private Boolean isPrivate = false;
}