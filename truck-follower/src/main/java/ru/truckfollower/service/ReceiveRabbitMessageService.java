package ru.truckfollower.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.Truck;
import ru.truckfollower.exception.EntityNotFoundException;
import ru.truckfollower.model.TruckRabbitMessageModel;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Optional;

@Service
@Slf4j
public class ReceiveRabbitMessageService {
/*

    @Autowired
    private RabbitMqConfig config;

    @Autowired
    private RabbitTemplate template;
*/


    private final TruckService truckService;
    private final CheckingTruckCoordinatesService checkingTruckCoordinates;
    private final AlarmService alarmService;

    @Autowired
    public ReceiveRabbitMessageService(TruckService truckService, CheckingTruckCoordinatesService checkingTruckCoordinatesService,
                                       AlarmService alarmService) {
        this.truckService = truckService;
        this.checkingTruckCoordinates = checkingTruckCoordinatesService;
        this.alarmService = alarmService;
    }


    @RabbitListener(queues = "truckCordsQueue")
    public void ReceiveMessage(TruckRabbitMessageModel truckRabbitMessageModel) throws IOException, ClassNotFoundException {
        // TODO: 13.11.2022 проверка на старость сообщения. 
        Truck truck;
        try {
             truck = truckService.getTruckByUId(truckRabbitMessageModel.getUid());
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException: ",e);
            return;
        }
            checkingTruckCoordinates.check(truckRabbitMessageModel, truck.getCompanyId());

    }

    private boolean isMessageTimeOld(){
        // TODO: 10.11.2022  
        return false;
    }


}
