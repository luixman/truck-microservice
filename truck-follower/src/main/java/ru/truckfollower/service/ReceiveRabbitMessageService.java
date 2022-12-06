package ru.truckfollower.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.truckfollower.entity.Truck;
import ru.truckfollower.exception.EntityNotFoundException;
import ru.truckfollower.model.TruckRabbitMessageModel;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class ReceiveRabbitMessageService {

    private final TruckService truckService;
    private final CheckingTruckCoordinatesService checkingTruckCoordinates;
    private final ExecutorService executorService;

    @Autowired
    public ReceiveRabbitMessageService(TruckService truckService, CheckingTruckCoordinatesService checkingTruckCoordinatesService,
                                       @Value("${services.alarm-service.thread-pool-amount}") int threadPoolCount) {
        this.truckService = truckService;
        this.checkingTruckCoordinates = checkingTruckCoordinatesService;
        executorService = Executors.newFixedThreadPool(threadPoolCount);
    }

    @RabbitListener(queues = "${rabbitmq.receive.queue.name}")
    public void receiveMessage(TruckRabbitMessageModel truckMessageModel) throws IOException, ClassNotFoundException {

        Runnable task = () -> {
            //если время сообщения различается больше чем на день, то у сообщения исправляется время и ставится флаг
            if(isMessageWrongTime(truckMessageModel)){
                truckMessageModel.setTimeWrong(true);
                log.info("this message has the wrong time: "+truckMessageModel);
                truckMessageModel.setInstant(Instant.now());
            } else truckMessageModel.setTimeWrong(false);

            Truck truck;
            try {
                truck = truckService.getTruckByUId(truckMessageModel.getUniqId());
            } catch (EntityNotFoundException e) {
                log.error("EntityNotFoundException: ", e);
                return;
            }
            checkingTruckCoordinates.check(truckMessageModel, truck.getCompany());
        };

        executorService.submit(task);
    }

    private boolean isMessageWrongTime(TruckRabbitMessageModel truckRabbitMessageModel) {

        Instant now =Instant.now();
        Instant messageTime =truckRabbitMessageModel.getInstant();
        Duration duration =Duration.between(now,messageTime);

        return Math.abs(duration.getSeconds()) > 86_400;
    }


}
