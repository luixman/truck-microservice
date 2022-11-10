package ru.truckfollower.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "truck_id")
    private Long truckId;

    @Column(name = "forbidden_zone_id")
    private Long forbiddenZone;


    @Column(name = "time")
    private Date time;

    @Column(name = "zone_leave")
    private Boolean zoneLeave;
    @Column(name = "leave_time")
    private Date leaveTime;
    @Column(name = "archive")
    private Boolean archive;

}
