package ru.truckfollower.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.TelegramConnection;
import ru.truckfollower.exception.EntityNotFoundException;
import ru.truckfollower.repo.TelegramConnectionRepo;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TelegramConnectionService {

    private final TelegramConnectionRepo telegramConnectionRepo;

    @Autowired
    public TelegramConnectionService(TelegramConnectionRepo telegramConnectionRepo) {
        this.telegramConnectionRepo = telegramConnectionRepo;
    }

    public List<TelegramConnection> getAll() {
        return telegramConnectionRepo.findAll();
    }

    public void deleteByChatID(Long chatId) {

        try {
            telegramConnectionRepo.delete(getByChatId(chatId));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public TelegramConnection getByChatId(Long chatId) throws EntityNotFoundException {
        Optional<TelegramConnection> telegramConnection = telegramConnectionRepo.getByChatId(chatId);

        if (telegramConnection.isEmpty()) {
            throw new EntityNotFoundException("TelegramConnection entity not found by chatId: " + chatId);
        }
        return telegramConnection.get();
    }

    public TelegramConnection save(TelegramConnection telegramConnection) {
        telegramConnectionRepo.save(telegramConnection);
        try {
            telegramConnection = getByChatId(telegramConnection.getChatId());
        } catch (EntityNotFoundException e) {
            log.error("Entity not found" + e);
        }
        return telegramConnection;
    }


}
