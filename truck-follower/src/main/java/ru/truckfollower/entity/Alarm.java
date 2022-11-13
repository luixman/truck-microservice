package ru.truckfollower.entity;


import lombok.*;
import org.springframework.data.jpa.repository.Query;

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




    @Column(name = "time")
    private Instant time;

    @Column(name = "zone_leave", columnDefinition = "boolean default false")
    private Boolean zoneLeave;
    @Column(name = "leave_time")
    private Instant leaveTime;
    @Column(name = "archive", columnDefinition = "boolean default false")
    private Boolean archive;

    /*@Query("select  uid from alarm JOIN truck t on t.id = alarm.truck_id;")
    private Long uniqId;*/
    @JoinColumn(name = "forbidden_zone_id")
    @ManyToOne
    private ForbiddenZone forbiddenZoneId;



    @JoinColumn(name = "truck_id")
    @ManyToOne()
    private Truck truckId;
}
