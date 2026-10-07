package com.sdewec.repository;

import com.sdewec.entity.RescueTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueTeamRepository extends JpaRepository<RescueTeam, Long> {
    List<RescueTeam> findByStatus(RescueTeam.TeamStatus status);
    Optional<RescueTeam> findByUserId(Long userId);
    long countByStatus(RescueTeam.TeamStatus status);
}
