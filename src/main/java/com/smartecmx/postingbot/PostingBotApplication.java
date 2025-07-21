package com.smartecmx.postingbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PostingBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(PostingBotApplication.class, args);
	}
}
