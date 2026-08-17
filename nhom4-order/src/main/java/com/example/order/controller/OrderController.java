package com.example.order.controller;

import com.example.order.domain.core.root.Order;
import com.example.order.domain.core.vo.IntentionVo;
import com.example.order.domain.repository.OrderRepository;
import com.example.order.domain.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order")
@Validated
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String hello() {
        return "Hello order-service";
    }
    @GetMapping("/all")
    public List<Order> findAll() {
        return (List<Order>) orderRepository.findAll();
    }
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@Valid @RequestBody IntentionVo intention, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                Arrays.asList("error", "Validation failed: " + 
                    bindingResult.getAllErrors().get(0).getDefaultMessage())
            );
        }
        
        try {
            Order order = orderService.createOrder(intention);
            return ResponseEntity.ok(Arrays.asList("success", order.getOid()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Arrays.asList("error", e.getMessage()));
        }
    }
    @PostMapping("/aboard")
    public List<String> aboard(@RequestParam String orderId) {
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
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestParam String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Arrays.asList("error", "Order not found"));
            }
            
            orderService.cancel(orderOpt.get());
            return ResponseEntity.ok(Arrays.asList("success cancel", 
                orderOpt.get().getOid(), 
                orderOpt.get().getCustomer().getCustomerName()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Arrays.asList("error", e.getMessage()));
        }
    }
    @PostMapping("/arrive")
    public List<String> arrive(@RequestParam String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return Arrays.asList("error", "Order not found");
            }
            orderService.arrive(orderOpt.get());
            return Arrays.asList("success arrive", orderOpt.get().getOid(), orderOpt.get().getCustomer().getCustomerName());
        } catch (Exception e) {
            return Arrays.asList("error", e.getMessage());
        }
    }
    @PostMapping("/paying")
    public List<String> paying(@RequestParam String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return Arrays.asList("error", "Order not found");
            }
            orderService.paying(orderOpt.get());
            return Arrays.asList("success paying, da tra", orderOpt.get().getOid(), orderOpt.get().getCustomer().getCustomerName());
        } catch (Exception e) {
            return Arrays.asList("error", e.getMessage());
        }
    }
}
