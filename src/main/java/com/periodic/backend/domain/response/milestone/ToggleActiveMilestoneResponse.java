package com.periodic.backend.domain.response.milestone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToggleActiveMilestoneResponse {
    private Long id;
    private boolean active;
} 