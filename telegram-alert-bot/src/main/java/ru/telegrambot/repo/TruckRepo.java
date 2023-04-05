package ru.telegrambot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.telegrambot.entity.Transport;

public interface TruckRepo extends JpaRepository<Transport,Long> {
    Transport findFirstByUniqId(Long uniqueId);
}
