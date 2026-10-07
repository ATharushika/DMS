package com.sdewec.service;

import com.sdewec.dto.request.IncidentRequest;
import com.sdewec.entity.Incident;
import com.sdewec.entity.User;
import com.sdewec.entity.Warning;
import com.sdewec.exception.ResourceNotFoundException;
import com.sdewec.exception.BadRequestException;
import com.sdewec.repository.IncidentRepository;
import com.sdewec.repository.WarningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final WarningRepository warningRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public Incident createIncident(IncidentRequest request, User createdBy) {
        Warning relatedWarning = null;
        if (request.getRelatedWarningId() != null) relatedWarning = warningRepository.findById(request.getRelatedWarningId()).orElse(null);
        Incident incident = Incident.builder()
                .description(request.getDescription()).latitude(request.getLatitude())
                .longitude(request.getLongitude()).locationName(request.getLocationName())
                .priority(request.getPriority()).status(Incident.IncidentStatus.OPEN)
                .createdBy(createdBy).relatedWarning(relatedWarning).build();
        incident = incidentRepository.save(incident);
        auditLogService.log(createdBy, "CREATE_INCIDENT", "Incident", incident.getId(), null, "priority=" + incident.getPriority() + ",status=OPEN");
        return incident;
    }

    public List<Incident> getAllIncidents() { return incidentRepository.findAllByOrderByPriorityAscCreatedAtDesc(); }

    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + id));
    }

    @Transactional
    public Incident updateStatus(Long id, Incident.IncidentStatus newStatus, User officer) {
        Incident incident = getIncidentById(id);
        String old = incident.getStatus().name();
        incident.setStatus(newStatus);
        if (newStatus == Incident.IncidentStatus.RESOLVED || newStatus == Incident.IncidentStatus.CLOSED)
            incident.setResolvedAt(LocalDateTime.now());
        incident = incidentRepository.save(incident);
        auditLogService.log(officer, "UPDATE_INCIDENT_STATUS", "Incident", id, old, newStatus.name());
        return incident;
    }

    public long countByStatus(Incident.IncidentStatus status) { return incidentRepository.countByStatus(status); }
    @Transactional
    public void deleteIncident(Long id) {
        try {
            incidentRepository.deleteById(id);
        } catch(Exception e) {
            throw new BadRequestException("Cannot delete incident. It is referenced by rescue assignments.");
        }
    }
}
