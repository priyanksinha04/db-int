package com.poc.generic.route.schemavalidation;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.poc.generic.route.exception.ValidationException;


@Component
public class JsonSchemavalidation extends RouteBuilder{

	@Override
	public void configure() throws Exception {
				
		from("direct:validate-Json")
	    .routeId("invalidRoute")
	    .log("Log ===========${header.schemaLocation}")
	    .doTry()
	        .toD("json-validator:classpath:${header.schemaLocation}")
	    .doCatch(Exception.class)
	        .log("JSON Schema validation failed: ${exception.message}")
	        .process(exchange -> {
	            throw new ValidationException(exchange,"Invalid JSON payload: " + exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage());
	        })
	    .end();
	   

	}

}
