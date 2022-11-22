package ru.truckfollower.entity;


import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Entity
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint default 0")
    private long id;

    @Column(name = "message_time")
    private Instant messageTime;

    @Column(name = "leave_time")
    private Instant leaveTime;

    @Column(name = "zone_leave", columnDefinition = "boolean default false")
    private Boolean zoneLeave;
    @Column(name = "archive", columnDefinition = "boolean default false")
    private Boolean archive;

    @Column(name ="message_time_wrong", columnDefinition = "boolean default false")
    private Boolean messageTimeWrong;

    // TODO: 22.11.2022 REFACTOR
    @Transient
    private Double y;

    @Transient
    private Double x;


    @JoinColumn(name = "forbidden_zone_id")
    @ManyToOne
    private ForbiddenZone forbiddenZoneId;



    @JoinColumn(name = "truck_id")
    @ManyToOne()
    private Truck truckId;


    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", messageTime=" + messageTime +
                ", leaveTime=" + leaveTime +
                ", zoneLeave=" + zoneLeave +
                ", archive=" + archive +
                ", messageTimeWrong=" + messageTimeWrong +
                ", forbiddenZoneId=" + forbiddenZoneId +
                ", truckId=" + truckId +
                '}';
    }
}
