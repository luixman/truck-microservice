package ru.imitationtruck.service;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.imitationtruck.config.RabbitMqConfig;
import ru.imitationtruck.entity.Truck;

@Service
public class SendRabbitMessageService {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private RabbitMqConfig config;

    public void send(Truck t) {

        template.convertAndSend(config.exchange, config.routingKey, t);

    }
}
