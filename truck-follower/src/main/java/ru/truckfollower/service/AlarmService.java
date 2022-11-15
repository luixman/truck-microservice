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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j

public class AlarmService  {

    private final AlarmRepo alarmRepo;
    private final ForbiddenZoneService forbiddenZoneService;
    private final TruckService truckService;


    //мапа uniqId,value(alarm)
    @Getter
    private final Map<Long, Alarm> trucksInTheForbiddenZone = new ConcurrentHashMap<>();

    @Autowired
    public AlarmService(AlarmRepo alarmRepo, ForbiddenZoneService forbiddenZoneService, TruckService truckService) {
        this.alarmRepo = alarmRepo;
        this.forbiddenZoneService = forbiddenZoneService;
        this.truckService = truckService;
    }

    public List<Alarm> getAll() {
        return alarmRepo.findAll();
    }

    @PostConstruct
    public void initialize() {

        List<Alarm> alarms = alarmRepo.findAllByZoneLeave(false);

        for (Alarm a : alarms) {
            trucksInTheForbiddenZone.put(a.getTruckId().getUniqId(), a);
        }

        log.info("Alarm service has ben initialized or updated"+trucksInTheForbiddenZone.getClass().getName()+" size:"+trucksInTheForbiddenZone.size());
    }



    public synchronized Alarm alarmCreate(TruckRabbitMessageModel truckRabbitMessageModel, ForbiddenZoneModel forbiddenZoneModel) {//methodname: startTracking

        if (trucksInTheForbiddenZone.containsKey(truckRabbitMessageModel.getUniqId())) {
            return trucksInTheForbiddenZone.get(truckRabbitMessageModel.getUniqId());
        }

        // TODO: 14.11.2022 исправить это
        ForbiddenZone forbiddenZone = forbiddenZoneService.toEntity(forbiddenZoneModel).get();
        Truck truck = truckService.rabbitModelToEntity(truckRabbitMessageModel).get();

        log.info(truck.getName() + " номер: " + truck.getCarNumber() + " Попал в запретную зону \"" + forbiddenZoneModel.getZoneName() + "\", координаты: " + truckRabbitMessageModel.getX() + " " + truckRabbitMessageModel.getY());

        Alarm a = Alarm.builder()
                .forbiddenZoneId(forbiddenZone)
                .truckId(truck)
                .messageTime(truckRabbitMessageModel.getInstant())
                .zoneLeave(false)
                .archive(false)
                .messageTimeWrong(truckRabbitMessageModel.isTimeWrong())
                .build();
        a = alarmRepo.save(a);
        trucksInTheForbiddenZone.put(truckRabbitMessageModel.getUniqId(), a);

        return a;
    }



    public synchronized void handleAlarmTruck(TruckRabbitMessageModel truckRabbitMessageModel) {
        Alarm a = trucksInTheForbiddenZone.get(truckRabbitMessageModel.getUniqId());

        if(a==null)
            return;
        ForbiddenZoneModel forbiddenZoneModel = forbiddenZoneService.toModel(a.getForbiddenZoneId());

        Polygon polygon = Polygon.geometryToPolygon(forbiddenZoneModel.getGeometry());
        if (!polygon.contains(new Point(truckRabbitMessageModel.getX(), truckRabbitMessageModel.getY()))) {
            a.setLeaveTime(truckRabbitMessageModel.getInstant());
            a.setZoneLeave(true);
            trucksInTheForbiddenZone.remove(truckRabbitMessageModel.getUniqId());
            alarmRepo.save(a);

            log.info(a.getTruckId().getName() + " номер: " + a.getTruckId().getCarNumber() + " вышел из запретной зоны \"" + forbiddenZoneModel.getZoneName() + "\", координаты: " + truckRabbitMessageModel.getX() + " " + truckRabbitMessageModel.getY());
        }
    }
}
