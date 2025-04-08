package com.periodic.backend.domain.request.element;

import java.util.List;

import com.periodic.backend.util.constant.StandardState;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ElementRequest {
	@NotBlank(message = "Element symbol is required")
    private String symbol;
	private String image;
	@NotBlank(message = "Element name is required")
	private String name;
	private int atomicNumber;
	@Min(value = 1, message = "Group must be at least 1")
    @Max(value = 18, message = "Group cannot exceed 18")
	private	String groupNumber;
	@Min(value = 1, message = "Period must be at least 1")
    @Max(value = 7, message = "Period cannot exceed 7")
	private String period;
	@NotBlank(message = "Block is required")
    @Pattern(regexp = "^[spdf]$", message = "Block must be one of: s, p, d, f")
	private String block;
	@NotBlank(message = "Classification is required")
	private String classification;
	private double meltingPoint;
	private double boilingPoint;
	private String atomicMass;
	@PositiveOrZero(message = "Density cannot be negative")
	private double density;
	@NotNull(message = "Standard state is required")
	private StandardState standardState;
	private String electronicConfiguration;
	private double electronegativity;
	private double atomicRadius;
	private String ionRadius;
	private double vanDelWaalsRadius;
	private double ionizationEnergy;
	private double electronAffinity;
	private String bondingType;
	private int yearDiscovered;
	private List<Integer> oxidationStates;
}
