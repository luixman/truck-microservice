package ru.truckfollower.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.truckfollower.entity.Alarm;
import ru.truckfollower.model.TelegramChatModel;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class TelegramAlarmService extends Thread {

    private final ConcurrentLinkedQueue<Alarm> alarmsQueue = new ConcurrentLinkedQueue<>();

    private final TelegramBot telegramBot;


    @Autowired
    public TelegramAlarmService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        this.start();
    }

    @Override
    public void run() {

        try {
            while (true) {
                Map<Long, TelegramChatModel> activatedCompanies = telegramBot.getActivatedCompanies();

                if (alarmsQueue.isEmpty()) {
                    Thread.sleep(5000);
                    continue;
                }
                Alarm a = alarmsQueue.poll();


                for (Map.Entry<Long, TelegramChatModel> entry : activatedCompanies.entrySet()) {
                    //если содержит поле ALL
                    if (entry.getValue().getCompanyIds().contains(0L)) {
                        String sb = a.getTruckId().getName() +
                                " номер: " +
                                a.getTruckId().getCarNumber() +
                                "\n попал в запретную зону \"" +
                                a.getForbiddenZoneId().getZoneName() +
                                "\n" +
                                "Время: " +
                                a.getMessageTime();
                        //DateTimeFormatter.ofPattern("dd-MM-yyyy  HH:mm:ss").format(a.getMessageTime())

                        telegramBot.execute(SendMessage.builder()
                                .chatId(entry.getKey())
                                .text(sb)
                                .build());
                        telegramBot.execute(SendLocation.builder()
                                .chatId(entry.getKey())
                                .latitude(a.getX())
                                .longitude(a.getY())
                                .build());
                    } else if (entry.getValue().getCompanyIds().contains(a.getTruckId().getCompanyId().getId())) {

                        String sb = a.getTruckId().getName() +
                                " номер: " +
                                a.getTruckId().getCarNumber() +
                                "\n попал в запретную зону \"" +
                                a.getForbiddenZoneId().getZoneName() +
                                "\n" +
                                "Время: " +
                                a.getMessageTime();
                        //DateTimeFormatter.ofPattern("dd-MM-yyyy  HH:mm:ss").format(a.getMessageTime())

                        telegramBot.execute(SendMessage.builder()
                                .chatId(entry.getKey())
                                .text(sb)
                                .build());
                        telegramBot.execute(SendLocation.builder()
                                .chatId(entry.getKey())
                                .latitude(a.getX())
                                .longitude(a.getY())
                                .build());


                    }
                }
                if (alarmsQueue.size() > 3)
                    Thread.sleep(3000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addAlarm(Alarm alarm) {
        alarmsQueue.add(alarm);
    }
}
