package com.poc.generic.route.http;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class RestRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		from("direct:http-rest-call")
	    .log("Request body: ${body}")
	    .log("PaymentValidationRoute 2 --0000000000000-- ${header.httpMethod}")
	    .log("PaymentValidationRoute 2 --0000000000000-- ${header.contentType}")
	    .setHeader(Exchange.HTTP_METHOD, simple("${header.httpMethod}"))
	    .choice()
          .when(simple("${header.httpMethod} == 'POST'"))
            .setHeader("Content-Type", simple("${header.contentType}"))
          .when(simple("${header.httpMethod} == 'GET'"))
            .setBody(constant(null))  // clear body for GET  
          .end()
	    .toD("${header.endpoint}?throwExceptionOnFailure=false")
	    .log("Response body: ${body}");
	}

}
