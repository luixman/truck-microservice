package ru.imitationtruck.service;


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
public class TruckService extends Thread {


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
        System.out.println();

       /* pointList.add(new Point(54.87811,37.2415 ));
        pointList.add(new Point(54.88035  ,37.23799  ));
        pointList.add(new Point(54.88254  ,37.23647 ));
        pointList.add(new Point(54.8857 ,37.23742  ));

        pointList.add(new Point(54.88807  ,37.23771  ));
        pointList.add(new Point(54.88899  ,37.23698 ));
        pointList.add(new Point(54.88989 ,37.2362  ));
        pointList.add(new Point(54.89147 ,37.23512 ));


        //запретная зона
      *//*  pointList.add(new Point(54.89225 ,37.23313 ));
        pointList.add(new Point(54.89152 ,37.23005 ));*//*


        pointList.add(new Point(54.89009 ,37.22704 ));
        pointList.add(new Point(54.88628 ,37.21887 ));
        pointList.add(new Point(54.88341 ,37.21166 ));
        pointList.add(new Point(54.88109 ,37.21089 ));

        pointList.add(new Point(54.87664 ,37.2093 ));
        pointList.add(new Point(54.87313 ,37.2081 ));
        pointList.add(new Point(54.87282 ,37.20396 ));
        pointList.add(new Point(54.87311 ,37.19822 ));

        //запретная зона
        pointList.add(new Point(54.87332,37.19674 ));
        pointList.add(new Point(54.87385 ,37.1925 ));
        pointList.add(new Point(54.86959 ,37.17772 ));
        pointList.add(new Point(54.86833 ,37.18346 ));

        pointList.add(new Point(54.8662 ,37.19432 ));
        pointList.add(new Point(54.8661 ,37.19787 ));
        pointList.add(new Point(54.86573 ,37.20098 ));
        pointList.add(new Point(54.86532,37.20396 ));*/

    }


    {
      /*  truckList.add(new Truck(100001L, 0, 0));
        truckList.add(new Truck(100002L, 0, 0));
        truckList.add(new Truck(100003L, 0, 0));*/


    }


    public void startMove() {
        for (Map.Entry<Truck, List<Point>> entry : truckListMap.entrySet()) {
            Runnable task = () -> {
                Truck t = entry.getKey();
                for (Point p : entry.getValue()) {
                    t.setX(p.y);
                    t.setY(p.x);
                    t.setInstant(Instant.now());
                    sendRabbitMessageService.send(t);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            executorService.submit(task);

        }


    }

    public void stopMove() {

    }


}
