package ru.truckfollower.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.truckfollower.entity.TelegramConnection;
import ru.truckfollower.repo.TelegramConnectionRepo;
import ru.truckfollower.service.AlarmService;

import javax.annotation.PostConstruct;

@Controller
@Slf4j
public class StartController {


    @Autowired
    TelegramConnectionRepo telegramConnectionRepo;

    @Autowired
    AlarmService alarmService;


    @PostConstruct
    public void init() throws Exception {

     /*  List<Company> list = companyRepo.findAll();
        System.out.println(list);

        List<Truck> list1 =truckRepo.findAll();
        System.out.println(list1);*/

       /* List<ForbiddenZone> list = forbiddenZoneRepo.findAll();
        String s = list.get(2).getPolygon();
        org.postgis.PGgeometry pGgeometry = new PGgeometry();
        pGgeometry.setValue(s);
        Geometry g =pGgeometry.getGeometry();*/

        // Alarm alarm =alarmRepo.findAll().get(0);


      /*TelegramConnection telegramConnection= telegramConnectionRepo.getByChatId(-1001856410390L).get();

        System.out.println();

       // telegramConnectionRepo.deleteAllByChatId(telegramConnection.getChatId());
        telegramConnectionRepo.delete(telegramConnection);
        System.out.println();*/


    }

}
