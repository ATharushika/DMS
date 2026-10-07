package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "shelter_occupancy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShelterOccupancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id", nullable = false)
    private Shelter shelter;

    @Column(nullable = false)
    private Integer previousOccupancy;

    @Column(nullable = false)
    private Integer newOccupancy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    private LocalDateTime updatedAt;
    private String reason;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }
}
