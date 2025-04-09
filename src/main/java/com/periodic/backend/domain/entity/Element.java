package com.periodic.backend.domain.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.periodic.backend.util.constant.StandardState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "elements")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Element extends BaseEntity{
	private String symbol;
	private String image;
	private String name;
	private int atomicNumber;
	private	String groupNumber;
	private String period;
	private String block;
	private String classification;
	private double meltingPoint;
	private double boilingPoint;
	private String atomicMass;
	private double density;
	@Enumerated(EnumType.STRING)
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
	@JdbcTypeCode(SqlTypes.ARRAY)
	@Column(columnDefinition = "integer[]")
	private List<Integer> oxidationStates = new ArrayList<>();
	
	@OneToMany(mappedBy = "element")
	@JsonIgnore
	private List<Discover> discoveries = new ArrayList<>();

    @OneToMany(mappedBy = "element", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FavoriteElement> favorites = new ArrayList<>();
    
    @OneToMany(mappedBy = "element", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ViewedElement> viewedElements = new ArrayList<>();
    
    @OneToMany(mappedBy = "element", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Podcast> podcasts = new ArrayList<>();
    
    @OneToMany(mappedBy = "element", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CommentElement> comments = new ArrayList<>();
    
}