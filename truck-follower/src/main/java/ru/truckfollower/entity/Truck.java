package ru.truckfollower.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;



@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Truck  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "uid")
    private long uniqId;

    @Column(name = "name")
    private String name;

    @Column(name = "car_number")
    private String carNumber;

    @Column(name = "issue_year")
    private int issueYear;

    @Column(name = "other_information")
    private String otherInformation;


    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company companyId; //TODO many to one*/

}
