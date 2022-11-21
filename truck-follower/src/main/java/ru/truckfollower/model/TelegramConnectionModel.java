package ru.truckfollower.model;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Data
@Builder
public class TelegramConnectionModel {

    Long id;
    Long chatId;
    String authKey;
    Boolean authorized;
    Instant firstAuthTime;
    Set<Long> activatedCompanies;
}
