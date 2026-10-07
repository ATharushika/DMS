package com.sdewec.dto.request;

import com.sdewec.entity.Warning;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class WarningRequest {
    @NotNull(message = "Hazard type is required")
    private com.sdewec.entity.HazardReport.HazardType hazardType;

    @NotNull(message = "Warning level is required")
    private Warning.WarningLevel warningLevel;

    @NotBlank(message = "Message is required")
    private String message;

    private String emergencyInstructions;

    @NotBlank(message = "Affected area is required")
    private String affectedArea;

    private Double affectedLatitude;
    private Double affectedLongitude;
    private Double affectedRadius;

    private List<String> notificationChannels;

    private Long sourceReportId;
}
