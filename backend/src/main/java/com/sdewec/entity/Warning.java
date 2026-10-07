package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "warnings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HazardReport.HazardType hazardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarningLevel warningLevel;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(columnDefinition = "TEXT")
    private String emergencyInstructions;

    @Column(nullable = false)
    private String affectedArea;

    private Double affectedLatitude;
    private Double affectedLongitude;
    private Double affectedRadius; // in km

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WarningStatus status = WarningStatus.DRAFT;

    // Version tracking for every update
    @Builder.Default
    private Integer version = 1;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime issuedAt;

    // Notification channels selected
    @Column(columnDefinition = "VARCHAR(255)")
    private String notificationChannels; // comma-separated e.g. "PUSH_NOTIFICATION,SMS"

    // Link to source hazard report (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_report_id")
    private HazardReport sourceReport;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum WarningLevel {
        INFORMATION,
        ADVISORY,
        WATCH,
        WARNING,
        CRITICAL
    }

    public enum WarningStatus {
        DRAFT,
        PENDING_APPROVAL,
        APPROVED,
        BROADCASTING,
        ACTIVE,
        EXPIRED,
        CANCELLED
    }
}
