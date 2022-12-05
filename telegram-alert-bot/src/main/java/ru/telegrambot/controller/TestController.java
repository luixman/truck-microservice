package ru.telegrambot.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.telegrambot.service.telegram.TelegramBot;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
public class TestController {

    @Autowired
    TelegramBot telegramBot;

    @PostConstruct
    @SneakyThrows
    public void init(){

/*        InputFile inputFile = new InputFile();
        inputFile.setMedia(downloadPhoto(),"File");


        telegramBot.execute(SendPhoto.builder()
                        .caption("\uD83D\uDE9BГАЗель NEXT (2017) гос.номер: C423CO197\uD83D\uDE9B\n" +
                                "\uD83D\uDEA9Время въезда: 02 декабря 2022г. 22:12:26\uD83D\uDEA9\n" +
                                "\uD83C\uDFC1Время выезда: 02 декабря 2022г. 22:12:26\uD83C\uDFC1\n" +
                                "\uD83D\uDD50Проведено времени: 01:00:00\uD83D\uDD50\n" +
                                "⚠️Запретная зона: Съяново-1⚠️\n" +
                                "\uD83C\uDFE2Принадлежит компании: ГК «Деловые Линии», ИНН: 7826156685, телефон: +7(495)775-55-30\uD83C\uDFE2")
                        .photo(inputFile)
                        .chatId(-1001856410390L)
                .build());*/
    }


    @SneakyThrows
    public InputStream downloadPhoto(){
        URL url = new URL("https://static-maps.yandex.ru/1.x/?" +
                "l=map&pl=f:bd09097F,c:bd0909FF,w:5,37.41589648168943,54.99227585133682," +
                "37.406455105957015,54.98842643170247," +
                "37.421389645751944,54.98536637205486," +
                "37.424994534667945,54.99286803682638," +
                "37.41589648168943,54.99227585133682" +
                "&pt=37.41898638647444,54.99228818846042,pm2rdl~37.417525318833505,54.985838525111454,pm2gnl");


       // HttpURLConnection urlConnection =(HttpURLConnection)  url.openConnection();

        return url.openConnection().getInputStream();

    }

}
