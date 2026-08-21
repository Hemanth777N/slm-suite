package com.hemanth.slmsuite.controller;

import com.hemanth.slmsuite.entity.Part;
import com.hemanth.slmsuite.service.PartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public String listParts(Model model) {
        model.addAttribute("parts", partService.getAllParts());
        model.addAttribute("lowStockParts", partService.getLowStockParts());
        return "parts/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("part", new Part());
        return "parts/form";
    }

    @PostMapping("/save")
    public String saveOrUpdatePart(@ModelAttribute Part part) {
        partService.saveOrUpdatePart(part);
        return "redirect:/parts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("part", partService.getPartById(id));
        return "parts/form";
    }

    @GetMapping("/delete/{id}")
    public String deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return "redirect:/parts";
    }
}