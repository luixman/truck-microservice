package ru.telegrambot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.telegrambot.entity.Alarm;

import java.util.List;

public interface AlarmRepo extends JpaRepository<Alarm,Long> {

    List<Alarm> findAllByZoneLeave(Boolean zoneLeave);
    List<Alarm> findFirst30ByZoneLeave(Boolean zoneLeave);



}
