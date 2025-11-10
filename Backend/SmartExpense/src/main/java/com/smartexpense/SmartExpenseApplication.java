package com.smartexpense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartExpenseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartExpenseApplication.class, args);
	}

}
