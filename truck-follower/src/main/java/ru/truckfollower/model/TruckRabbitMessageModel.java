package ru.truckfollower.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.*;
import ru.truckfollower.service.DefaultInstantDeserializer;

import java.time.Instant;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TruckRabbitMessageModel {
    private long uid;
    private double x;
    private double y;
    //@JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = DefaultInstantDeserializer.class)
    private Instant instant;

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }
}
