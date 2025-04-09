package com.periodic.backend.domain.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="scientists")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scientist extends BaseEntity {
	private String name;
	private Integer birthYear;
	private Integer deathYear;
	private String nationality;
	@Column(columnDefinition = "TEXT")
	private String contribution;
	@Column(columnDefinition = "TEXT")
	private String fact;
	@OneToMany(mappedBy = "scientist")
	private List<Discover> discoveries = new ArrayList<>();
	@OneToMany(mappedBy = "scientist")
	private List<Milestone> milestones = new ArrayList<>();
}
