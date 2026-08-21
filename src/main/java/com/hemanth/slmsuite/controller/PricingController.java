package com.hemanth.slmsuite.controller;

import com.hemanth.slmsuite.repository.PartRepository;
import com.hemanth.slmsuite.service.PricingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/pricing")
public class PricingController {

    private final PricingService pricingService;
    private final PartRepository partRepository;

    public PricingController(PricingService pricingService, PartRepository partRepository) {
        this.pricingService = pricingService;
        this.partRepository = partRepository;
    }

    @GetMapping
    public String listRules(Model model) {
        model.addAttribute("rules", pricingService.getAllRules());
        return "pricing/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("parts", partRepository.findAll());
        return "pricing/form";
    }

    @PostMapping("/save")
    public String saveRule(@RequestParam Long partId, @RequestParam String customerTier, @RequestParam BigDecimal adjustmentPercent) {
        pricingService.saveRule(partId, customerTier, adjustmentPercent);
        return "redirect:/pricing";
    }

    @GetMapping("/quote")
    public String showQuoteForm(Model model) {
        model.addAttribute("parts", partRepository.findAll());
        return "pricing/quote";
    }

    @PostMapping("/quote")
    public String calculateQuote(@RequestParam Long partId, @RequestParam String customerTier, Model model) {
        BigDecimal price = pricingService.calculatePrice(partId, customerTier);
        model.addAttribute("parts", partRepository.findAll());
        model.addAttribute("calculatedPrice", price);
        return "pricing/quote";
    }
}