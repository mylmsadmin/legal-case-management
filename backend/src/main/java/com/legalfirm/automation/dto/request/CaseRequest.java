package com.legalfirm.automation.dto.request;

import com.legalfirm.automation.enums.CaseStatus;
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
public class CaseRequest {
    @NotBlank(message = "Case title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Case status is required")
    private CaseStatus status;
    
    @NotNull(message = "Client ID is required")
    private UUID clientId;
    
    private UUID assignedLawyerId;
}