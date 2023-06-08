package ru.imitationtruck.service;


import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.imitationtruck.entity.Transport;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class TruckService {

    private final SendRabbitMessageService sendRabbitMessageService;
    private final Map<Transport, List<Point>> truckListMap = new LinkedHashMap<>();
    private ExecutorService executorService;
    private boolean isStarted = false;

    @Getter
    @Setter
    private int timeout;

    @Autowired
    public TruckService(SendRabbitMessageService sendRabbitMessageService) {
        this.sendRabbitMessageService = sendRabbitMessageService;
    }

    @PostConstruct
    public void initialize() throws Exception {
        String[] files = {"truck1.txt", "truck2.txt", "truck3.txt"};

        List<List<Point>> list = new ArrayList<>();
        for (String file : files) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            List<Point> l = new ArrayList();
            list.add(l);

            while (reader.ready()) {
                String[] s = reader.readLine().split(" ");
                l.add(new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1])));

            }
        }

        truckListMap.put(new Transport(100001L, 0, 0), list.get(0));
        truckListMap.put(new Transport(100002L, 0, 0), list.get(1));
        truckListMap.put(new Transport(100003L, 0, 0), list.get(2));
    }

    @SneakyThrows
    public void startMove() {
        if (!isStarted) {
            isStarted = true;
            executorService = Executors.newFixedThreadPool(truckListMap.size());

            for (Map.Entry<Transport, List<Point>> entry : truckListMap.entrySet()) {
                Runnable task = () -> {
                    Transport t = entry.getKey();
                    while (true) {
                        for (Point p : entry.getValue()) {
                            t.setX(p.y);
                            t.setY(p.x);
                            t.setInstant(Instant.now());
                            sendRabbitMessageService.send(t);
                            try {
                                Thread.sleep(timeout);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        log.info(entry.getKey().getUid() + " started again");
                    }
                };
                executorService.submit(task);
            }
        }
    }

    public void stopMove() {
        executorService.shutdownNow();
        isStarted = false;
    }

}
