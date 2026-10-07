package com.sdewec.controller;

import com.sdewec.entity.*;
import com.sdewec.service.RescueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RescueController {

    private final RescueService rescueService;

    @GetMapping("/api/rescue-teams")
    public ResponseEntity<List<RescueTeam>> getAllTeams(
            @RequestParam(required = false) Boolean available) {
        if (Boolean.TRUE.equals(available)) {
            return ResponseEntity.ok(rescueService.getAvailableTeams());
        }
        return ResponseEntity.ok(rescueService.getAllTeams());
    }

    @GetMapping("/api/rescue-teams/recommended")
    public ResponseEntity<List<RescueTeam>> getRecommended(
            @RequestParam Long incidentId) {
        return ResponseEntity.ok(rescueService.getRecommendedTeams(incidentId));
    }

    @PostMapping("/api/rescue-assignments")
    public ResponseEntity<RescueAssignment> assignTeam(
            @RequestBody Map<String, Long> body,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rescueService.assignTeam(
                body.get("incidentId"), body.get("teamId"), user));
    }

    @PutMapping("/api/rescue-assignments/{id}/accept")
    public ResponseEntity<RescueAssignment> acceptAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rescueService.acceptAssignment(id, user));
    }

    @PutMapping("/api/rescue-assignments/{id}/status")
    public ResponseEntity<RescueAssignment> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user) {
        RescueAssignment.AssignmentStatus status =
                RescueAssignment.AssignmentStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(rescueService.updateAssignmentStatus(id, status, user));
    }

    @PutMapping("/api/rescue-assignments/{id}/location")
    public ResponseEntity<RescueAssignment> updateLocation(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rescueService.updateLocation(
                id, body.get("latitude"), body.get("longitude"), user));
    }

    @GetMapping("/api/rescue-assignments")
    public ResponseEntity<List<RescueAssignment>> getAssignments(
            @RequestParam(required = false) Long incidentId,
            @RequestParam(required = false) Long teamId) {
        if (incidentId != null) return ResponseEntity.ok(rescueService.getAssignmentsForIncident(incidentId));
        if (teamId != null) return ResponseEntity.ok(rescueService.getAssignmentsForTeam(teamId));
        return ResponseEntity.ok(List.of());
    }
}
