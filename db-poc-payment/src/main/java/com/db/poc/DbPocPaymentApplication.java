package com.db.poc;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import jakarta.jms.ConnectionFactory;

@SpringBootApplication
@ComponentScan(basePackages = { "com.db.poc", "com.poc.generic" })
public class DbPocPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbPocPaymentApplication.class, args);
	}
	
	
	@Bean
	public JmsComponent activemq(CamelContext context) {
	        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
	        return JmsComponent.jmsComponentAutoAcknowledge(connectionFactory);
	}

}
