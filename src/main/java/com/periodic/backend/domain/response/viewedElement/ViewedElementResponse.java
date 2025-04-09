package com.periodic.backend.domain.response.viewedElement;

import java.time.Instant;

import com.periodic.backend.domain.entity.Element;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ViewedElementResponse {
	private Element element;
	private Instant lastSeen;
}
