package ru.truckfollower.entity;


import lombok.*;

import javax.persistence.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString

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

    /*@Query("select  uniqId from alarm JOIN truck t on t.id = alarm.truck_id;")
    private Long uniqId;*/
    @JoinColumn(name = "forbidden_zone_id")
    @ManyToOne
    private ForbiddenZone forbiddenZoneId;



    @JoinColumn(name = "truck_id")
    @ManyToOne()
    private Truck truckId;
}
