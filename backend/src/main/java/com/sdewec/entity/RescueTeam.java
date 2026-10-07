package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "rescue_teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescueTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private String specialization; // e.g. "FLOOD_RESCUE", "MOUNTAIN_RESCUE"

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TeamStatus status = TeamStatus.AVAILABLE;

    // Current team location (mock GPS)
    private Double latitude;
    private Double longitude;

    private String contactNumber;

    // Number of members
    private Integer memberCount;

    // Link to user account for this team
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public enum TeamStatus {
        AVAILABLE,
        DISPATCHED,
        ON_SITE,
        UNAVAILABLE
    }
}
