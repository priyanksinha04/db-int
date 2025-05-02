package com.poc.generic.route.businessvalidation;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.generic.route.exception.ValidationException;

@Component
public class ISOCountryCheckRoute extends RouteBuilder{
	
	 private static final Set<String> isoAlpha3CountryCodes = Arrays.stream(Locale.getISOCountries())
	            .map(locale -> new Locale("", locale).getISO3Country())
	            .collect(Collectors.toSet());

	@Override
	public void configure() throws Exception {
		
		
		
		 onException(ValidationException.class)
         .handled(false)
         .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
         .setBody(simple("Exception: ${exception.message}"));
		
		
		from("direct:iso-country-check-route")
		.process(new Processor() {
			private final ObjectMapper objectMapper = new ObjectMapper();
            @Override
            public void process(Exchange exchange) throws Exception {            
            	String fieldValue = exchange.getIn().getHeader("countryCode", String.class);
            	String fieldName = exchange.getIn().getHeader("countryCodeField", String.class);
            	System.out.println("--ISO COUNTRY CODE---SSSSSSSS----");
            	System.out.println(exchange.getMessage().getBody()); 
            	System.out.println(fieldValue);
            	System.out.println(fieldName);
            	
            	if(!isoAlpha3CountryCodes.contains(fieldValue)) {
            		throw new ValidationException("ISO Country Code IS invalid :: fieldName="+fieldName+" :: fieldValue="+fieldValue);
            	}
            }
        })
		.log("Validation completed (ISOCountryCheckRoute): ${body}");
		
	}
	
}
