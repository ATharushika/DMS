package com.sdewec.repository;

import com.sdewec.entity.WarningNotification;
import com.sdewec.entity.Warning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarningNotificationRepository extends JpaRepository<WarningNotification, Long> {
    List<WarningNotification> findByWarning(Warning warning);
    long countByWarningAndStatus(Warning warning, WarningNotification.NotificationStatus status);
}
