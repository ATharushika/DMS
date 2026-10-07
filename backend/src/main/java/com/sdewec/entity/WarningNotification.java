package com.sdewec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "warning_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warning_id", nullable = false)
    private Warning warning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    // Recipient info (cached for reports even if user deleted)
    private String recipientName;
    private String recipientContact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }

    public enum NotificationChannel {
        PUSH_NOTIFICATION,
        SMS,
        AUDIBLE_ALERT
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        DELIVERED,
        FAILED
    }
}
