package ru.truckfollower.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.Alarm;
import ru.truckfollower.entity.ForbiddenZone;
import ru.truckfollower.entity.Truck;
import ru.truckfollower.model.ForbiddenZoneModel;
import ru.truckfollower.model.TruckRabbitMessageModel;
import ru.truckfollower.repo.AlarmRepo;
import ru.truckfollower.service.polygon.Polygon;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j

public class AlarmService  {

    private final AlarmRepo alarmRepo;
    private final ForbiddenZoneService forbiddenZoneService;


    //мапа uid,value(alarm)
    @Getter
    private final Map<Long, Alarm> trucksInTheForbiddenZone = new ConcurrentHashMap<>();

    @Autowired
    public AlarmService(AlarmRepo alarmRepo, ForbiddenZoneService forbiddenZoneService) {
        this.alarmRepo = alarmRepo;
        this.forbiddenZoneService = forbiddenZoneService;
    }

    public List<Alarm> getAll() {
        return alarmRepo.findAll();
    }

    @PostConstruct
    public void initialize() {

        List<Alarm> alarms = alarmRepo.findAllByZoneLeave(false);

        for (Alarm a : alarms) {
            //truckService.getTruckById();


            trucksInTheForbiddenZone.put(a.getTruckId().getUniqId(), a);
        }

    }



    public Alarm alarmCreate(Truck t, ForbiddenZoneModel forbiddenZoneModel) {//methodname: startTracking
        if (trucksInTheForbiddenZone.containsKey(t.getId())) {
            return trucksInTheForbiddenZone.get(t.getId());
        }

        ForbiddenZone forbiddenZone = forbiddenZoneService.toEntity(forbiddenZoneModel).get();

        Alarm a = Alarm.builder()
                .forbiddenZoneId(forbiddenZone)
                .truckId(t)
                .time(Instant.now())
                .zoneLeave(false)
                .archive(false)
                .build();
        a = alarmRepo.save(a);
        trucksInTheForbiddenZone.put(t.getUniqId(), a);

        return a;
    }

    public void alarmClose(Truck truck) {
        Alarm a = trucksInTheForbiddenZone.get(truck.getUniqId());


        if (a == null) {
            log.error("такого не должно произойти никогда");
        } else {
            a.setZoneLeave(true);
            a.setLeaveTime(Instant.now());
            alarmRepo.save(a);
            trucksInTheForbiddenZone.remove(truck.getUniqId());
        }

    }

    public void handleAlarmTruck(TruckRabbitMessageModel truckRabbitMessageModel) {
        Alarm a = trucksInTheForbiddenZone.get(truckRabbitMessageModel.getUid());
        ForbiddenZoneModel forbiddenZoneModel = forbiddenZoneService.toModel(a.getForbiddenZoneId());

        Polygon polygon = Polygon.geometryToPolygon(forbiddenZoneModel.getGeometry());
        if (!polygon.contains(new Point(truckRabbitMessageModel.getX(), truckRabbitMessageModel.getY()))) {
            a.setLeaveTime(Instant.now());
            a.setZoneLeave(true);
            trucksInTheForbiddenZone.remove(truckRabbitMessageModel.getUid());
            alarmRepo.save(a);

            log.info(a.getTruckId().getName() + " номер: " + a.getTruckId().getCarNumber() + " вышел из запретной зоны \"" + forbiddenZoneModel.getZoneName() + "\", координаты: " + truckRabbitMessageModel.getX() + " " + truckRabbitMessageModel.getY());



        }

    }





}
