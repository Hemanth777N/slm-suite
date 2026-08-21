package com.hemanth.slmsuite.repository;

import com.hemanth.slmsuite.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {
}