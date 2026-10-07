package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "shelters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private Integer maximumCapacity;

    @Builder.Default
    private Integer currentOccupancy = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ShelterStatus status = ShelterStatus.OPEN;

    // Link to coordinator
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinator_id")
    private User coordinator;

    public enum ShelterStatus {
        OPEN,
        NEAR_CAPACITY,
        FULL,
        CLOSED
    }
}
