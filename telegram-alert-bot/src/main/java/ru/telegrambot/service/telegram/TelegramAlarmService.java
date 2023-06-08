package ru.telegrambot.service.telegram;

import com.google.common.util.concurrent.AtomicDouble;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.telegrambot.entity.Alarm;
import ru.telegrambot.model.AlarmSendModel;
import ru.telegrambot.model.TelegramConnectionModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class TelegramAlarmService {
    private final TelegramBot telegramBot;

    private final TelegramAuthService telegramAuthService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyyг. HH:mm:ss")
            .withZone(ZoneId.systemDefault());


    public TelegramAlarmService(TelegramBot telegramBot, TelegramAuthService telegramAuthService) {
        this.telegramBot = telegramBot;
        this.telegramAuthService = telegramAuthService;
    }


    @SneakyThrows //для всех случаев, кроме TelegramApiException
    public void send(AlarmSendModel a) {
        sendNoZoneLeaveMessage(a);
    }

    private void sendNoZoneLeaveMessage(AlarmSendModel a) throws InterruptedException {
        Map<Long, TelegramConnectionModel> chatConnections = telegramBot.getChatConnections();

        for (Map.Entry<Long, TelegramConnectionModel> entry : chatConnections.entrySet()) {
            Set<Long> companies = entry.getValue().getActivatedCompanies();
            //если содержит /all
            if (telegramAuthService.hasChatAuth(entry.getKey()))
                if (companies.contains(0L) || companies.contains((a.getTruck().getCompany().getId()))) {
                    //send message

                    String text = "";
                    if (!a.isZoneLeave()) {
                        text = "❗️❗️❗️\n" +
                                a.getTruck().getName() +
                                " гос.номер: " +
                                a.getTruck().getCarNumber() +
                                "\nЛокализован в запретной зоне: \"" +
                                a.getForbiddenZone().getZoneName() +
                                "\n" +
                                "Время: " +
                                formatter.format(a.getTime())
                                + "\nПодробнее: /get_alarm_" + a.getId()
                                + "\n❗️❗️❗️";
                    } else {
                        text = "✅✅✅\n" +
                                a.getTruck().getName() +
                                " гос.номер: " +
                                a.getTruck().getCarNumber() +
                                "\nПокинул запретную зону: \"" +
                                a.getForbiddenZone().getZoneName() +
                                "\n" +
                                "Время выхода: " +
                                formatter.format(a.getTime())
                                + "\nПодробнее: /get_alarm_" + a.getId()
                                + "\n✅✅✅";
                    }
                    try {
                        telegramBot.execute(SendMessage.builder().chatId(entry.getKey())
                                .text(text)
                                .build());

                        log.info("Chat: " + entry.getKey() + ". Notification sent. notification id: " + a.getId());
                    } catch (Exception e) {
                        log.error(e.getMessage() + ". try again in 10 seconds");
                        Thread.sleep(10000);
                        send(a);
                    }
                }
        }
        //сон после отправки сообщения, из-за ограничений телеграмма
        Thread.sleep(1000);
    }
}
