package com.sdewec.dto.request;

import com.sdewec.entity.Incident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidentRequest {
    @NotBlank(message = "Description is required")
    private String description;

    private Double latitude;
    private Double longitude;

    @NotBlank(message = "Location name is required")
    private String locationName;

    @NotNull(message = "Priority is required")
    private Incident.Priority priority;

    private Long relatedWarningId;
}
