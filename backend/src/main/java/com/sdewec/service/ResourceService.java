package com.sdewec.service;

import com.sdewec.entity.*;
import com.sdewec.exception.BadRequestException;
import com.sdewec.exception.ResourceNotFoundException;
import com.sdewec.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceProviderRepository providerRepository;
    private final ResourceAllocationRepository allocationRepository;
    private final ResourceDeliveryRepository deliveryRepository;
    private final ShelterRepository shelterRepository;
    private final AuditLogService auditLogService;

    public List<com.sdewec.entity.Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public List<ResourceProvider> getAllProviders() {
        return providerRepository.findAll();
    }

    /**
     * Recommend providers for a shelter based on:
     * - Distance to shelter (closer = higher score, up to 40 pts)
     * - Available resource quantity (up to 40 pts)
     * - (Mock) delivery time (random 1-24h, closer distance = less time)
     */
    public List<ResourceProvider> getRecommendedProviders(Long shelterId) {
        Shelter shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter not found: " + shelterId));

        List<ResourceProvider> providers = providerRepository.findAll();
        return providers.stream()
                .sorted(Comparator.comparingDouble(p -> -scoreProvider(p, shelter)))
                .collect(Collectors.toList());
    }

    private double scoreProvider(ResourceProvider provider, Shelter shelter) {
        double score = 0;
        // Distance score
        if (provider.getLatitude() != null && shelter.getLatitude() != null) {
            double dist = haversineKm(provider.getLatitude(), provider.getLongitude(),
                    shelter.getLatitude(), shelter.getLongitude());
            score += Math.max(0, 40 - (dist * 2));
        } else {
            score += 20;
        }
        // Resource availability: count total resources from provider
        List<com.sdewec.entity.Resource> resources = resourceRepository.findByProvider(provider);
        int totalQty = resources.stream().mapToInt(com.sdewec.entity.Resource::getQuantity).sum();
        score += Math.min(40, totalQty / 10.0);
        return score;
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    @Transactional
    public ResourceAllocation createAllocation(Long shelterId, Long resourceId,
                                               Long providerId, int quantity,
                                               String notes, User requestedBy) {
        Shelter shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter not found: " + shelterId));
        com.sdewec.entity.Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + resourceId));
        ResourceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found: " + providerId));

        ResourceAllocation allocation = ResourceAllocation.builder()
                .shelter(shelter)
                .resource(resource)
                .provider(provider)
                .requestedQuantity(quantity)
                .status(ResourceAllocation.AllocationStatus.REQUESTED)
                .notes(notes)
                .build();

        allocation = allocationRepository.save(allocation);
        auditLogService.log(requestedBy, "CREATE_RESOURCE_ALLOCATION", "ResourceAllocation",
                allocation.getId(), null,
                "shelter=" + shelterId + ",resource=" + resourceId + ",qty=" + quantity);
        return allocation;
    }

    @Transactional
    public ResourceAllocation updateAllocationStatus(Long allocationId,
                                                      ResourceAllocation.AllocationStatus newStatus,
                                                      Integer quantity,
                                                      User updatedBy) {
        ResourceAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found: " + allocationId));

        String old = allocation.getStatus().name();
        allocation.setStatus(newStatus);

        if (quantity != null) allocation.setAllocatedQuantity(quantity);

        // When dispatched, create a delivery record
        if (newStatus == ResourceAllocation.AllocationStatus.DISPATCHED) {
            ResourceDelivery delivery = ResourceDelivery.builder()
                    .allocation(allocation)
                    .dispatchedQuantity(quantity != null ? quantity : allocation.getRequestedQuantity())
                    .dispatchedAt(LocalDateTime.now())
                    .build();
            deliveryRepository.save(delivery);
        }

        // When received, update shelter resource inventory
        allocation = allocationRepository.save(allocation);
        auditLogService.log(updatedBy, "UPDATE_ALLOCATION_STATUS", "ResourceAllocation",
                allocationId, old, newStatus.name());
        return allocation;
    }

    @Transactional
    public ResourceDelivery confirmReceipt(Long allocationId, int receivedQty, User coordinator) {
        ResourceAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found: " + allocationId));

        allocation.setStatus(ResourceAllocation.AllocationStatus.RECEIVED);
        allocationRepository.save(allocation);

        List<ResourceDelivery> deliveries = deliveryRepository.findByAllocation(allocation);
        ResourceDelivery delivery = deliveries.isEmpty()
                ? ResourceDelivery.builder().allocation(allocation)
                    .dispatchedQuantity(receivedQty).dispatchedAt(LocalDateTime.now()).build()
                : deliveries.get(0);

        delivery.setReceivedQuantity(receivedQty);
        delivery.setReceivedAt(LocalDateTime.now());
        delivery.setVerifiedBy(coordinator);
        delivery = deliveryRepository.save(delivery);

        auditLogService.log(coordinator, "CONFIRM_RESOURCE_RECEIPT", "ResourceDelivery",
                delivery.getId(), null, "receivedQty=" + receivedQty);
        return delivery;
    }

    public List<ResourceAllocation> getAllocationsForShelter(Long shelterId) {
        Shelter shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter not found: " + shelterId));
        return allocationRepository.findByShelter(shelter);
    }

    public List<ResourceAllocation> getAllocationsForProvider(Long providerId) {
        ResourceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found: " + providerId));
        return allocationRepository.findByProvider(provider);
    }

    public List<ResourceAllocation> getAllAllocations() {
        return allocationRepository.findAll();
    }
}
