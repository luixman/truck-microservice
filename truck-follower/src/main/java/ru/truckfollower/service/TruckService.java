package ru.truckfollower.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.Truck;
import ru.truckfollower.exception.EntityNotFoundException;
import ru.truckfollower.model.TruckRabbitMessageModel;
import ru.truckfollower.repo.TruckRepo;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TruckService {

    private final TruckRepo truckRepo;
    //мапа uniqId,truck
    private Map<Long, Truck> truckMap;

    @Autowired
    public TruckService( TruckRepo truckRepo) {
        this.truckRepo = truckRepo;
    }

    @PostConstruct
    @Scheduled(fixedDelayString = "${scheduler-time.service.truck-service}", timeUnit = TimeUnit.SECONDS)
    // TODO: 05.11.2022 добавить шедулер
    public void initialize() {
        Map<Long, Truck> map = new HashMap<>();

        for (Truck t :
                getAll()) {
            map.put(t.getUniqId(), t);
        }
        truckMap = map;

        log.info("Truck Service has ben initialized or updated. " + truckMap.getClass().getName() + "size: " + truckMap.size());
    }


    public Truck getTruckByUId(long uid) throws EntityNotFoundException {
        Truck truck = truckMap.get(uid);
        if(Objects.isNull(truck))
            throw new EntityNotFoundException("Truck not found");
        return truck;
    }

    public  Optional<Truck> rabbitModelToEntity(TruckRabbitMessageModel rabbitModel) {

        Optional<Truck> truck;
        truck = Optional.of(truckRepo.findFirstByUniqId(rabbitModel.getUniqId()));
        return truck;
    }

    public List<Truck> getAll() {
        return truckRepo.findAll();
    }


}

