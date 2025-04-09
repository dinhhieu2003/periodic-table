package com.periodic.backend.domain.response.viewedPodcast;

import java.time.Instant;

import com.periodic.backend.domain.entity.Podcast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ViewedPodcastResponse {
    private Podcast podcast;
    private Instant lastSeen;
} 