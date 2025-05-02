package com.db.poc.fraud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.db.poc.fraud", "com.poc.generic" })
public class DbPocFraudcheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbPocFraudcheckApplication.class, args);
	}

}
