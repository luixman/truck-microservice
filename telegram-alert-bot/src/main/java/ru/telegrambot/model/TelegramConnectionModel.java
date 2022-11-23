package ru.telegrambot.model;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class TelegramConnectionModel {
    Long id;
    Long chatId;
    String authKey;
    Boolean authorized;
    Instant firstAuthTime;
    Set<Long> activatedCompanies;
    Integer page;
}
