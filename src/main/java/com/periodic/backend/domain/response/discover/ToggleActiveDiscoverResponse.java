package com.periodic.backend.domain.response.discover;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToggleActiveDiscoverResponse {
    private Long id;
    private boolean active;
} 