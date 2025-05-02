package com.db.poc.brocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.db.poc.brocker", "com.poc.generic" })
public class DbPocBrockerSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbPocBrockerSystemApplication.class, args);
	}

}
