package com.db.poc.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PostingRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		// Dummy Route for posting 
		
		from("direct:postPayment")
	    .log("Request body: ${body}")
	    .log("POSTION DONE")
	    .log("Response body: ${body}");
		
	}

}
