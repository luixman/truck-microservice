package ru.truckfollower.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.xml.bind.v2.TODO;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "short_name")
    String shortName;

    @Column(name = "inn")
    String inn;

    @Column(name = "phone_number")
    String phoneNumber;
    // TODO: 03.11.2022 '+7(495)775-55-30'  PATTERN


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        if (id != null ? !id.equals(company.id) : company.id != null) return false;
        if (fullName != null ? !fullName.equals(company.fullName) : company.fullName != null) return false;
        if (shortName != null ? !shortName.equals(company.shortName) : company.shortName != null) return false;
        if (inn != null ? !inn.equals(company.inn) : company.inn != null) return false;
        return phoneNumber != null ? phoneNumber.equals(company.phoneNumber) : company.phoneNumber == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (inn != null ? inn.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        return result;
    }

}
