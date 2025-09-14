package com.legalfirm.automation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseStatistics {
    private Integer totalDocuments;
    private Integer totalHearings;
    private Integer completedHearings;
    private Integer upcomingHearings;
    private Integer daysOpen;
    private Double estimatedCompletion;
    private String priority;
    private Integer relatedCases;
}