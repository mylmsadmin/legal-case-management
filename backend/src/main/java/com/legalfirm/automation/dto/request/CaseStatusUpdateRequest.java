package com.legalfirm.automation.dto.request;

import com.legalfirm.automation.enums.CaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseStatusUpdateRequest {
    @NotNull(message = "Case status is required")
    private CaseStatus status;
    
    private String reason;
}