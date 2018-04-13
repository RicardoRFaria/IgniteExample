package com.example.ignite.IgniteExample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IgniteExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(IgniteExampleApplication.class, args);
	}
}
