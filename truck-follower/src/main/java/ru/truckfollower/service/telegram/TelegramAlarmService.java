package ru.truckfollower.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.truckfollower.entity.Alarm;
import ru.truckfollower.model.TelegramConnectionModel;
import ru.truckfollower.service.AlarmService;


import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class TelegramAlarmService extends Thread {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyyг. HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    // private final ConcurrentLinkedQueue<Alarm> alarmsQueue = new ConcurrentLinkedQueue<>();

    private final TelegramBot telegramBot;
    private final TelegramAuthService telegramAuthService;
    private final AlarmService alarmService;


    @Autowired
    public TelegramAlarmService(TelegramBot telegramBot, TelegramAuthService telegramAuthService, AlarmService alarmService) {
        this.telegramBot = telegramBot;
        this.telegramAuthService = telegramAuthService;
        this.alarmService = alarmService;
    }

    @PostConstruct
    public void init() {
        this.start();
    }

    @Override
    public void run(){} /*{

        try {
            while (true) {

                // TODO: 22.11.2022 реализовать группированное сообщение


                if (alarmsQueue.isEmpty()) {
                    Thread.sleep(5000);
                    continue;
                }

                Map<Long, TelegramConnectionModel> activatedCompanies = telegramBot.getChatConnections();

                Alarm a = alarmsQueue.element();
                //kod
                for (Map.Entry<Long, TelegramConnectionModel> entry : activatedCompanies.entrySet()) {

                    Set<Long> companies = entry.getValue().getActivatedCompanies();
                    //если содержит /all
                    if (telegramAuthService.hasChatAuth(entry.getKey()))
                        if (companies.contains(0L) || companies.contains((a.getTruckId().getCompanyId().getId()))) {
                            //send message

                            String text = a.getTruckId().getName() +
                                    " гос.номер: " +
                                    a.getTruckId().getCarNumber() +
                                    "\nЛокализован в запретной зоне: \"" +
                                    a.getForbiddenZoneId().getZoneName() +
                                    "\n" +
                                    "Время: " +
                                    formatter.format(a.getMessageTime())
                                    + "\nВведите /get_alarm_" + a.getId() + " чтобы увидеть подробную информацию";
                            telegramBot.execute(SendMessage.builder().chatId(entry.getKey())
                                    .text(text)
                                    .build());
                        }
                }

                alarmsQueue.poll();

                if (alarmsQueue.size() > 3)
                    Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            log.error(Thread.currentThread().getName() + "interrupted");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            run();
        }
    }*/


}
