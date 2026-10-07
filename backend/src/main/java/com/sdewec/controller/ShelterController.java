package com.sdewec.controller;

import com.sdewec.entity.*;
import com.sdewec.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shelters")
@RequiredArgsConstructor
public class ShelterController {

    private final ShelterService shelterService;

    @GetMapping
    public ResponseEntity<List<Shelter>> getAllShelters() {
        return ResponseEntity.ok(shelterService.getAllShelters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shelter> getShelter(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.getShelterById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<Shelter> getMyShelter(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(shelterService.getShelterByCoordinator(user.getId()));
    }

    @PutMapping("/{id}/occupancy")
    public ResponseEntity<Shelter> updateOccupancy(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User user) {
        int newOccupancy = Integer.parseInt(body.get("occupancy").toString());
        String reason = (String) body.getOrDefault("reason", "");
        return ResponseEntity.ok(shelterService.updateOccupancy(id, newOccupancy, reason, user));
    }

    @GetMapping("/{id}/occupancy-history")
    public ResponseEntity<List<ShelterOccupancy>> getOccupancyHistory(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.getOccupancyHistory(id));
    }
}
