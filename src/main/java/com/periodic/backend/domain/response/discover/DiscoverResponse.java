package com.periodic.backend.domain.response.discover;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DiscoverResponse {
    private Long id;
    private String scientistName;
    private String elementName;
    private String elementSymbol;
    private int discoveryYear;
    private String discoveryLocation;
    private boolean active;
} 