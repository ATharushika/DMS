package com.sdewec.repository;

import com.sdewec.entity.HazardReport;
import com.sdewec.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HazardReportRepository extends JpaRepository<HazardReport, Long> {
    List<HazardReport> findByReporter(User reporter);
    List<HazardReport> findByStatus(HazardReport.ReportStatus status);
    List<HazardReport> findByReporterOrderByReportedAtDesc(User reporter);

    // For duplicate detection: find nearby reports of same type within 24h
    @Query("SELECT h FROM HazardReport h WHERE h.hazardType = :type " +
           "AND h.reportedAt >= :since " +
           "AND h.latitude BETWEEN :minLat AND :maxLat " +
           "AND h.longitude BETWEEN :minLon AND :maxLon " +
           "AND h.id != :excludeId")
    List<HazardReport> findNearbyRecentReports(
        @Param("type") HazardReport.HazardType type,
        @Param("since") LocalDateTime since,
        @Param("minLat") double minLat,
        @Param("maxLat") double maxLat,
        @Param("minLon") double minLon,
        @Param("maxLon") double maxLon,
        @Param("excludeId") Long excludeId
    );

    List<HazardReport> findByReporterAndStatus(User reporter, HazardReport.ReportStatus status);
    long countByReporter(User reporter);
    long countByReporterAndStatus(User reporter, HazardReport.ReportStatus status);
}
