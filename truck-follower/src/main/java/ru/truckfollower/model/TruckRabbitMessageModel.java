package ru.truckfollower.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
public class TruckRabbitMessageModel {
    private long uid;
    private double x;
    private double y;



}
