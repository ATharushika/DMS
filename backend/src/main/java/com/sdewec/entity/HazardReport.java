package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "hazard_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HazardReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HazardType hazardType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private String photoUrl;

    private Double latitude;

    private Double longitude;

    private String locationName;

    @Column(updatable = false)
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportStatus status = ReportStatus.SUBMITTED;

    // Credibility score 0-100 (calculated, not ML)
    private Integer credibilityScore;

    // For duplicate detection: nearby reports reference
    private Long possibleDuplicateId;

    @PrePersist
    protected void onCreate() {
        reportedAt = LocalDateTime.now();
    }

    public enum HazardType {
        FLOOD, LANDSLIDE, CYCLONE, TSUNAMI, EARTHQUAKE,
        FIRE, DROUGHT, INDUSTRIAL_ACCIDENT, OTHER
    }

    public enum ReportStatus {
        SUBMITTED,
        UNDER_REVIEW,
        VERIFIED,
        REJECTED,
        MORE_INFORMATION_REQUIRED,
        PENDING_SYNC  // offline mode simulation
    }
}
