package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hazard_report_id", nullable = false)
    private HazardReport hazardReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision decision;

    @Column(columnDefinition = "TEXT")
    private String comments;

    private LocalDateTime verificationTime;

    @PrePersist
    protected void onCreate() {
        verificationTime = LocalDateTime.now();
    }

    public enum Decision {
        VERIFIED,
        REJECTED,
        REQUEST_MORE_INFORMATION
    }
}
