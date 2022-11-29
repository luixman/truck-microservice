package ru.telegrambot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.telegrambot.entity.Truck;

public interface TruckRepo extends JpaRepository<Truck,Long> {
    Truck findFirstByUniqId(Long uniqueId);
}
