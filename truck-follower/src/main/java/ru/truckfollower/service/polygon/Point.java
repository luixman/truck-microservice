package ru.truckfollower.service.polygon;

import lombok.*;
import org.hibernate.annotations.Immutable;



@AllArgsConstructor
@Immutable
@Getter
@ToString
public class Point {
    private double x;
    private double y;

}
