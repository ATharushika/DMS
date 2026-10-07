package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "rescue_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescueAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescue_team_id", nullable = false)
    private RescueTeam rescueTeam;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;

    // Mock current team location during assignment
    private Double currentLatitude;
    private Double currentLongitude;

    @Column(updatable = false)
    private LocalDateTime assignedAt;

    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private String statusNote;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }

    public enum AssignmentStatus {
        ASSIGNED,
        ACCEPTED,
        EN_ROUTE,
        ON_SITE,
        COMPLETED,
        CANCELLED
    }
}
