package com.legalfirm.automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hearings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hearing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hearing_id")
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}