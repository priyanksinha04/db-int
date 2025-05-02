package com.db.poc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.poc.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rtp")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	// Solution 1
	@PostMapping(value = "/payment", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> processInstantPayment(@RequestBody String paymentRequest) {
		return paymentService.processPayment(paymentRequest);
	}
	
	
	// Solution 2
	@PostMapping(value = "/payment2", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> processInstantPaymentRest(@RequestBody String paymentRequest) {
		return paymentService.processPaymentRest(paymentRequest);
	}
}
