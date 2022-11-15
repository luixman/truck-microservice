package ru.imitationtruck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.imitationtruck.service.TruckService;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/order")
public class ProducerController {




    @Autowired
    private TruckService truckService;




    @GetMapping("/start")
    public ResponseEntity<String> start(){
        truckService.startMove();

        return ResponseEntity.ok("OK");

    }

    @GetMapping("stop")
    public ResponseEntity<String> stop(){

        truckService.stopMove();
        return ResponseEntity.ok("stoped");

    }

    @PostConstruct
    public void initialize(){
        start();
    }
}
