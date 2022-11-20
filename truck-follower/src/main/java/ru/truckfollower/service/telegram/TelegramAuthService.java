package ru.truckfollower.service.telegram;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.TelegramConnection;
import ru.truckfollower.exception.EntityNotFoundException;

@Service
public class TelegramAuthService {

    @Autowired
    TelegramConnectionService telegramConnectionService;

    public boolean hasChatAuth(Long chatId) {

        TelegramConnection telegramConnection;
        try {
            telegramConnection = telegramConnectionService.getByChatId(chatId);
        } catch (EntityNotFoundException e) {
            return false;
        }

        return telegramConnection.getAuthorized();



    }

    public boolean authorizationAttempt(Long chatId, String authKey){
        TelegramConnection telegramConnection;
        try {
            telegramConnection = telegramConnectionService.getByChatId(chatId);
        } catch (EntityNotFoundException e) {
            return false;
        }

        if(telegramConnection.getAuthKey().equals(authKey)) {
            telegramConnection.setAuthorized(true);
            telegramConnectionService.save(telegramConnection);
            return true;
        }
        else return false;
    }
}
