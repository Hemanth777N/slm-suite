package com.hemanth.slmsuite.service;

import com.hemanth.slmsuite.entity.Part;
import com.hemanth.slmsuite.entity.PricingRule;
import com.hemanth.slmsuite.repository.PartRepository;
import com.hemanth.slmsuite.repository.PricingRuleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    private final PricingRuleRepository pricingRuleRepository;
    private final PartRepository partRepository;

    public PricingService(PricingRuleRepository pricingRuleRepository, PartRepository partRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
        this.partRepository = partRepository;
    }

    public List<PricingRule> getAllRules() {
        return pricingRuleRepository.findAll();
    }

    public PricingRule saveRule(Long partId, String customerTier, BigDecimal adjustmentPercent) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));
        PricingRule rule = new PricingRule();
        rule.setPart(part);
        rule.setCustomerTier(customerTier);
        rule.setAdjustmentPercent(adjustmentPercent);
        return pricingRuleRepository.save(rule);
    }

    public BigDecimal calculatePrice(Long partId, String customerTier) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        return pricingRuleRepository.findByPartAndCustomerTier(part, customerTier)
                .map(rule -> {
                    BigDecimal adjustment = part.getBasePrice()
                            .multiply(rule.getAdjustmentPercent())
                            .divide(BigDecimal.valueOf(100));
                    return part.getBasePrice().add(adjustment);
                })
                .orElse(part.getBasePrice());
    }
}