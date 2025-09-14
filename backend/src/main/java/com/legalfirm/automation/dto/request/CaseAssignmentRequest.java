package com.legalfirm.automation.dto.request;

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
public class CaseAssignmentRequest {
    @NotNull(message = "Lawyer ID is required")
    private UUID lawyerId;
    
    private String notes;
}