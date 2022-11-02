package ru.imitationtruck.entity;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Truck implements Serializable {

    private long truckId;
    private String name;
    private double x;
    private double y;



}
