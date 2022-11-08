package ru.truckfollower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TruckFollowerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TruckFollowerApplication.class, args);
	}

}
