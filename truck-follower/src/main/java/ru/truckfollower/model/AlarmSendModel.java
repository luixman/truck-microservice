package ru.truckfollower.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.truckfollower.entity.ForbiddenZone;
import ru.truckfollower.entity.Transport;

import java.time.Instant;

@Data
@NoArgsConstructor
public class AlarmSendModel {
    Long id;
    @JsonSerialize(using = InstantSerializer.class)
    Instant time;
    Boolean messageTimeWrong;
    Boolean zoneLeave;

    Double x;
    Double y;

    ForbiddenZone forbiddenZone;
    Transport truck;

}
