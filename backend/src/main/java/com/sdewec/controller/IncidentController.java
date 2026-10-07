package com.sdewec.controller;

import com.sdewec.dto.request.IncidentRequest;
import com.sdewec.entity.Incident;
import com.sdewec.entity.User;
import com.sdewec.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<Incident> createIncident(@Valid @RequestBody IncidentRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(incidentService.createIncident(request, user));
    }

    @GetMapping
    public ResponseEntity<List<Incident>> getAllIncidents() { return ResponseEntity.ok(incidentService.getAllIncidents()); }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncident(@PathVariable Long id) { return ResponseEntity.ok(incidentService.getIncidentById(id)); }

    @PutMapping("/{id}/status")
    public ResponseEntity<Incident> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body, @AuthenticationPrincipal User user) {
        Incident.IncidentStatus status = Incident.IncidentStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(incidentService.updateStatus(id, status, user));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.ok().build();
    }
}
