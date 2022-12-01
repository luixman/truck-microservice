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

    public void send(Alarm a) {


        // TODO: 01.12.2022 преобразовываем в модель и отправляем
        AlarmSendModel alarmSendModel = new AlarmSendModel();

        alarmSendModel.setId(a.getId());
        alarmSendModel.setMessageTimeWrong(a.getMessageTimeWrong());
        alarmSendModel.setZoneLeave(a.getZoneLeave());
        alarmSendModel.setTruck(a.getTruck());
        alarmSendModel.setForbiddenZone(a.getForbiddenZone());

        if (a.getZoneLeave())
            alarmSendModel.setTime(a.getLeaveTime());
        else
            alarmSendModel.setTime(a.getMessageTime());



        template.convertAndSend(config.getExchange(), config.getRoutingKey(), alarmSendModel);
    }
}
