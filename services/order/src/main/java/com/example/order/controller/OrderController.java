package com.example.order.controller;

import com.example.order.domain.core.root.Order;
import com.example.order.domain.repository.OrderRepository;
import com.example.order.domain.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/cancel")
    public List<String> cancelOrder(@RequestParam int driverId, @RequestParam String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return Arrays.asList("error", "Order not found");
            }
            orderService.cancel(orderOpt.get());
            return Arrays.asList("success cancel", orderOpt.get().getOid(), orderOpt.get().getCustomer().getCustomerName());
        } catch (Exception e) {
            return Arrays.asList("error", e.getMessage());
        }
    }

    @PostMapping("/order/aboard")
    public List<String> aboard(@RequestParam int driverId, @RequestParam String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return Arrays.asList("error", "Order not found");
            }
            orderService.aboard(orderOpt.get());
            return Arrays.asList("success aboard", orderOpt.get().getOid(), orderOpt.get().getCustomer().getCustomerName());
        } catch (Exception e) {
            return Arrays.asList("error", e.getMessage());
        }
    }
    @PostMapping("/order/arrive")
    public List<String> arrive(@RequestParam int driverId, @RequestParam String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return Arrays.asList("error", "Order not found");
            }
            orderService.arrive(orderOpt.get());
            return Arrays.asList("success aboard", orderOpt.get().getOid(), orderOpt.get().getCustomer().getCustomerName());
        } catch (Exception e) {
            return Arrays.asList("error", e.getMessage());
        }
    }
}
