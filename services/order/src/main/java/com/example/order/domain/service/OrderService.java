package com.example.order.domain.service;


import com.example.order.domain.core.infrastructure.UserRibbonHystrixApi;
import com.example.order.domain.core.root.Order;
import com.example.order.domain.core.vo.CustomerVo;
import com.example.order.domain.core.vo.DriverVo;
import com.example.order.domain.core.vo.FlowState;
import com.example.order.domain.core.vo.IntentionVo;
import com.example.order.domain.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    UserRibbonHystrixApi userService;
    @Autowired
    private OrderRepository orderRepository;
    @Transactional
    public Order createOrder(IntentionVo intention) {
        Order order = new Order();
        CustomerVo customerVo = userService.findCustomerById(intention.getCustomerId());
        DriverVo driverVo = userService.findDriverById(intention.getDriverId());
        order.setCustomer(customerVo);
        order.setDriver(driverVo);
        order.setOrderStatus(FlowState.WAITING_ABOARD.toValue());
        order.setOpened(new Date());
        order.setStartLong(intention.getStartLong());
        order.setStartLat(intention.getStartLat());
        order.setDestLong(intention.getDestLong());
        order.setDestLat(intention.getDestLat());
        order.setIntentionId(String.valueOf(intention.getIntentionId()));
        orderRepository.save(order);
        LOGGER.info("Created new order: {}", order.getOid());
        return order;
    }

    @Transactional
    public void aboard(Order order) {
        order.setOrderStatus(FlowState.WAITING_ARRIVE.toValue());
        order.setOpened(new Date());
        orderRepository.save(order);
    }

    @Transactional
    public void cancel(Order order) {
        Date currentTime = new Date();
        if ((currentTime.getTime() - order.getOpened().getTime()) <= 3 * 60 * 1000L) {
            order.setOrderStatus(FlowState.CANCELED.toValue());
            orderRepository.save(order);
            LOGGER.info("Order {} canceled by user {}", order.getOid(), order.getCustomer().getCustomerName());
        } else {
            throw new Error("Cannot cancel after 3 minutes");
        }
    }
    @Transactional
    public void arrive(Order order) {
        order.setOrderStatus(FlowState.UNPAY.toValue());
        orderRepository.save(order);
        LOGGER.info("Order {} status changed to UNPAY", order.getOid());
    }
}
