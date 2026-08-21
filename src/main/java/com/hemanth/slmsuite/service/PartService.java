package com.hemanth.slmsuite.service;

import com.hemanth.slmsuite.entity.Part;
import com.hemanth.slmsuite.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public Part getPartById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found with id: " + id));
    }

    public Part saveOrUpdatePart(Part part) {
        return partRepository.save(part);
    }

    public void deletePart(Long id) {
        partRepository.deleteById(id);
    }

    public List<Part> getLowStockParts() {
        return partRepository.findAll().stream()
                .filter(p -> p.getStockQuantity() != null
                        && p.getReorderThreshold() != null
                        && p.getStockQuantity() < p.getReorderThreshold())
                .toList();
    }
}