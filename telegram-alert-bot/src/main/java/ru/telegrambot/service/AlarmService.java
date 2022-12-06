package ru.telegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.telegrambot.entity.Alarm;
import ru.telegrambot.entity.Company;
import ru.telegrambot.entity.ForbiddenZone;
import ru.telegrambot.entity.Truck;
import ru.telegrambot.exception.EntityNotFoundException;
import ru.telegrambot.repo.AlarmRepo;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j

public class AlarmService {
    private final AlarmRepo alarmRepo;

    public AlarmService(AlarmRepo alarmRepo) {
        this.alarmRepo = alarmRepo;
    }

    //  private final TelegramAlarmService telegramAlarmService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyyг. HH:mm:ss")
            .withZone(ZoneId.systemDefault());


    public Alarm getAlarmById(Long id) throws EntityNotFoundException {

        Optional<Alarm> a = alarmRepo.findById(id);
        if (a.isEmpty())
            throw new EntityNotFoundException("alarm not found by id= " + id);
        return a.get();
    }

    public String getDetailedMessage(Alarm a) {
        Truck t = a.getTruck();
        ForbiddenZone f = a.getForbiddenZone();
        Company c = a.getTruck().getCompany();

        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDE9B")
                .append(t.getName())
                .append(" (")
                .append(t.getIssueYear())
                .append(") гос.номер: ")
                .append(t.getCarNumber())
                .append("\uD83D\uDE9B\n");

        sb.append("\uD83D\uDFE5Время въезда: ")
                .append(formatter.format(a.getMessageTime()))
                .append("\uD83D\uDFE5\n");

        sb.append("\uD83D\uDFE9Время выезда: ");
        if (a.getLeaveTime() != null)
            sb.append(formatter.format(a.getLeaveTime()));
        else
            sb.append("не выехал");
        sb.append("\uD83D\uDFE9\n");

        if (a.getLeaveTime() != null) {
            Duration duration = Duration.between(a.getMessageTime(), a.getLeaveTime());
            sb.append("🕐Проведено времени: ")
                    .append(String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.toSeconds() % 60))
                    .append("🕐\n");
        }

        sb.append("⚠️Запретная зона: ")
                .append(f.getZoneName())
                .append("⚠️\n");
        sb.append("\uD83C\uDFE2Принадлежит компании: ")
                .append(c.getFullName())
                .append(", ИНН: ")
                .append(c.getInn())
                .append(", телефон: ")
                .append(c.getPhoneNumber())
                .append("\uD83C\uDFE2");
        return sb.toString();
    }

    public List<Alarm> findFirst30ByZoneLeave(Boolean zoneLeave) {
        return alarmRepo.findFirst30ByZoneLeave(zoneLeave);
    }
}
