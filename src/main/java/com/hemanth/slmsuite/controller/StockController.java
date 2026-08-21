package com.hemanth.slmsuite.controller;

import com.hemanth.slmsuite.entity.Stock;
import com.hemanth.slmsuite.repository.PartRepository;
import com.hemanth.slmsuite.repository.StockRepository;
import com.hemanth.slmsuite.repository.WarehouseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockController {

    private final StockRepository stockRepository;
    private final PartRepository partRepository;
    private final WarehouseRepository warehouseRepository;

    public StockController(StockRepository stockRepository, PartRepository partRepository, WarehouseRepository warehouseRepository) {
        this.stockRepository = stockRepository;
        this.partRepository = partRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @GetMapping
    public String listStock(Model model) {
        model.addAttribute("stockEntries", stockRepository.findAll());
        return "stock/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("stock", new Stock());
        model.addAttribute("parts", partRepository.findAll());
        model.addAttribute("warehouses", warehouseRepository.findAll());
        return "stock/form";
    }

    @PostMapping("/save")
    public String saveStock(@RequestParam Long partId, @RequestParam Long warehouseId, @RequestParam Integer quantity) {
        Stock stock = new Stock();
        stock.setPart(partRepository.findById(partId).orElseThrow());
        stock.setWarehouse(warehouseRepository.findById(warehouseId).orElseThrow());
        stock.setQuantity(quantity);
        stockRepository.save(stock);
        return "redirect:/stock";
    }
}