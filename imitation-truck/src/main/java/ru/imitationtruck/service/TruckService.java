package ru.imitationtruck.service;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.imitationtruck.entity.Truck;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class TruckService {


    @Autowired
    private SendRabbitMessageService sendRabbitMessageService;


    Map<Truck, List<Point>> truckListMap = new LinkedHashMap<>();

    ExecutorService executorService = Executors.newFixedThreadPool(3);

    @PostConstruct
    public void initialize() throws Exception {


        String[] files = {"truck1.txt", "truck2.txt", "truck3.txt"};

        List<List<Point>> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(".\\imitation-truck\\"+files[i]));
            List<Point> l = new ArrayList();
            list.add(l);
            while (reader.ready()) {
                String[] s = reader.readLine().split(" ");
                l.add(new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1])));

            }
        }

        truckListMap.put(new Truck(100001L, 0, 0), list.get(0));
        truckListMap.put(new Truck(100002L, 0, 0), list.get(1));
        truckListMap.put(new Truck(100003L, 0, 0), list.get(2));

        startMove();
    }




    @SneakyThrows
    public void startMove() {

        for (Map.Entry<Truck, List<Point>> entry : truckListMap.entrySet()) {
            Runnable task = () -> {

                Truck t = entry.getKey();
                while(true) {
                    for (Point p : entry.getValue()) {
                        t.setX(p.y);
                        t.setY(p.x);
                        t.setInstant(Instant.now());
                        sendRabbitMessageService.send(t);
                        try {
                            Thread.sleep(5);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    log.info(entry.getKey().getUid()+" started again");
                }

            };
            executorService.submit(task);

        }


    }

    public void stopMove() {

    }





}
