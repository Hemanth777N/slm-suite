package com.hemanth.slmsuite.controller;

import com.hemanth.slmsuite.entity.Warehouse;
import com.hemanth.slmsuite.repository.WarehouseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;

    public WarehouseController(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("warehouses", warehouseRepository.findAll());
        return "warehouses/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        return "warehouses/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Warehouse warehouse) {
        warehouseRepository.save(warehouse);
        return "redirect:/warehouses";
    }
}