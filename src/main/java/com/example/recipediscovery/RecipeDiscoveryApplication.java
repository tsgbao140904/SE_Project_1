package com.example.recipediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RecipeDiscoveryApplication {
	public static void main(String[] args) {
		SpringApplication.run(RecipeDiscoveryApplication.class, args);
	}
}
