package ru.truckfollower.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.Truck;
import ru.truckfollower.model.TruckRabbitModel;
import ru.truckfollower.repo.TruckRepo;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
public class TruckService {


    private final CheckingTruckCoordinatesOnTheForbiddenZone checkingTruckCoordinates;
    private final TruckRepo truckRepo;

    //мапа uniqId,truck
    private Map<Long, Truck> truckMap;


    @Autowired
    public TruckService(CheckingTruckCoordinatesOnTheForbiddenZone checkingTruckCoordinates, TruckRepo truckRepo) {
        this.checkingTruckCoordinates = checkingTruckCoordinates;
        this.truckRepo = truckRepo;
    }

    @PostConstruct
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


    public void processTheMessage(TruckRabbitModel truckRabbitModel) {

        Truck t = truckMap.get(truckRabbitModel.getUid());

        if (Objects.isNull(t))
            log.info("Unknown truck");
        else {
            checkingTruckCoordinates.check(truckRabbitModel, t.getCompany_id());
        }

    }

    public Optional<Truck> RabbitModelToEntity(TruckRabbitModel rabbitModel) {

        Optional<Truck> truck;
        truck = Optional.of(truckRepo.findFirstByUniqId(rabbitModel.getUid()));
        return truck;
    }

    public List<Truck> getAll() {
        return truckRepo.findAll();
    }


}

