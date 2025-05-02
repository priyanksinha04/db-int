package com.db.poc.brocker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.poc.brocker.service.BrockerService;

@RestController
@RequestMapping("/api/rtp")
public class BrokerController {

	@Autowired
	private BrockerService brokerService;

	@PostMapping(value = "/paymentBroker", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> brokerFraudCheck(@RequestBody String paymentRequest, @RequestHeader Map<String, String> headers) {

		headers.forEach((key, value) -> System.out.println(key + " = " + value));

		return brokerService.processBrokerFraudCheck(paymentRequest);

	}

}
