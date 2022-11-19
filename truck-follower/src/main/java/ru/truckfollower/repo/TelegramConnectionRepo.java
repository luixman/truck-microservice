package ru.truckfollower.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.truckfollower.entity.TelegramConnection;

import java.util.Optional;

public interface TelegramConnectionRepo extends JpaRepository<TelegramConnection,Long> {


    Optional<TelegramConnection> getByChatId(Long chatID);
}
