package com.hemanth.slmsuite.controller;

import com.hemanth.slmsuite.entity.User;
import com.hemanth.slmsuite.entity.WarrantyClaim;
import com.hemanth.slmsuite.repository.PartRepository;
import com.hemanth.slmsuite.repository.UserRepository;
import com.hemanth.slmsuite.service.InsufficientStockException;
import com.hemanth.slmsuite.service.WarrantyClaimService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/claims")
public class WarrantyClaimController {

    private final WarrantyClaimService warrantyClaimService;
    private final PartRepository partRepository;
    private final UserRepository userRepository;

    public WarrantyClaimController(WarrantyClaimService warrantyClaimService, PartRepository partRepository, UserRepository userRepository) {
        this.warrantyClaimService = warrantyClaimService;
        this.partRepository = partRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listClaims(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRole().name().equals("ADMIN")) {
            model.addAttribute("claims", warrantyClaimService.getAllClaims());
        } else {
            model.addAttribute("claims", warrantyClaimService.getClaimsForCustomer(currentUser));
        }
        return "claims/list";
    }

    @GetMapping("/new")
    public String showClaimForm(Model model) {
        model.addAttribute("claim", new WarrantyClaim());
        model.addAttribute("parts", partRepository.findAll());
        return "claims/form";
    }

    @PostMapping("/save")
    public String submitClaim(@RequestParam Long partId, @RequestParam String issueDescription,
                              @RequestParam String serialNumber,
                              @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate purchaseDate,
                              Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        warrantyClaimService.submitClaim(currentUser, partId, issueDescription, serialNumber, purchaseDate);
        return "redirect:/claims";
    }
    @GetMapping("/approve/{id}")
    public String approveClaim(@PathVariable Long id, Model model) {
        try {
            warrantyClaimService.approveClaim(id);
        } catch (InsufficientStockException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("claims", warrantyClaimService.getAllClaims());
            return "claims/list";
        }
        return "redirect:/claims";
    }

    @GetMapping("/reject/{id}")
    public String rejectClaim(@PathVariable Long id) {
        warrantyClaimService.rejectClaim(id, "Rejected by admin");
        return "redirect:/claims";
    }

    @GetMapping("/review/{id}")
    public String markUnderReview(@PathVariable Long id) {
        warrantyClaimService.markUnderReview(id);
        return "redirect:/claims";
    }

    @GetMapping("/pay/{id}")
    public String markPaid(@PathVariable Long id) {
        warrantyClaimService.markPaid(id);
        return "redirect:/claims";
    }
}