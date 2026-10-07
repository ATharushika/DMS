package com.sdewec.dto.request;

import com.sdewec.entity.HazardReport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HazardReportRequest {
    @NotNull(message = "Hazard type is required")
    private HazardReport.HazardType hazardType;

    @NotBlank(message = "Description is required")
    private String description;

    private String photoUrl;
    private Double latitude;
    private Double longitude;

    @NotBlank(message = "Location name is required")
    private String locationName;

    // True if GPS was used (for credibility score)
    private boolean gpsUsed;
}
