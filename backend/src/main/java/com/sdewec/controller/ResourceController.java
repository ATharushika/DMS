package com.sdewec.controller;

import com.sdewec.entity.*;
import com.sdewec.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/api/resources")
    public ResponseEntity<List<Resource>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/api/providers")
    public ResponseEntity<List<ResourceProvider>> getAllProviders() {
        return ResponseEntity.ok(resourceService.getAllProviders());
    }

    @GetMapping("/api/providers/recommended")
    public ResponseEntity<List<ResourceProvider>> getRecommended(
            @RequestParam Long shelterId) {
        return ResponseEntity.ok(resourceService.getRecommendedProviders(shelterId));
    }

    @PostMapping("/api/resource-allocations")
    public ResponseEntity<ResourceAllocation> createAllocation(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User user) {
        Long shelterId = Long.parseLong(body.get("shelterId").toString());
        Long resourceId = Long.parseLong(body.get("resourceId").toString());
        Long providerId = Long.parseLong(body.get("providerId").toString());
        int quantity = Integer.parseInt(body.get("quantity").toString());
        String notes = (String) body.getOrDefault("notes", "");
        return ResponseEntity.ok(resourceService.createAllocation(
                shelterId, resourceId, providerId, quantity, notes, user));
    }

    @PutMapping("/api/resource-allocations/{id}/status")
    public ResponseEntity<ResourceAllocation> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User user) {
        ResourceAllocation.AllocationStatus status =
                ResourceAllocation.AllocationStatus.valueOf(body.get("status").toString());
        Integer qty = body.containsKey("quantity") ? Integer.parseInt(body.get("quantity").toString()) : null;
        return ResponseEntity.ok(resourceService.updateAllocationStatus(id, status, qty, user));
    }

    @PostMapping("/api/resource-allocations/{id}/confirm-receipt")
    public ResponseEntity<ResourceDelivery> confirmReceipt(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(resourceService.confirmReceipt(
                id, body.get("receivedQuantity"), user));
    }

    @GetMapping("/api/resource-allocations")
    public ResponseEntity<List<ResourceAllocation>> getAllocations(
            @RequestParam(required = false) Long shelterId,
            @RequestParam(required = false) Long providerId) {
        if (shelterId != null) return ResponseEntity.ok(resourceService.getAllocationsForShelter(shelterId));
        if (providerId != null) return ResponseEntity.ok(resourceService.getAllocationsForProvider(providerId));
        return ResponseEntity.ok(resourceService.getAllAllocations());
    }
}
