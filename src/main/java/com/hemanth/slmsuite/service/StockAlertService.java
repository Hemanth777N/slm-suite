package com.hemanth.slmsuite.service;

import com.hemanth.slmsuite.entity.Part;
import com.hemanth.slmsuite.repository.PartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockAlertService {

    private static final Logger logger = LoggerFactory.getLogger(StockAlertService.class);

    private final PartRepository partRepository;

    public StockAlertService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void checkLowStock() {
        List<Part> allParts = partRepository.findAll();
        List<Part> lowStockParts = allParts.stream()
                .filter(p -> p.getStockQuantity() != null
                        && p.getReorderThreshold() != null
                        && p.getStockQuantity() < p.getReorderThreshold())
                .toList();

        if (lowStockParts.isEmpty()) {
            logger.info("Stock check: all parts above reorder threshold.");
        } else {
            logger.warn("LOW STOCK ALERT: {} part(s) below reorder threshold:", lowStockParts.size());
            for (Part p : lowStockParts) {
                logger.warn(" - {} (SKU: {}) — stock: {}, threshold: {}",
                        p.getName(), p.getSku(), p.getStockQuantity(), p.getReorderThreshold());
            }
        }
    }
}