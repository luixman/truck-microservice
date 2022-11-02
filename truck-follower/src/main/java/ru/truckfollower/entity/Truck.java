package ru.truckfollower.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@EqualsAndHashCode

public class Truck  {

    private long truckId;
    private String name;
    private double x;
    private double y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Truck truck = (Truck) o;
        return truckId == truck.truckId && Double.compare(truck.x, x) == 0 && Double.compare(truck.y, y) == 0 && Objects.equals(name, truck.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(truckId, name, x, y);
    }
}
