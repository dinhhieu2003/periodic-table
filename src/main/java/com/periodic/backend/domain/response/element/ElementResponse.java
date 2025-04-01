package com.periodic.backend.domain.response.element;

import java.util.List;

import com.periodic.backend.util.constant.StandardState;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ElementResponse {
	private Long id;
	private String symbol;
	private String image;
	private String name;
	private String atomicNumber;
	private	String groupNumber;
	private String period;
	private String block;
	private String classification;
	private double meltingPoint;
	private double boilingPoint;
	private String atomicMass;
	private double density;
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
	private boolean active;
}
