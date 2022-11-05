package ru.truckfollower.controller;

import lombok.extern.slf4j.Slf4j;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgresql.geometric.PGpolygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import ru.truckfollower.entity.Company;
import ru.truckfollower.entity.ForbiddenZone;
import ru.truckfollower.entity.Truck;
import ru.truckfollower.repo.CompanyRepo;
import ru.truckfollower.repo.ForbiddenZoneRepo;
import ru.truckfollower.repo.TruckRepo;

import ru.truckfollower.service.ReceiveRabbitMessageService;
import ru.truckfollower.service.polygon.Polygon;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

@Controller
@Slf4j
public class StartController {
/*
    @Autowired
    ReceiveRabbitMessageService messageService;

    @Autowired
    CompanyRepo companyRepo;

    @Autowired
    TruckRepo truckRepo;
*/

    @Autowired
    ForbiddenZoneRepo forbiddenZoneRepo;


    @PostConstruct
    public void init() throws Exception{

     /*  List<Company> list = companyRepo.findAll();
        System.out.println(list);

        List<Truck> list1 =truckRepo.findAll();
        System.out.println(list1);*/

        List<ForbiddenZone> list = forbiddenZoneRepo.findAll();
        System.out.println(list);
        String s = list.get(2).getPolygon();
        org.postgis.PGgeometry pGgeometry = new PGgeometry();
        pGgeometry.setValue(s);
        Geometry g =pGgeometry.getGeometry();

        for (int i = 0; i < g.numPoints(); i++) {
            System.out.println(g.getPoint(i));
        }









    }

}
