package ru.truckfollower.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.truckfollower.entity.Alarm;

import java.util.List;

public interface AlarmRepo extends JpaRepository<Alarm,Long> {

    List<Alarm> findAllByZoneLeave(Boolean zoneLeave);



}
