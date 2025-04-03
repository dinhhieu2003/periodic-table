package com.periodic.backend.domain.response.milestone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MilestoneResponse {
    private Long id;
    private int year;
    private String milestone;
    private String details;
    private String scientistName;
    private boolean active;
} 