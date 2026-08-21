package com.hemanth.slmsuite.repository;

import com.hemanth.slmsuite.entity.Order;
import com.hemanth.slmsuite.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDealer(User dealer);
}