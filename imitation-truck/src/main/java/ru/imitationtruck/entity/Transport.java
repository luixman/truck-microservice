package ru.imitationtruck.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.*;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Transport implements Serializable {

    private long uid;
    private double x;
    private double y;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant instant;

    public Transport(long uid, double x, double y) {
        this.uid = uid;
        this.x = x;
        this.y = y;
        instant = Instant.now(Clock.system(ZoneId.of("Europe/Moscow")));
    }
}
