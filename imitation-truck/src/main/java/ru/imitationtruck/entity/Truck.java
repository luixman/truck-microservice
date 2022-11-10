package ru.imitationtruck.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Truck implements Serializable {

    private long uid;
    private double x;
    private double y;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
   // @JsonFormat(pattern = "dd-MM-yyyy")
    private Instant instant;

    public Truck(long uid, double x, double y) {
        this.uid = uid;
        this.x = x;
        this.y = y;
        instant = Instant.now();

    }
}
