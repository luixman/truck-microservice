package ru.truckfollower.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.truckfollower.entity.Company;

public interface CompanyRepo extends JpaRepository<Company,Long> {

}
