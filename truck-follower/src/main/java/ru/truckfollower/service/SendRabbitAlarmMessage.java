package ru.truckfollower.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.config.rabbitMq.RabbitMqConfig;
import ru.truckfollower.config.rabbitMq.RabbitMqSendConfig;
import ru.truckfollower.entity.Alarm;

@Service
public class SendRabbitAlarmMessage {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private RabbitMqSendConfig config;

    public void send(Alarm a){
        template.convertAndSend(config.getExchange(),config.getRoutingKey(),a);
    }
}
