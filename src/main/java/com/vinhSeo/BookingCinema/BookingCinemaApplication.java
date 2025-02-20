package com.vinhSeo.BookingCinema;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookingCinemaApplication {

	@Value("${jwt.secretKey}")
	private String secretKey;

	public static void main(String[] args) {
		SpringApplication.run(BookingCinemaApplication.class, args);
	}

	@PostConstruct
	public void init() {
		System.out.println(secretKey);
	}
}
