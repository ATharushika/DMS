package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String resourceType; // e.g. "WATER", "FOOD", "MEDICINE", "BLANKETS"

    @Column(nullable = false)
    private Integer quantity;

    private String unit; // e.g. "Litres", "Kg", "Units"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    // For medicine/food expiry tracking
    private LocalDate expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ResourceProvider provider;

    public enum Priority {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW
    }
}
