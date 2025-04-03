package com.periodic.backend.domain.request.milestone;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MilestoneRequest {
    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be greater than 0")
    private Integer year;
    
    @NotBlank(message = "Milestone description is required")
    private String milestone;
    
    private String details;
    
    @NotNull(message = "Scientist ID is required")
    private Long scientistId;
} 