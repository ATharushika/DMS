package com.sdewec.repository;

import com.sdewec.entity.Warning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarningRepository extends JpaRepository<Warning, Long> {
    List<Warning> findByStatus(Warning.WarningStatus status);
    List<Warning> findByStatusOrderByCreatedAtDesc(Warning.WarningStatus status);
    List<Warning> findAllByOrderByCreatedAtDesc();
    List<Warning> findByStatusIn(List<Warning.WarningStatus> statuses);
    long countByStatus(Warning.WarningStatus status);
}
