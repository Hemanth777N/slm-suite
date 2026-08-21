package com.hemanth.slmsuite.service;
import com.hemanth.slmsuite.entity.User;
import com.hemanth.slmsuite.entity.Part;
import com.hemanth.slmsuite.entity.WarrantyClaim;
import com.hemanth.slmsuite.repository.PartRepository;
import com.hemanth.slmsuite.repository.WarrantyClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WarrantyClaimService {

    private final WarrantyClaimRepository warrantyClaimRepository;
    private final PartRepository partRepository;

    public WarrantyClaimService(WarrantyClaimRepository warrantyClaimRepository, PartRepository partRepository) {
        this.warrantyClaimRepository = warrantyClaimRepository;
        this.partRepository = partRepository;
    }

    public List<WarrantyClaim> getAllClaims() {
        return warrantyClaimRepository.findAll();
    }

    public WarrantyClaim getClaimById(Long id) {
        return warrantyClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));
    }

    public List<WarrantyClaim> getClaimsForCustomer(User customer) {
        return warrantyClaimRepository.findByCustomer(customer);
    }

    public WarrantyClaim submitClaim(User customer, Long partId, String issueDescription, String serialNumber, java.time.LocalDate purchaseDate) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        WarrantyClaim claim = new WarrantyClaim();
        claim.setCustomer(customer);
        claim.setPart(part);
        claim.setIssueDescription(issueDescription);
        claim.setSerialNumber(serialNumber);
        claim.setPurchaseDate(purchaseDate);
        claim.setStatus("SUBMITTED");
        claim.setSubmittedAt(LocalDateTime.now());

        return warrantyClaimRepository.save(claim);
    }

    @Transactional
    public WarrantyClaim approveClaim(Long claimId) {
        WarrantyClaim claim = getClaimById(claimId);
        Part part = claim.getPart();

        if (part.getStockQuantity() == null || part.getStockQuantity() <= 0) {
            throw new InsufficientStockException("Not enough stock for part: " + part.getName());
        }

        part.setStockQuantity(part.getStockQuantity() - 1);
        partRepository.save(part);

        claim.setStatus("PARTS_ISSUED");
        return warrantyClaimRepository.save(claim);
    }
    public WarrantyClaim rejectClaim(Long claimId, String reason) {
        WarrantyClaim claim = getClaimById(claimId);
        claim.setStatus("REJECTED");
        claim.setRejectionReason(reason);
        return warrantyClaimRepository.save(claim);
    }

    public WarrantyClaim markUnderReview(Long claimId) {
        WarrantyClaim claim = getClaimById(claimId);
        claim.setStatus("UNDER_REVIEW");
        return warrantyClaimRepository.save(claim);
    }

    public WarrantyClaim markPaid(Long claimId) {
        WarrantyClaim claim = getClaimById(claimId);
        claim.setStatus("PAID");
        return warrantyClaimRepository.save(claim);
    }
}