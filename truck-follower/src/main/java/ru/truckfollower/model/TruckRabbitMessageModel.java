package ru.truckfollower.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonProperty("uid")
    private long uniqId;
    private double x;
    private double y;
    @JsonDeserialize(using = DefaultInstantDeserializer.class)
    private Instant instant;
    private boolean isTimeWrong;




}
