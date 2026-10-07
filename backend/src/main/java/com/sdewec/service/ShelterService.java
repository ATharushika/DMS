package com.sdewec.service;

import com.sdewec.entity.*;
import com.sdewec.exception.BadRequestException;
import com.sdewec.exception.ResourceNotFoundException;
import com.sdewec.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterService {

    private final ShelterRepository shelterRepository;
    private final ShelterOccupancyRepository occupancyRepository;
    private final AuditLogService auditLogService;

    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    public Shelter getShelterById(Long id) {
        return shelterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter not found: " + id));
    }

    public Shelter getShelterByCoordinator(Long userId) {
        return shelterRepository.findByCoordinatorId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No shelter found for this coordinator."));
    }

    @Transactional
    public Shelter updateOccupancy(Long shelterId, int newOccupancy, String reason, User updatedBy) {
        Shelter shelter = getShelterById(shelterId);
        int previous = shelter.getCurrentOccupancy();

        if (newOccupancy < 0 || newOccupancy > shelter.getMaximumCapacity()) {
            throw new BadRequestException("Occupancy must be between 0 and " + shelter.getMaximumCapacity());
        }

        shelter.setCurrentOccupancy(newOccupancy);

        // Update status based on occupancy
        double pct = (double) newOccupancy / shelter.getMaximumCapacity() * 100;
        if (pct >= 100) shelter.setStatus(Shelter.ShelterStatus.FULL);
        else if (pct >= 80) shelter.setStatus(Shelter.ShelterStatus.NEAR_CAPACITY);
        else shelter.setStatus(Shelter.ShelterStatus.OPEN);

        shelter = shelterRepository.save(shelter);

        // Record occupancy change history
        ShelterOccupancy history = ShelterOccupancy.builder()
                .shelter(shelter)
                .previousOccupancy(previous)
                .newOccupancy(newOccupancy)
                .updatedBy(updatedBy)
                .reason(reason)
                .build();
        occupancyRepository.save(history);

        auditLogService.log(updatedBy, "UPDATE_SHELTER_OCCUPANCY", "Shelter", shelterId,
                String.valueOf(previous), String.valueOf(newOccupancy));
        return shelter;
    }

    public List<ShelterOccupancy> getOccupancyHistory(Long shelterId) {
        Shelter shelter = getShelterById(shelterId);
        return occupancyRepository.findByShelterOrderByUpdatedAtDesc(shelter);
    }

    public long countNearCapacity() {
        return shelterRepository.countByStatus(Shelter.ShelterStatus.NEAR_CAPACITY)
                + shelterRepository.countByStatus(Shelter.ShelterStatus.FULL);
    }
}
