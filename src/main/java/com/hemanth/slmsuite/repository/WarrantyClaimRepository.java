package com.hemanth.slmsuite.repository;

import com.hemanth.slmsuite.entity.User;
import com.hemanth.slmsuite.entity.WarrantyClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarrantyClaimRepository extends JpaRepository<WarrantyClaim, Long> {
    List<WarrantyClaim> findByCustomer(User customer);
}