package com.hemanth.slmsuite.repository;

import com.hemanth.slmsuite.entity.Part;
import com.hemanth.slmsuite.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByPart(Part part);
}