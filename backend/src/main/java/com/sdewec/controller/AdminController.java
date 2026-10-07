package com.sdewec.controller;

import com.sdewec.entity.*;
import com.sdewec.exception.ResourceNotFoundException;
import com.sdewec.repository.*;
import com.sdewec.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final ShelterRepository shelterRepository;
    private final ResourceProviderRepository providerRepository;
    private final AuditLogService auditLogService;
    private final WarningRepository warningRepository;
    private final HazardReportRepository hazardReportRepository;
    private final IncidentRepository incidentRepository;

    // User management
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        if (body.containsKey("active")) {
            user.setActive((Boolean) body.get("active"));
        }
        if (body.containsKey("name")) {
            user.setName((String) body.get("name"));
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    // Audit logs
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    // Dashboard stats
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = Map.of(
            "activeWarnings", warningRepository.countByStatus(Warning.WarningStatus.ACTIVE),
            "criticalWarnings", warningRepository.findByStatus(Warning.WarningStatus.ACTIVE)
                    .stream().filter(w -> w.getWarningLevel() == Warning.WarningLevel.CRITICAL).count(),
            "pendingReports", hazardReportRepository.findByStatus(HazardReport.ReportStatus.SUBMITTED).size(),
            "openIncidents", incidentRepository.countByStatus(Incident.IncidentStatus.OPEN),
            "availableTeams", rescueTeamRepository.countByStatus(RescueTeam.TeamStatus.AVAILABLE),
            "totalUsers", userRepository.count()
        );
        return ResponseEntity.ok(stats);
    }
}
