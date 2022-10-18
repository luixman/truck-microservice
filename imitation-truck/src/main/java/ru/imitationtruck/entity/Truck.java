package ru.imitationtruck.entity;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Truck {

    private long truckId;
    private String name;
    private double x;
    private double y;

}
