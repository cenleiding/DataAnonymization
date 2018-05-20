package com.CLD.dataAnonymization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class DataAnonymizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataAnonymizationApplication.class, args);
	}
}
