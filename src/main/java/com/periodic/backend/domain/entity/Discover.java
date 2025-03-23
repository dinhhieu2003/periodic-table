package com.periodic.backend.domain.entity;

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
@Table(name="discoveries")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discover extends BaseEntity {
	@ManyToOne
	@JoinColumn(name ="scientist_id")
	private Scientist scientist;
	@ManyToOne
	@JoinColumn(name ="element_id")
	private Element element;
	private int discoveryYear;
	private String discoveryLocation;
}
