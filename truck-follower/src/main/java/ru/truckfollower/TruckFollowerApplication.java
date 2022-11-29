package ru.truckfollower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableWebMvc
public class TruckFollowerApplication {
	public static void main(String[] args) {
		SpringApplication.run(TruckFollowerApplication.class, args);
	}

}
