package com.hemanth.slmsuite.service;

import com.hemanth.slmsuite.entity.*;
import com.hemanth.slmsuite.repository.OrderRepository;
import com.hemanth.slmsuite.repository.PartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PartRepository partRepository;

    public OrderService(OrderRepository orderRepository, PartRepository partRepository) {
        this.orderRepository = orderRepository;
        this.partRepository = partRepository;
    }

    public List<Order> getOrdersForDealer(User dealer) {
        return orderRepository.findByDealer(dealer);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order placeOrder(User dealer, Long partId, Integer quantity) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        Order order = new Order();
        order.setDealer(dealer);
        order.setPart(part);
        order.setQuantity(quantity);
        order.setOrderedAt(LocalDateTime.now());
        order.setStatus("PLACED");

        return orderRepository.save(order);
    }

    @Transactional
    public Order approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Part part = order.getPart();

        if (part.getStockQuantity() < order.getQuantity()) {
            throw new InsufficientStockException("Not enough stock for part: " + part.getName());
        }

        part.setStockQuantity(part.getStockQuantity() - order.getQuantity());
        partRepository.save(part);

        order.setStatus("APPROVED");
        return orderRepository.save(order);
    }

    public Order rejectOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("REJECTED");
        return orderRepository.save(order);
    }

    public Order shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("SHIPPED");
        return orderRepository.save(order);
    }

    public Order deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("DELIVERED");
        return orderRepository.save(order);
    }
}