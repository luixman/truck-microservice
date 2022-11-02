package ru.truckfollower.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.truckfollower.config.RabbitMqConfig;
import ru.truckfollower.entity.Truck;

import javax.sound.midi.Track;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Service
@Slf4j
public class ReceiveRabbitMessageService {

    @Autowired
    private RabbitMqConfig config;

    @Autowired
    private RabbitTemplate template;




    @RabbitListener(queues = "truckCordsQueue")
    public void ReceiveMessage(Truck t) throws IOException, ClassNotFoundException {
        System.out.println(t);
    }


}
