package com.peciatech.ibold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IboldApplication {

	public static void main(String[] args) {
		SpringApplication.run(IboldApplication.class, args);
	}

}
