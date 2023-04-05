package ru.telegrambot.entity;




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
    private Long id;

    @Column(name = "message_time")
    private Instant messageTime;

    @Column(name = "leave_time")
    private Instant leaveTime;


    @Column(name = "zone_leave", columnDefinition = "boolean default false")
    private Boolean zoneLeave;

    @Column(name ="message_time_wrong", columnDefinition = "boolean default false")
    private Boolean messageTimeWrong;

    @Column(name ="point_entry")

    //private Point pointEntry;
    org.springframework.data.geo.Point pointEntry;

    @Column(name ="point_exit")
    //private Point pointExit;
    org.springframework.data.geo.Point pointExit;


    @JoinColumn(name = "forbidden_zone_id")
    @ManyToOne
    private ForbiddenZone forbiddenZone;

    @JoinColumn(name = "truck_id")
    @ManyToOne()
    private Transport truck;


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
