package com.sdewec.controller;

import com.sdewec.dto.request.WarningRequest;
import com.sdewec.entity.Warning;
import com.sdewec.entity.User;
import com.sdewec.service.WarningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warnings")
@RequiredArgsConstructor
public class WarningController {

    private final WarningService warningService;

    @PostMapping
    public ResponseEntity<Warning> createWarning(@Valid @RequestBody WarningRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(warningService.createWarning(request, user));
    }

    @GetMapping
    public ResponseEntity<List<Warning>> getAllWarnings() { return ResponseEntity.ok(warningService.getAllWarnings()); }

    @GetMapping("/active")
    public ResponseEntity<List<Warning>> getActiveWarnings() { return ResponseEntity.ok(warningService.getActiveWarnings()); }

    @GetMapping("/{id}")
    public ResponseEntity<Warning> getWarning(@PathVariable Long id) { return ResponseEntity.ok(warningService.getWarningById(id)); }

    @PostMapping("/{id}/submit-approval")
    public ResponseEntity<Warning> submitForApproval(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(warningService.submitForApproval(id, user));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Warning> approveWarning(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(warningService.approveWarning(id, user));
    }

    @PostMapping("/{id}/broadcast")
    public ResponseEntity<Warning> broadcastWarning(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(warningService.broadcastWarning(id, user));
    }

    @GetMapping("/{id}/notifications")
    public ResponseEntity<?> getNotifications(@PathVariable Long id) { return ResponseEntity.ok(warningService.getNotificationsForWarning(id)); }

    @GetMapping("/{id}/notification-stats")
    public ResponseEntity<Map<String, Long>> getNotificationStats(@PathVariable Long id) {
        return ResponseEntity.ok(warningService.getNotificationStats(id));
    }
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Warning> deactivateWarning(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(warningService.deactivateWarning(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarning(@PathVariable Long id) {
        warningService.deleteWarning(id);
        return ResponseEntity.ok().build();
    }
}
