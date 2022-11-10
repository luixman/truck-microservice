package ru.truckfollower.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.truckfollower.entity.Alarm;

public interface AlarmRepo extends JpaRepository<Alarm,Long> {
}
