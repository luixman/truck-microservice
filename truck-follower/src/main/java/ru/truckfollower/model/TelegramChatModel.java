package ru.truckfollower.model;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
public class TelegramChatModel {

    private Set<Long> companyIds = ConcurrentHashMap.newKeySet();
    private InlineKeyboardMarkup inlineKeyboardMarkup;

    @Override
    public String toString() {
        return "TelegramChatModel{" +
                "companyIds=" + companyIds +
                '}';
    }
}
