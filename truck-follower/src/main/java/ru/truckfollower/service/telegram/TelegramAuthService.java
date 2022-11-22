package ru.truckfollower.service.telegram;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.TelegramConnection;
import ru.truckfollower.exception.EntityNotFoundException;
import ru.truckfollower.model.TelegramConnectionModel;

@Service
public class TelegramAuthService {

    @Autowired
    TelegramConnectionService telegramConnectionService;

    @Cacheable("authService")
    public boolean hasChatAuth(Long chatId) {
        TelegramConnection telegramConnection;
        try {
            telegramConnection = telegramConnectionService.getByChatId(chatId);
        } catch (EntityNotFoundException e) {
            return false;
        }

        return telegramConnection.getAuthorized();


    }

    @CachePut(value = "authService", key = "#chatId")
    public boolean authorizationAttempt(Long chatId, String authKey) {

        TelegramConnection telegramConnection;
        try {
            telegramConnection = telegramConnectionService.getByChatId(chatId);
        } catch (EntityNotFoundException e) {
            return false;
        }

        if (telegramConnection.getAuthorized()) {
            return true;
        } else if (telegramConnection.getAuthKey().equals(authKey)) {
            telegramConnection.setAuthorized(true);
            telegramConnectionService.save(telegramConnection);
            return true;
        } else return false;
    }

    @CacheEvict("authService")
    public void authorizationDelete(Long chatId) {
        telegramConnectionService.deleteByChatID(chatId);

    }

}
