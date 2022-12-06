package ru.truckfollower.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.*;
import org.springframework.data.geo.Point;


import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Entity
public class Alarm implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint default 0")
    private long id;

    @JsonSerialize(using = InstantSerializer.class)
    @Column(name = "message_time")
    private Instant messageTime;


    @JsonSerialize(using = InstantSerializer.class)
    @Column(name = "leave_time")
    private Instant leaveTime;


    @Column(name = "zone_leave", columnDefinition = "boolean default false")
    private Boolean zoneLeave;

    @Column(name ="message_time_wrong", columnDefinition = "boolean default false")
    private Boolean messageTimeWrong;

    @Column(name ="point_entry")
    private Point pointEntry;

    @Column(name ="point_exit")
    private Point pointExit;


    @JoinColumn(name = "forbidden_zone_id")
    @ManyToOne
    private ForbiddenZone forbiddenZone;


    @JoinColumn(name = "truck_id")
    @ManyToOne()
    private Truck truck;


    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", messageTime=" + messageTime +
                ", leaveTime=" + leaveTime +
                ", zoneLeave=" + zoneLeave +
                ", messageTimeWrong=" + messageTimeWrong +
                '}';
    }
}
