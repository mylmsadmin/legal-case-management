package com.legalfirm.automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "case_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "activity_type", nullable = false)
    private String activityType; // CREATED, STATUS_CHANGED, LAWYER_ASSIGNED, DOCUMENT_ADDED, HEARING_SCHEDULED, NOTE_ADDED

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @Column(name = "activity_date", nullable = false)
    private LocalDateTime activityDate;

    @PrePersist
    protected void onCreate() {
        activityDate = LocalDateTime.now();
    }
}