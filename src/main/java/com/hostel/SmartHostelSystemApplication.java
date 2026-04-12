package com.hostel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartHostelSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(SmartHostelSystemApplication.class, args);

//		 System.out.println(System.getenv("MAIL_USERNAME"));
//		 System.out.println(System.getenv("MAIL_PASSWORD"));

	}

}
