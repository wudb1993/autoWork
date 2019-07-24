package com.autowork.weekly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeeklyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeeklyApplication.class, args);
	}

}
