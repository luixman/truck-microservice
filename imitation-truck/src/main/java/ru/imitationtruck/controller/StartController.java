package ru.imitationtruck.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.imitationtruck.service.TruckService;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping
@Slf4j


public class StartController {

    private final TruckService truckService;

    @Autowired
    public StartController(TruckService truckService) {
        this.truckService = truckService;
    }


    @GetMapping("/start")
    public ResponseEntity<String> start(@RequestParam(value = "timeout", defaultValue = "5000") Integer timeout) {
        truckService.setTimeout(timeout);
        truckService.startMove();
        log.info("imitation trucks started, timeout:" + timeout);

        return ResponseEntity.ok("OK");
    }
    @GetMapping("/stop")
    public ResponseEntity<String> stop() {
        truckService.stopMove();
        log.info("imitation trucks is stoped");
        return ResponseEntity.ok("stoped");

    }

}
