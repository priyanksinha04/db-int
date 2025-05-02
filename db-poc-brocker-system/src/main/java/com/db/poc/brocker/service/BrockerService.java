package com.db.poc.brocker.service;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BrockerService {

	@Autowired
	private ProducerTemplate producerTemplate;

	public ResponseEntity<String> processBrokerFraudCheck(String fraudCheckRequest) {
		System.out.println("#######2222222222###000000000000000000000000000000000000000##");
		System.out.println(fraudCheckRequest);
		//String response = producerTemplate.requestBody("direct:fraud-service-route", fraudCheckRequest, String.class);
		
		Exchange exchange = producerTemplate.request("direct:brocker-service-rest-route", ex -> {
		    ex.getIn().setBody(fraudCheckRequest);
		});

		// Get the response body
		String responseBody = exchange.getMessage().getBody(String.class);

		// Get the fraud check status from exchange property
		Integer fraudResponseCode = exchange.getMessage().getHeader("fraudResponseCode", Integer.class);
		
		
		System.out.println("############");
		System.out.println(responseBody);
		System.out.println(fraudResponseCode);
		
		return buildResponse(responseBody, fraudResponseCode);
		
	}
	
	private ResponseEntity<String> buildResponse(String body , Integer fraudResponseCode){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(body,headers,fraudResponseCode);
	}


}
