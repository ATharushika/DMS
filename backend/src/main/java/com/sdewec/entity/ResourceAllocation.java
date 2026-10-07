package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "resource_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id", nullable = false)
    private Shelter shelter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ResourceProvider provider;

    @Column(nullable = false)
    private Integer requestedQuantity;

    private Integer allocatedQuantity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AllocationStatus status = AllocationStatus.REQUESTED;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum AllocationStatus {
        REQUESTED,
        ALLOCATED,
        DISPATCHED,
        IN_TRANSIT,
        DELIVERED,
        RECEIVED,
        VERIFIED,
        CANCELLED
    }
}
