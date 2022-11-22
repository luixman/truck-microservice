package ru.truckfollower.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.truckfollower.entity.TelegramConnection;
import ru.truckfollower.repo.TelegramConnectionRepo;
import ru.truckfollower.service.AlarmService;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@Slf4j
public class StartController {


    @PostConstruct
    public void init() throws Exception {


    }

}
