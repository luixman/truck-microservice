package ru.truckfollower.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import ru.truckfollower.service.ReceiveRabbitMessageService;

@Controller
public class StartController {
    @Autowired
    ReceiveRabbitMessageService messageService;


    @PostMapping
    public void init(){


    }

}
