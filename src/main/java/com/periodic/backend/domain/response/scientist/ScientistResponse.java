package com.periodic.backend.domain.response.scientist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ScientistResponse {
	private long id;
	private String name;
	private Integer birthYear;
	private Integer deathYear;
	private String nationality;
	private String contribution;
	private String fact;
}
