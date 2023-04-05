package ru.truckfollower.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.truckfollower.entity.Transport;

public interface TruckRepo extends JpaRepository<Transport,Long> {
    Transport findFirstByUniqId(Long uniqueId);
}
