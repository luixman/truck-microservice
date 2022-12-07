package ru.telegrambot.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.telegrambot.service.telegram.TelegramBot;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URL;

@Controller
public class TestController {



    @PostConstruct
    @SneakyThrows
    public void init(){

    }



}
