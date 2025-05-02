package com.db.poc.route.service;

import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poc.generic.route.exception.ValidationException;

@Component
public class PaymentServiceRoute extends RouteBuilder {

	@Autowired
	GetLoggingBean getLoggingBean;

	@Override
	public void configure() throws Exception {
		
		
		onException(ValidationException.class)
	    .handled(false) // Let it propagate to the caller
	    .log("Validation failed: ${exception.message}");

		from("direct:payment-service-route")
	    .log("PaymentServiceRoute>>>> Recieved Request ---- ${body}")
	    .setHeader("schemaLocation", constant("schema/paymentschema.json"))
	    .to("direct:validate-Json")            // Schema validation
	    .to("direct:validation-route")         // Business validation
	    .log("PaymentServiceRoute>>>> After validation ---- ${body}")
	    .to("activemq:queue:pps.to.bs?exchangePattern=InOut")

	    // Parse the JSON response 
	    .unmarshal().json(JsonLibrary.Jackson) 

	    // Log the parsed body   
	    .log("PaymentServiceRoute>>>>Parsed Response Body: ${body}")
	    .choice()
	        .when(simple("${body[status]} == 200"))
	            .log("PaymentServiceRoute>>>Status 200 received from broker - calling post-REST routes")
	            .to("direct:postPayment")
	            .process(exchange -> {
	                String successJson = "{ \"status\": 200, \"message\": \"Payment Success\" }";
	                exchange.getMessage().setBody(successJson);
	                exchange.getMessage().setHeader("Content-Type", "application/json");
	                exchange.getMessage().setHeader("responseCode", 200);
	            })
	        .otherwise()
	            .log("PaymentServiceRoute>>>>Error status received - generating error response")
	            .process(exchange -> {
	                // Extract status manually from the Map
	                Map<String, Object> responseMap = exchange.getIn().getBody(Map.class);
	                Integer code = (Integer) responseMap.get("status");
	                if (code == null) code = 500;
	                String errorJson = String.format(
	                    "{ \"status\": %d, \"message\": \"Payment Failed due to Fraud check fail\" }", code
	                );
	                exchange.getMessage().setBody(errorJson);
	                exchange.getMessage().setHeader("Content-Type", "application/json");
	                exchange.getMessage().setHeader("responseCode", code);
	            })
	    .end()
	    .log("PaymentServiceRoute>>>>Final Response === ${body}");







		
	}
}



