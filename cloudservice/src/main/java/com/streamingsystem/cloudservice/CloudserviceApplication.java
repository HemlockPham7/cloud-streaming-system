package com.streamingsystem.cloudservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CloudserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudserviceApplication.class, args);
	}

}
