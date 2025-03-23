package com.periodic.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "milestones")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Milestone extends BaseEntity{
	private int year;
	private String milestone;
	@Column(columnDefinition = "TEXT")
	private String details;
	@ManyToOne
	@JoinColumn(name = "scientist_id")
	private Scientist scientist;
}