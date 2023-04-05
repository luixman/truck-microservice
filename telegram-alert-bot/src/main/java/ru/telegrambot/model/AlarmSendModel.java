package ru.telegrambot.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.telegrambot.entity.ForbiddenZone;
import ru.telegrambot.entity.Transport;
import ru.telegrambot.service.DefaultInstantDeserializer;


import java.time.Instant;

@Data
@NoArgsConstructor
public class AlarmSendModel {
    long id;
    @JsonDeserialize(using = DefaultInstantDeserializer.class)
    Instant time;
    boolean messageTimeWrong;
    boolean zoneLeave;

    Double x;
    Double y;

    ForbiddenZone forbiddenZone;
    Transport truck;

}
