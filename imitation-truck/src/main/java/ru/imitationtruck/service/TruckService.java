package ru.imitationtruck.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.imitationtruck.entity.Truck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TruckService extends Thread {


    @Autowired
    private SendRabbitMessageService sendRabbitMessageService;
    private final List<Truck> truckList = new ArrayList<>();


    {
        truckList.add(new Truck(100001L, 1.234, 5.345));
        truckList.add(new Truck(100002L, 2.234, 6.345));
        truckList.add(new Truck(100003L, 3.234, 7.345));
    }


    public List<Truck> getAll() {
        return truckList;
    }

    public void startMove() {
        if (!this.isAlive())
            this.start();


    }

    public void stopMove() {
        this.interrupt();
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (Truck t : truckList) {
                    t.setX(t.getX() + 0.001);
                    t.setY(t.getY() + 0.002);
                    sendRabbitMessageService.send(t);
                    System.out.println("вывод");
                }

                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            log.info("stop");

        }
    }
}
