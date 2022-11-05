package ru.truckfollower.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.truckfollower.entity.ForbiddenZone;

public interface ForbiddenZoneRepo extends JpaRepository<ForbiddenZone,Long> {

}
