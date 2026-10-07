package com.sdewec.repository;

import com.sdewec.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(Incident.IncidentStatus status);
    List<Incident> findByStatusNotOrderByPriorityAscCreatedAtDesc(Incident.IncidentStatus status);
    List<Incident> findAllByOrderByPriorityAscCreatedAtDesc();
    long countByStatus(Incident.IncidentStatus status);
}
