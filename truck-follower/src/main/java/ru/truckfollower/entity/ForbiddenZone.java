package ru.truckfollower.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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


    @JsonIgnore
    @Column(name="polygon")
    private String polygon;

}
