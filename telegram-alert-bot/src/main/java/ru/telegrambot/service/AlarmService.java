package ru.telegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.telegrambot.entity.Alarm;
import ru.telegrambot.exception.EntityNotFoundException;
import ru.telegrambot.repo.AlarmRepo;


import java.util.Optional;

@Service
@Slf4j

public class AlarmService {
    private final AlarmRepo alarmRepo;

    public AlarmService(AlarmRepo alarmRepo) {
        this.alarmRepo = alarmRepo;
    }

    //  private final TelegramAlarmService telegramAlarmService;


    public Alarm getAlarmById(Long id) throws EntityNotFoundException {

        Optional<Alarm> a = alarmRepo.findById(id);
        if(a.isEmpty())
            throw new EntityNotFoundException("alarm not found by id= "+id);
        return a.get();
    }
}
