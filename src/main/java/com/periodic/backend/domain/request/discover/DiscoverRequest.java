package com.periodic.backend.domain.request.discover;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DiscoverRequest {
    @NotNull(message = "Scientist ID is required")
    private Long scientistId;
    
    @NotNull(message = "Element ID is required")
    private Long elementId;
    
    @NotNull(message = "Discovery year is required")
    @Min(value = 1, message = "Discovery year must be greater than 0")
    private Integer discoveryYear;
    
    private String discoveryLocation;
} 