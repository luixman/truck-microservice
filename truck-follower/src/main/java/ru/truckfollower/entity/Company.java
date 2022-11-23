package ru.truckfollower.entity;


import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "inn")
    private String inn;

    @Column(name = "phone_number")
    private String phoneNumber;
    // TODO: 03.11.2022 '+7(495)775-55-30'  PATTERN





}
