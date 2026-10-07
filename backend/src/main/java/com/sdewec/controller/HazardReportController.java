package com.sdewec.controller;

import com.sdewec.dto.request.HazardReportRequest;
import com.sdewec.entity.HazardReport;
import com.sdewec.entity.User;
import com.sdewec.service.HazardReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hazards")
@RequiredArgsConstructor
public class HazardReportController {

    private final HazardReportService hazardReportService;

    @PostMapping
    public ResponseEntity<HazardReport> createReport(@Valid @RequestBody HazardReportRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hazardReportService.createReport(request, user));
    }

    @GetMapping
    public ResponseEntity<List<HazardReport>> getAllReports(@RequestParam(required = false) String status) {
        if (status != null) return ResponseEntity.ok(hazardReportService.getReportsByStatus(HazardReport.ReportStatus.valueOf(status)));
        return ResponseEntity.ok(hazardReportService.getAllReports());
    }

    @GetMapping("/my")
    public ResponseEntity<List<HazardReport>> getMyReports(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hazardReportService.getMyReports(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HazardReport> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(hazardReportService.getReportById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HazardReport> updateReport(@PathVariable Long id, @Valid @RequestBody HazardReportRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hazardReportService.updateReport(id, request, user));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<HazardReport> setUnderReview(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hazardReportService.setUnderReview(id, user));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<HazardReport> verifyReport(@PathVariable Long id, @RequestBody Map<String, String> body, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hazardReportService.verifyReport(id, body.get("comments"), user));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<HazardReport> rejectReport(@PathVariable Long id, @RequestBody Map<String, String> body, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hazardReportService.rejectReport(id, body.get("comments"), user));
    }

    @PostMapping("/{id}/request-information")
    public ResponseEntity<HazardReport> requestMoreInfo(@PathVariable Long id, @RequestBody Map<String, String> body, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hazardReportService.requestMoreInformation(id, body.get("comments"), user));
    }

    @PostMapping("/check-duplicate")
    public ResponseEntity<List<HazardReport>> checkDuplicate(@RequestBody HazardReportRequest request) {
        return ResponseEntity.ok(hazardReportService.checkDuplicates(request));
    }

    @GetMapping("/{id}/verifications")
    public ResponseEntity<?> getVerificationHistory(@PathVariable Long id) {
        return ResponseEntity.ok(hazardReportService.getVerificationHistory(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        hazardReportService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
}
