package ru.truckfollower.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import ru.truckfollower.entity.Company;
import ru.truckfollower.entity.Truck;
import ru.truckfollower.repo.CompanyRepo;
import ru.truckfollower.repo.TruckRepo;
import ru.truckfollower.service.ReceiveRabbitMessageService;

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


    @PostConstruct
    public void init() throws Exception{

     /*  List<Company> list = companyRepo.findAll();
        System.out.println(list);

        List<Truck> list1 =truckRepo.findAll();
        System.out.println(list1);*/

    }

}
