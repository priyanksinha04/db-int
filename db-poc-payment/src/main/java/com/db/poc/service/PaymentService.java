package com.db.poc.service;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

	@Autowired
	private ProducerTemplate producerTemplate;

	public ResponseEntity<String> processPayment(String paymentRequest) {
		
		Exchange exchange = producerTemplate.request("direct:payment-service-route", ex -> {
		    ex.getIn().setBody(paymentRequest);
		});
		
		Integer responseCode = exchange.getMessage().getHeader("responseCode", Integer.class);
		String responseBody = exchange.getMessage().getBody(String.class);
		
		return buildResponse(responseBody, responseCode);
	}
	
	
	private ResponseEntity<String> buildResponse(String body , Integer fraudResponseCode){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(body,headers,fraudResponseCode);
	}
	
	public ResponseEntity<String> processPaymentRest(String paymentRequest) {
		
		Exchange exchange = producerTemplate.request("direct:payment-service-route-rest", ex -> {
		    ex.getIn().setBody(paymentRequest);
		});
		
		Integer responseCode = exchange.getMessage().getHeader("responseCode", Integer.class);
		String responseBody = exchange.getMessage().getBody(String.class);
		
		return buildResponse(responseBody, responseCode);
	}

}
