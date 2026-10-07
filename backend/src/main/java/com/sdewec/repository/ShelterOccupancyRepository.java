package com.sdewec.repository;

import com.sdewec.entity.ShelterOccupancy;
import com.sdewec.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterOccupancyRepository extends JpaRepository<ShelterOccupancy, Long> {
    List<ShelterOccupancy> findByShelterOrderByUpdatedAtDesc(Shelter shelter);
}
