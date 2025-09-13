package com.legalfirm.automation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
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
public class HearingRequest {
    @NotNull(message = "Hearing date is required")
    @Future(message = "Hearing date must be in the future")
    private LocalDateTime date;
    
    private String notes;
    
    @NotNull(message = "Case ID is required")
    private UUID caseId;
}
