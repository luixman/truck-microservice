package ru.truckfollower.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.config.RabbitMqSendConfig;
import ru.truckfollower.entity.Alarm;
import ru.truckfollower.model.AlarmSendModel;

@Service
public class SendAlarmMessageService {

    private final RabbitTemplate template;
    private final RabbitMqSendConfig config;

    @Autowired
    public SendAlarmMessageService(RabbitTemplate template, RabbitMqSendConfig config) {
        this.template = template;
        this.config = config;
    }

    public void send(AlarmSendModel a) {
        template.convertAndSend(config.getExchange(), config.getRoutingKey(), a);
    }
}
