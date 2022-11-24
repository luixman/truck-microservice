package ru.truckfollower.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.truckfollower.entity.Alarm;
import ru.truckfollower.repo.AlarmRepo;
import ru.truckfollower.service.SendRabbitAlarmMessage;

import javax.annotation.PostConstruct;

@Controller
@Slf4j
public class StartController {

    @Autowired
    AlarmRepo alarmRepo;

    @Autowired
    SendRabbitAlarmMessage sendRabbitAlarmMessage;

    @PostConstruct
    public void init() throws Exception {
    }
}
