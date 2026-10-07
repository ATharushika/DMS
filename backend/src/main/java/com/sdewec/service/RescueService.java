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
public class RescueService {

    private final RescueTeamRepository rescueTeamRepository;
    private final RescueAssignmentRepository assignmentRepository;
    private final IncidentRepository incidentRepository;
    private final AuditLogService auditLogService;

    public List<RescueTeam> getAllTeams() {
        return rescueTeamRepository.findAll();
    }

    public List<RescueTeam> getAvailableTeams() {
        return rescueTeamRepository.findByStatus(RescueTeam.TeamStatus.AVAILABLE);
    }

    /**
     * Recommendation algorithm: scores teams based on:
     * - Availability (must be AVAILABLE): if not available, score = 0
     * - Distance from incident (closer = higher score, up to 40 pts)
     * - Specialization match (20 pts)
     * - Current workload (fewer assignments = higher score, up to 20 pts)
     * - Member count (more = higher score, up to 20 pts)
     */
    public List<RescueTeam> getRecommendedTeams(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + incidentId));

        List<RescueTeam> availableTeams = rescueTeamRepository.findByStatus(RescueTeam.TeamStatus.AVAILABLE);

        return availableTeams.stream()
                .sorted(Comparator.comparingDouble(team -> -scoreTeam(team, incident)))
                .collect(Collectors.toList());
    }

    private double scoreTeam(RescueTeam team, Incident incident) {
        double score = 0;

        // Distance score (up to 40 pts). If no GPS, give medium score.
        if (team.getLatitude() != null && incident.getLatitude() != null) {
            double distKm = haversineKm(team.getLatitude(), team.getLongitude(),
                    incident.getLatitude(), incident.getLongitude());
            // Max 40 pts at 0km, 0 pts at 20km+
            score += Math.max(0, 40 - (distKm * 2));
        } else {
            score += 20; // unknown distance = medium
        }

        // Specialization: simple keyword match
        if (incident.getRelatedWarning() != null) {
            String incidentType = incident.getRelatedWarning().getHazardType().name().toLowerCase();
            String spec = team.getSpecialization().toLowerCase();
            if (spec.contains(incidentType) || incidentType.contains(spec.split("_")[0])) {
                score += 20;
            }
        } else {
            score += 10; // neutral
        }

        // Workload: fewer assignments is better
        long activeAssignments = assignmentRepository.findByRescueTeam(team).stream()
                .filter(a -> a.getStatus() != RescueAssignment.AssignmentStatus.COMPLETED
                        && a.getStatus() != RescueAssignment.AssignmentStatus.CANCELLED)
                .count();
        score += Math.max(0, 20 - (activeAssignments * 5));

        // Member count (up to 20 pts)
        if (team.getMemberCount() != null) {
            score += Math.min(20, team.getMemberCount() * 2);
        }

        return score;
    }

    /**
     * Haversine formula for distance between two GPS coordinates.
     */
    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Transactional
    public RescueAssignment assignTeam(Long incidentId, Long teamId, User officer) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + incidentId));
        RescueTeam team = rescueTeamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Rescue team not found: " + teamId));

        if (team.getStatus() != RescueTeam.TeamStatus.AVAILABLE) {
            throw new BadRequestException("Team is not available for assignment.");
        }

        RescueAssignment assignment = RescueAssignment.builder()
                .incident(incident)
                .rescueTeam(team)
                .status(RescueAssignment.AssignmentStatus.ASSIGNED)
                .build();
        assignment = assignmentRepository.save(assignment);

        // Update incident status to IN_PROGRESS
        incident.setStatus(Incident.IncidentStatus.IN_PROGRESS);
        incidentRepository.save(incident);

        // Update team status
        team.setStatus(RescueTeam.TeamStatus.DISPATCHED);
        rescueTeamRepository.save(team);

        auditLogService.log(officer, "ASSIGN_RESCUE_TEAM", "RescueAssignment", assignment.getId(),
                null, "team=" + team.getTeamName() + ",incident=" + incidentId);
        return assignment;
    }

    @Transactional
    public RescueAssignment acceptAssignment(Long assignmentId, User teamUser) {
        RescueAssignment assignment = getAssignmentById(assignmentId);
        assignment.setStatus(RescueAssignment.AssignmentStatus.ACCEPTED);
        assignment.setAcceptedAt(LocalDateTime.now());
        assignment = assignmentRepository.save(assignment);
        auditLogService.log(teamUser, "ACCEPT_ASSIGNMENT", "RescueAssignment", assignmentId,
                "ASSIGNED", "ACCEPTED");
        return assignment;
    }

    @Transactional
    public RescueAssignment updateAssignmentStatus(Long assignmentId,
                                                     RescueAssignment.AssignmentStatus newStatus,
                                                     User teamUser) {
        RescueAssignment assignment = getAssignmentById(assignmentId);
        String old = assignment.getStatus().name();
        assignment.setStatus(newStatus);

        if (newStatus == RescueAssignment.AssignmentStatus.EN_ROUTE) {
            assignment.setStartedAt(LocalDateTime.now());
        } else if (newStatus == RescueAssignment.AssignmentStatus.COMPLETED) {
            assignment.setCompletedAt(LocalDateTime.now());
            // Free the team
            RescueTeam team = assignment.getRescueTeam();
            team.setStatus(RescueTeam.TeamStatus.AVAILABLE);
            rescueTeamRepository.save(team);
        } else if (newStatus == RescueAssignment.AssignmentStatus.ON_SITE) {
            RescueTeam team = assignment.getRescueTeam();
            team.setStatus(RescueTeam.TeamStatus.ON_SITE);
            rescueTeamRepository.save(team);
        }

        assignment = assignmentRepository.save(assignment);
        auditLogService.log(teamUser, "UPDATE_ASSIGNMENT_STATUS", "RescueAssignment",
                assignmentId, old, newStatus.name());
        return assignment;
    }

    @Transactional
    public RescueAssignment updateLocation(Long assignmentId, Double lat, Double lon, User teamUser) {
        RescueAssignment assignment = getAssignmentById(assignmentId);
        assignment.setCurrentLatitude(lat);
        assignment.setCurrentLongitude(lon);
        assignment = assignmentRepository.save(assignment);
        return assignment;
    }

    public RescueAssignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + id));
    }

    public List<RescueAssignment> getAssignmentsForTeam(Long teamId) {
        RescueTeam team = rescueTeamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
        return assignmentRepository.findByRescueTeam(team);
    }

    public List<RescueAssignment> getAssignmentsForIncident(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + incidentId));
        return assignmentRepository.findByIncident(incident);
    }
}
