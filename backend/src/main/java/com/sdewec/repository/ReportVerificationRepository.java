package com.sdewec.repository;

import com.sdewec.entity.ReportVerification;
import com.sdewec.entity.HazardReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportVerificationRepository extends JpaRepository<ReportVerification, Long> {
    List<ReportVerification> findByHazardReportOrderByVerificationTimeDesc(HazardReport hazardReport);
}
