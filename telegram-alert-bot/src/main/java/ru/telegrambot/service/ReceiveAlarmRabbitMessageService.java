package ru.telegrambot.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.telegrambot.config.RabbitMqConfig;
import ru.telegrambot.entity.Alarm;
import ru.telegrambot.model.AlarmSendModel;
import ru.telegrambot.service.telegram.TelegramAlarmService;


@Slf4j
@Service
public class ReceiveAlarmRabbitMessageService {

    private final TelegramAlarmService telegramAlarmService;
    @Autowired
    public ReceiveAlarmRabbitMessageService(TelegramAlarmService telegramAlarmService) {
        this.telegramAlarmService = telegramAlarmService;
    }


    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(AlarmSendModel a){

        System.out.println(a);
        /*log.info("New message: "+a.getId());
        telegramAlarmService.send(a);*/


    }

}
