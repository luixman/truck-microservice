package ru.truckfollower.entity;

import lombok.*;
import org.postgresql.geometric.PGpolygon;

import javax.persistence.*;

@Entity
@Table(name = "forbidden_zone")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ForbiddenZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "zone_name")
    String zoneName;

    @Column(name="deactivated")
    private Boolean deactivated;

    @Column(name ="company_id")
    private Long companyId;


    @Column(name="polygon")
    private String polygon;




}
