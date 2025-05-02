package com.poc.generic.route.businessvalidation;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.generic.route.exception.ValidationException;

@Component
public class ISOCurrencyCodeCheck extends RouteBuilder{
	
	 private static final Set<String> validCodes = Currency.getAvailableCurrencies().stream()
	            .map(Currency::getCurrencyCode)
	            .collect(Collectors.toSet());

	@Override
	public void configure() throws Exception {
		
		System.out.print("------ISO Currency Check --------");
		
		
		 onException(ValidationException.class)
         .handled(false)
         .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
         .setBody(simple("Exception: ${exception.message}"));
		
		
		from("direct:iso-currency-check-route")
		.log("ISOCurrencyCodeCheck------${body}")
		.process(new Processor() {
			private final ObjectMapper objectMapper = new ObjectMapper();
            @Override
            public void process(Exchange exchange) throws Exception {            
            	String fieldValue = exchange.getIn().getHeader("curencyCode", String.class);
            	String fieldName = exchange.getIn().getHeader("curencyCodeField", String.class);
            	System.out.println("--ISO Curency CODE---SSSSSSSS----");
            	System.out.println(exchange.getMessage().getBody()); 
            	System.out.println(fieldValue);
            	System.out.println(fieldName);
            	
            	if(!validCodes.contains(fieldValue)) {
            		throw new ValidationException("ISO Curency Code IS invalid :: fieldName="+fieldName+" :: fieldValue="+fieldValue);
            	}
            }
        })
		.log("Validation completed (ISOCurrencyCodeCheck): ${body}");
		
	}

}
