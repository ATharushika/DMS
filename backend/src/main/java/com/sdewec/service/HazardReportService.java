package com.sdewec.service;

import com.sdewec.dto.request.HazardReportRequest;
import com.sdewec.entity.HazardReport;
import com.sdewec.entity.ReportVerification;
import com.sdewec.entity.User;
import com.sdewec.exception.BadRequestException;
import com.sdewec.exception.ResourceNotFoundException;
import com.sdewec.exception.UnauthorizedException;
import com.sdewec.repository.HazardReportRepository;
import com.sdewec.repository.ReportVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HazardReportService {

    private final HazardReportRepository hazardReportRepository;
    private final ReportVerificationRepository verificationRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public HazardReport createReport(HazardReportRequest request, User reporter) {
        int score = calculateCredibilityScore(request, reporter);
        HazardReport report = HazardReport.builder()
                .reporter(reporter)
                .hazardType(request.getHazardType())
                .description(request.getDescription())
                .photoUrl(request.getPhotoUrl())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .locationName(request.getLocationName())
                .status(HazardReport.ReportStatus.SUBMITTED)
                .credibilityScore(score)
                .build();
        report = hazardReportRepository.save(report);
        auditLogService.log(reporter, "CREATE_HAZARD_REPORT", "HazardReport", report.getId(), null, "status=SUBMITTED");
        return report;
    }

    private int calculateCredibilityScore(HazardReportRequest request, User reporter) {
        int score = 0;
        if (request.getLatitude() != null && request.getLongitude() != null) score += 25;
        if (request.getPhotoUrl() != null && !request.getPhotoUrl().isEmpty()) score += 25;
        if (request.getDescription() != null && request.getDescription().length() >= 50) score += 20;
        long verifiedCount = hazardReportRepository.countByReporterAndStatus(reporter, HazardReport.ReportStatus.VERIFIED);
        if (verifiedCount > 0) score += 20;
        if (request.getLocationName() != null && !request.getLocationName().isEmpty()) score += 10;
        return Math.min(score, 100);
    }

    public List<HazardReport> checkDuplicates(HazardReportRequest request) {
        if (request.getLatitude() == null || request.getLongitude() == null) return List.of();
        double delta = 0.01;
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return hazardReportRepository.findNearbyRecentReports(
                request.getHazardType(), since,
                request.getLatitude() - delta, request.getLatitude() + delta,
                request.getLongitude() - delta, request.getLongitude() + delta, -1L);
    }

    public List<HazardReport> getAllReports() { return hazardReportRepository.findAll(); }
    public List<HazardReport> getReportsByStatus(HazardReport.ReportStatus status) { return hazardReportRepository.findByStatus(status); }
    public List<HazardReport> getMyReports(User reporter) { return hazardReportRepository.findByReporterOrderByReportedAtDesc(reporter); }

    public HazardReport getReportById(Long id) {
        return hazardReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hazard report not found with id: " + id));
    }

    @Transactional
    public HazardReport updateReport(Long id, HazardReportRequest request, User reporter) {
        HazardReport report = getReportById(id);
        if (!report.getReporter().getId().equals(reporter.getId())) throw new UnauthorizedException("You can only update your own reports.");
        if (report.getStatus() != HazardReport.ReportStatus.MORE_INFORMATION_REQUIRED) throw new BadRequestException("Report can only be updated when status is MORE_INFORMATION_REQUIRED.");
        String old = report.getStatus().name();
        report.setDescription(request.getDescription());
        report.setPhotoUrl(request.getPhotoUrl());
        report.setLatitude(request.getLatitude());
        report.setLongitude(request.getLongitude());
        report.setLocationName(request.getLocationName());
        report.setStatus(HazardReport.ReportStatus.SUBMITTED);
        report.setCredibilityScore(calculateCredibilityScore(request, reporter));
        report = hazardReportRepository.save(report);
        auditLogService.log(reporter, "UPDATE_HAZARD_REPORT", "HazardReport", report.getId(), old, "status=SUBMITTED");
        return report;
    }

    @Transactional
    public HazardReport verifyReport(Long id, String comments, User officer) {
        HazardReport report = getReportById(id);
        String old = report.getStatus().name();
        report.setStatus(HazardReport.ReportStatus.VERIFIED);
        report = hazardReportRepository.save(report);
        saveVerification(report, officer, ReportVerification.Decision.VERIFIED, comments);
        auditLogService.log(officer, "VERIFY_REPORT", "HazardReport", id, old, "status=VERIFIED");
        return report;
    }

    @Transactional
    public HazardReport rejectReport(Long id, String comments, User officer) {
        HazardReport report = getReportById(id);
        String old = report.getStatus().name();
        report.setStatus(HazardReport.ReportStatus.REJECTED);
        report = hazardReportRepository.save(report);
        saveVerification(report, officer, ReportVerification.Decision.REJECTED, comments);
        auditLogService.log(officer, "REJECT_REPORT", "HazardReport", id, old, "status=REJECTED");
        return report;
    }

    @Transactional
    public HazardReport requestMoreInformation(Long id, String comments, User officer) {
        HazardReport report = getReportById(id);
        String old = report.getStatus().name();
        report.setStatus(HazardReport.ReportStatus.MORE_INFORMATION_REQUIRED);
        report = hazardReportRepository.save(report);
        saveVerification(report, officer, ReportVerification.Decision.REQUEST_MORE_INFORMATION, comments);
        auditLogService.log(officer, "REQUEST_MORE_INFO", "HazardReport", id, old, "status=MORE_INFORMATION_REQUIRED");
        return report;
    }

    @Transactional
    public HazardReport setUnderReview(Long id, User officer) {
        HazardReport report = getReportById(id);
        String old = report.getStatus().name();
        report.setStatus(HazardReport.ReportStatus.UNDER_REVIEW);
        hazardReportRepository.save(report);
        auditLogService.log(officer, "OPEN_REPORT", "HazardReport", id, old, "status=UNDER_REVIEW");
        return report;
    }

    private void saveVerification(HazardReport report, User officer, ReportVerification.Decision decision, String comments) {
        ReportVerification verification = ReportVerification.builder()
                .hazardReport(report).officer(officer).decision(decision).comments(comments).build();
        verificationRepository.save(verification);
    }

    public List<ReportVerification> getVerificationHistory(Long reportId) {
        HazardReport report = getReportById(reportId);
        return verificationRepository.findByHazardReportOrderByVerificationTimeDesc(report);
    }
    @Transactional
    public void deleteReport(Long id) {
        try {
            hazardReportRepository.deleteById(id);
        } catch(Exception e) {
            throw new BadRequestException("Cannot delete report. It is referenced by other records.");
        }
    }
}
