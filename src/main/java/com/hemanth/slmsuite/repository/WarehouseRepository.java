package com.hemanth.slmsuite.repository;

import com.hemanth.slmsuite.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}