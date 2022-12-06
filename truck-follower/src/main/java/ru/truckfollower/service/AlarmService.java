package ru.truckfollower.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.Alarm;
import ru.truckfollower.entity.ForbiddenZone;
import ru.truckfollower.entity.Truck;
import ru.truckfollower.exception.EntityNotFoundException;
import ru.truckfollower.model.AlarmSendModel;
import ru.truckfollower.model.ForbiddenZoneModel;
import ru.truckfollower.model.TruckRabbitMessageModel;
import ru.truckfollower.repo.AlarmRepo;
import ru.truckfollower.service.polygon.Polygon;

import javax.annotation.PostConstruct;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j

public class AlarmService {
    private final AlarmRepo alarmRepo;
    private final ForbiddenZoneService forbiddenZoneService;
    private final TruckService truckService;
    private final SendAlarmMessageService sendAlarmMessageService;

    //мапа uniqId,value(alarm)
    @Getter
    private final Map<Long, Alarm> trucksInTheForbiddenZone = new ConcurrentHashMap<>();

    @Autowired
    public AlarmService(AlarmRepo alarmRepo, ForbiddenZoneService forbiddenZoneService, TruckService truckService, SendAlarmMessageService sendAlarmMessageService) {
        this.alarmRepo = alarmRepo;
        this.forbiddenZoneService = forbiddenZoneService;
        this.truckService = truckService;
        this.sendAlarmMessageService = sendAlarmMessageService;
    }

    public List<Alarm> getAll() {
        return alarmRepo.findAll();
    }

    @PostConstruct
    public void initialize() {

        List<Alarm> alarms = alarmRepo.findAllByZoneLeave(false);
        for (Alarm a : alarms) {
            trucksInTheForbiddenZone.put(a.getTruck().getUniqId(), a);
        }
        log.info("Alarm service has ben initialized or updated" + trucksInTheForbiddenZone.getClass().getName() + " size:" + trucksInTheForbiddenZone.size());
    }


    @Transient
    public synchronized Alarm alarmCreate(TruckRabbitMessageModel truckRabbitMessageModel, ForbiddenZoneModel forbiddenZoneModel) {//methodname: startTracking

        if (trucksInTheForbiddenZone.containsKey(truckRabbitMessageModel.getUniqId())) {
            return trucksInTheForbiddenZone.get(truckRabbitMessageModel.getUniqId());
        }

        // TODO: 14.11.2022 исправить это
        ForbiddenZone forbiddenZone = forbiddenZoneService.toEntity(forbiddenZoneModel).get();
        Truck truck = truckService.rabbitModelToEntity(truckRabbitMessageModel).get();

        log.info(truck.getName() + " номер: " + truck.getCarNumber() + " Попал в запретную зону \"" + forbiddenZoneModel.getZoneName() + "\", координаты: " + truckRabbitMessageModel.getX() + " " + truckRabbitMessageModel.getY());

        Alarm a = Alarm.builder()
                .forbiddenZone(forbiddenZone)
                .truck(truck)
                .messageTime(truckRabbitMessageModel.getInstant())
                .zoneLeave(false)
                .messageTimeWrong(truckRabbitMessageModel.isTimeWrong())
                .pointEntry(new org.springframework.data.geo.Point(truckRabbitMessageModel.getX(), truckRabbitMessageModel.getY()))
                .build();
        a = alarmRepo.save(a);
        trucksInTheForbiddenZone.put(truckRabbitMessageModel.getUniqId(), a);

        AlarmSendModel alarmSendModel = toModel(a);
        alarmSendModel.setX(truckRabbitMessageModel.getX());
        alarmSendModel.setY(truckRabbitMessageModel.getY());
        sendAlarmMessageService.send(alarmSendModel);
        return a;
    }


    @Transient
    public synchronized void handleAlarmTruck(TruckRabbitMessageModel truckRabbitMessageModel) {
        Alarm a = trucksInTheForbiddenZone.get(truckRabbitMessageModel.getUniqId());

        if (a == null)
            return;
        ForbiddenZoneModel forbiddenZoneModel = forbiddenZoneService.toModel(a.getForbiddenZone());

        Polygon polygon = Polygon.geometryToPolygon(forbiddenZoneModel.getGeometry());
        if (!polygon.contains(new Point(truckRabbitMessageModel.getX(), truckRabbitMessageModel.getY()))) {
            a.setLeaveTime(truckRabbitMessageModel.getInstant());
            a.setZoneLeave(true);
            trucksInTheForbiddenZone.remove(truckRabbitMessageModel.getUniqId());
            a.setPointExit(new org.springframework.data.geo.Point(truckRabbitMessageModel.getX(), truckRabbitMessageModel.getY()));
            alarmRepo.save(a);

            AlarmSendModel alarmSendModel = toModel(a);
            alarmSendModel.setX(truckRabbitMessageModel.getX());
            alarmSendModel.setY(truckRabbitMessageModel.getY());

            sendAlarmMessageService.send(alarmSendModel);

            log.info(a.getTruck().getName() + " номер: " + a.getTruck().getCarNumber() + " вышел из запретной зоны \"" + forbiddenZoneModel.getZoneName() + "\", координаты: " + truckRabbitMessageModel.getX() + " " + truckRabbitMessageModel.getY());
        }
    }

    public Alarm getAlarmById(Long id) throws EntityNotFoundException {

        Optional<Alarm> a = alarmRepo.findById(id);
        if (a.isEmpty())
            throw new EntityNotFoundException("alarm not found by id= " + id);
        return a.get();
    }

    private AlarmSendModel toModel(Alarm a) {
        AlarmSendModel alarmSendModel = new AlarmSendModel();
        alarmSendModel.setId(a.getId());
        alarmSendModel.setMessageTimeWrong(a.getMessageTimeWrong());
        alarmSendModel.setZoneLeave(a.getZoneLeave());
        alarmSendModel.setTruck(a.getTruck());
        alarmSendModel.setForbiddenZone(a.getForbiddenZone());

        if (a.getZoneLeave())
            alarmSendModel.setTime(a.getLeaveTime());
        else
            alarmSendModel.setTime(a.getMessageTime());
        return alarmSendModel;
    }
}
