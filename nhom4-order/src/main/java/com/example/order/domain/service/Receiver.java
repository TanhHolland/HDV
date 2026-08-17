package com.example.order.domain.service;

import com.example.order.domain.core.vo.IntentionVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "intention")
public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void receiveMessage(String message) {
        LOGGER.info("Received new intention <" + message + ">");
        System.out.println(message);
        try {
            IntentionVo vo = mapper.readValue(message, IntentionVo.class);
            orderService.createOrder(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receivePositionUpdate(String message) {
        LOGGER.info("Received position update " + message);
        try {
            String[] values = message.split("\\|");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
