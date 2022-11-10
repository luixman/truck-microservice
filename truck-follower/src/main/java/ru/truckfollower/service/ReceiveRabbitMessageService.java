package ru.truckfollower.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.model.TruckRabbitMessageModel;

import java.io.IOException;

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

    @Autowired
    public ReceiveRabbitMessageService(TruckService truckService) {
        this.truckService = truckService;
    }


    @RabbitListener(queues = "truckCordsQueue")
    public void ReceiveMessage(TruckRabbitMessageModel t) throws IOException, ClassNotFoundException {
        System.out.println(t);
        truckService.processTheMessage(t);



    }


}
