package com.hemanth.slmsuite.controller;

import com.hemanth.slmsuite.entity.User;
import com.hemanth.slmsuite.repository.PartRepository;
import com.hemanth.slmsuite.repository.UserRepository;
import com.hemanth.slmsuite.service.InsufficientStockException;
import com.hemanth.slmsuite.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final PartRepository partRepository;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, PartRepository partRepository, UserRepository userRepository) {
        this.orderService = orderService;
        this.partRepository = partRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listOrders(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRole().name().equals("ADMIN")) {
            model.addAttribute("orders", orderService.getAllOrders());
        } else {
            model.addAttribute("orders", orderService.getOrdersForDealer(currentUser));
        }
        return "orders/list";
    }

    @GetMapping("/new")
    public String showOrderForm(Model model) {
        model.addAttribute("parts", partRepository.findAll());
        return "orders/form";
    }

    @PostMapping("/place")
    public String placeOrder(@RequestParam Long partId, @RequestParam Integer quantity, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        orderService.placeOrder(currentUser, partId, quantity);
        return "redirect:/orders";
    }

    @GetMapping("/approve/{id}")
    public String approveOrder(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            orderService.approveOrder(id);
        } catch (InsufficientStockException e) {
            model.addAttribute("errorMessage", e.getMessage());
            User currentUser = userRepository.findByUsername(authentication.getName()).orElseThrow();
            model.addAttribute("orders", currentUser.getRole().name().equals("ADMIN")
                    ? orderService.getAllOrders() : orderService.getOrdersForDealer(currentUser));
            return "orders/list";
        }
        return "redirect:/orders";
    }

    @GetMapping("/reject/{id}")
    public String rejectOrder(@PathVariable Long id) {
        orderService.rejectOrder(id);
        return "redirect:/orders";
    }

    @GetMapping("/ship/{id}")
    public String shipOrder(@PathVariable Long id) {
        orderService.shipOrder(id);
        return "redirect:/orders";
    }

    @GetMapping("/deliver/{id}")
    public String deliverOrder(@PathVariable Long id) {
        orderService.deliverOrder(id);
        return "redirect:/orders";
    }
}