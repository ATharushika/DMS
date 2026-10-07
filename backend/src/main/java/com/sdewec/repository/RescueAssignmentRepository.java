package com.sdewec.repository;

import com.sdewec.entity.RescueAssignment;
import com.sdewec.entity.RescueTeam;
import com.sdewec.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueAssignmentRepository extends JpaRepository<RescueAssignment, Long> {
    List<RescueAssignment> findByRescueTeam(RescueTeam team);
    List<RescueAssignment> findByIncident(Incident incident);
    Optional<RescueAssignment> findByRescueTeamAndStatusNot(RescueTeam team, RescueAssignment.AssignmentStatus status);
    List<RescueAssignment> findByStatus(RescueAssignment.AssignmentStatus status);
    long countByStatus(RescueAssignment.AssignmentStatus status);
}
