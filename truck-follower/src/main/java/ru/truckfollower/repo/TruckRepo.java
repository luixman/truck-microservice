package ru.truckfollower.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.truckfollower.entity.Truck;

public interface TruckRepo extends JpaRepository<Truck,Long> {


    Truck findFirstByUniqId(Long uniqueId);


}
