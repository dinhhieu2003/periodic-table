package com.periodic.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.periodic.backend.domain.entity.Scientist;

public interface ScientistRepository extends JpaRepository<Scientist, Long>{

}
