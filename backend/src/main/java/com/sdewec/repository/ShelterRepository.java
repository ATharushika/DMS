package com.sdewec.repository;

import com.sdewec.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    List<Shelter> findByStatus(Shelter.ShelterStatus status);
    Optional<Shelter> findByCoordinatorId(Long coordinatorId);
    long countByStatus(Shelter.ShelterStatus status);
}
