package com.periodic.backend.domain.request.scientist;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ScientistRequest {
	@NotBlank(message = "Name field is required")
	private String name;
	private Integer birthYear;
	private Integer deathYear;
	private String nationality;
	private String contribution;
	private String fact;
	
	@AssertTrue(message = "Birth year must be before death year")
    public boolean isBirthBeforeDeath() {
		if(birthYear == null)
			return true;
        return (deathYear == null || birthYear < deathYear);
    }
}
