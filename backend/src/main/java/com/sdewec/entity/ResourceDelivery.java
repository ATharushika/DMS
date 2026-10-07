package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "resource_deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_id", nullable = false)
    private ResourceAllocation allocation;

    @Column(nullable = false)
    private Integer dispatchedQuantity;

    private Integer receivedQuantity;

    private LocalDateTime dispatchedAt;
    private LocalDateTime receivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id")
    private User verifiedBy;

    private String deliveryNotes;
}
