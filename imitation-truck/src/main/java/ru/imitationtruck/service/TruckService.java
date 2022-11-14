package ru.imitationtruck.service;


import lombok.extern.slf4j.Slf4j;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.imitationtruck.entity.Truck;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TruckService extends Thread {


    @Autowired
    private SendRabbitMessageService sendRabbitMessageService;
    private final List<Truck> truckList = new ArrayList<>();

    private final List<Point> pointList = new ArrayList<>();

    @PostConstruct
    public void initialize(){
        pointList.add(new Point(54.87811,37.2415 ));
        pointList.add(new Point(54.88035  ,37.23799  ));
        pointList.add(new Point(54.88254  ,37.23647 ));
        pointList.add(new Point(54.8857 ,37.23742  ));

        pointList.add(new Point(54.88807  ,37.23771  ));
        pointList.add(new Point(54.88899  ,37.23698 ));
        pointList.add(new Point(54.88989 ,37.2362  ));
        pointList.add(new Point(54.89147 ,37.23512 ));


        //запретная зона
      /*  pointList.add(new Point(54.89225 ,37.23313 ));
        pointList.add(new Point(54.89152 ,37.23005 ));*/


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
        pointList.add(new Point(54.86532,37.20396 ));

    }


    {
        truckList.add(new Truck(100001L, 0, 0));
        truckList.add(new Truck(100002L, 0, 0));
        truckList.add(new Truck(100003L, 0, 0));

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

        try{

            while(true){

                for (Point point: pointList) {
                   /* Thread.sleep(500);*/
                    for (Truck truck : truckList) {
                        truck.setX(point.x);
                        truck.setY(point.y);
                        truck.setInstant(Instant.MIN);
                        sendRabbitMessageService.send(truck);
                    }
                    Thread.sleep(1000);
                }
            }

        }catch (Exception e){
            log.info("stop");
        }
    }
}
