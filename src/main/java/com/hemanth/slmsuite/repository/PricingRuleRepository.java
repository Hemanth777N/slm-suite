package com.hemanth.slmsuite.repository;

import com.hemanth.slmsuite.entity.Part;
import com.hemanth.slmsuite.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    List<PricingRule> findByPart(Part part);
    Optional<PricingRule> findByPartAndCustomerTier(Part part, String customerTier);
}