package com.sdewec.service;

import com.sdewec.dto.request.WarningRequest;
import com.sdewec.entity.*;
import com.sdewec.exception.BadRequestException;
import com.sdewec.exception.ResourceNotFoundException;
import com.sdewec.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WarningService {

    private final WarningRepository warningRepository;
    private final WarningNotificationRepository notificationRepository;
    private final HazardReportRepository hazardReportRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public Warning createWarning(WarningRequest request, User createdBy) {
        HazardReport sourceReport = null;
        if (request.getSourceReportId() != null) sourceReport = hazardReportRepository.findById(request.getSourceReportId()).orElse(null);
        String channels = request.getNotificationChannels() != null ? String.join(",", request.getNotificationChannels()) : "";
        Warning warning = Warning.builder()
                .hazardType(request.getHazardType()).warningLevel(request.getWarningLevel())
                .message(request.getMessage()).emergencyInstructions(request.getEmergencyInstructions())
                .affectedArea(request.getAffectedArea()).affectedLatitude(request.getAffectedLatitude())
                .affectedLongitude(request.getAffectedLongitude()).affectedRadius(request.getAffectedRadius())
                .createdBy(createdBy).status(Warning.WarningStatus.DRAFT).version(1)
                .notificationChannels(channels).sourceReport(sourceReport).build();
        warning = warningRepository.save(warning);
        auditLogService.log(createdBy, "CREATE_WARNING", "Warning", warning.getId(), null, "level=" + warning.getWarningLevel() + ",status=DRAFT");
        return warning;
    }

    @Transactional
    public Warning submitForApproval(Long id, User officer) {
        Warning warning = getWarningById(id);
        String old = warning.getStatus().name();
        warning.setStatus(Warning.WarningStatus.PENDING_APPROVAL);
        warning.setVersion(warning.getVersion() + 1);
        warning = warningRepository.save(warning);
        auditLogService.log(officer, "SUBMIT_WARNING_FOR_APPROVAL", "Warning", id, old, "status=PENDING_APPROVAL,version=" + warning.getVersion());
        return warning;
    }

    @Transactional
    public Warning approveWarning(Long id, User approver) {
        Warning warning = getWarningById(id);
        if (warning.getStatus() != Warning.WarningStatus.PENDING_APPROVAL && warning.getStatus() != Warning.WarningStatus.DRAFT)
            throw new BadRequestException("Warning is not in a state that can be approved.");
        String old = warning.getStatus().name();
        warning.setApprovedBy(approver);
        warning.setStatus(Warning.WarningStatus.APPROVED);
        warning.setVersion(warning.getVersion() + 1);
        warning = warningRepository.save(warning);
        auditLogService.log(approver, "APPROVE_WARNING", "Warning", id, old, "status=APPROVED,version=" + warning.getVersion());
        return warning;
    }

    @Transactional
    public Warning broadcastWarning(Long id, User officer) {
        Warning warning = getWarningById(id);
        if (warning.getStatus() != Warning.WarningStatus.APPROVED && warning.getStatus() != Warning.WarningStatus.DRAFT) {
            if (warning.getWarningLevel() == Warning.WarningLevel.CRITICAL && warning.getStatus() != Warning.WarningStatus.APPROVED)
                throw new BadRequestException("Critical warnings require approval before broadcasting.");
        }
        String old = warning.getStatus().name();
        warning.setStatus(Warning.WarningStatus.ACTIVE);
        warning.setIssuedAt(LocalDateTime.now());
        warning.setVersion(warning.getVersion() + 1);
        warning = warningRepository.save(warning);
        generateNotifications(warning);
        auditLogService.log(officer, "BROADCAST_WARNING", "Warning", id, old, "status=ACTIVE,issuedAt=" + warning.getIssuedAt());
        return warning;
    }

    private void generateNotifications(Warning warning) {
        List<User> recipients = userRepository.findByRole(User.UserRole.CITIZEN);
        recipients.addAll(userRepository.findByRole(User.UserRole.VOLUNTEER));
        String[] channels = warning.getNotificationChannels() != null ? warning.getNotificationChannels().split(",") : new String[]{"PUSH_NOTIFICATION"};
        Random random = new Random();
        for (User recipient : recipients) {
            for (String ch : channels) {
                if (ch.isEmpty()) continue;
                try {
                    WarningNotification.NotificationChannel channel = WarningNotification.NotificationChannel.valueOf(ch.trim());
                    boolean delivered = random.nextInt(10) != 0;
                    WarningNotification notification = WarningNotification.builder()
                            .warning(warning).recipient(recipient).recipientName(recipient.getName())
                            .recipientContact(recipient.getEmail()).channel(channel)
                            .status(delivered ? WarningNotification.NotificationStatus.DELIVERED : WarningNotification.NotificationStatus.FAILED)
                            .deliveredAt(delivered ? LocalDateTime.now() : null).build();
                    notificationRepository.save(notification);
                } catch (IllegalArgumentException e) { /* Skip invalid channels */ }
            }
        }
    }

    public Warning getWarningById(Long id) {
        return warningRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Warning not found with id: " + id));
    }

    public List<Warning> getAllWarnings() { return warningRepository.findAllByOrderByCreatedAtDesc(); }
    public List<Warning> getActiveWarnings() { return warningRepository.findByStatus(Warning.WarningStatus.ACTIVE); }

    public List<WarningNotification> getNotificationsForWarning(Long warningId) {
        Warning warning = getWarningById(warningId);
        return notificationRepository.findByWarning(warning);
    }

    public Map<String, Long> getNotificationStats(Long warningId) {
        Warning warning = getWarningById(warningId);
        long delivered = notificationRepository.countByWarningAndStatus(warning, WarningNotification.NotificationStatus.DELIVERED);
        long failed = notificationRepository.countByWarningAndStatus(warning, WarningNotification.NotificationStatus.FAILED);
        long pending = notificationRepository.countByWarningAndStatus(warning, WarningNotification.NotificationStatus.PENDING);
        return Map.of("delivered", delivered, "failed", failed, "pending", pending);
    }
    @Transactional
    public Warning deactivateWarning(Long id, User officer) {
        Warning warning = getWarningById(id);
        warning.setStatus(Warning.WarningStatus.CANCELLED);
        return warningRepository.save(warning);
    }

    @Transactional
    public void deleteWarning(Long id) {
        try {
            warningRepository.deleteById(id);
        } catch(Exception e) {
            throw new BadRequestException("Cannot delete warning. It is referenced by other records.");
        }
    }
}
