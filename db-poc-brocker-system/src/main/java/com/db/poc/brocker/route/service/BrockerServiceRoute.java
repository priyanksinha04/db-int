package com.db.poc.brocker.route.service;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.xpath.XPathBuilder;
import org.springframework.stereotype.Component;

@Component
public class BrockerServiceRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("activemq:queue:pps.to.bs")
			.log("Received JSON from PPS: ${body}")
			.log("BrockerServiceRoute>>>Request from calling system : ------ ${body}")
	    .to("direct:convert-json-to-xml")
	    	.log("BrockerServiceRoute -- Converted XML: ${body}")
	    	.setHeader("endpoint").simple("{{fraud.endpoint}}")
	    	.setHeader("httpMethod", constant("POST"))
	    	.setHeader("contentType", constant("application/xml"))
	    .to("activemq:queue:bs.to.fcs?exchangePattern=InOut")
	    .log("BrockerServiceRoute>>>Got Response back from downstream system --------- ${body}")
	    .process(exchange -> {
	        String responseXml = exchange.getIn().getBody(String.class);
	        String bodyCode = XPathBuilder.xpath("/FraudResponse/code/text()")
	                .evaluate(exchange.getContext(), responseXml, String.class);

	        String jsonResponse;

	        if ("400".equals(bodyCode)) {
	            jsonResponse = "{ \"status\": 400, \"message\": \"Fraud detected - Suspicious payment.\" }";
	        } else if ("200".equals(bodyCode) ) {
		    jsonResponse = "{ \"status\": 200, \"message\": \"Nothing found, all okay.\" }";
	            
	        } else {
	             jsonResponse = "{ \"status\": 500, \"message\": \"Internal Server error\" }";
	        }
	        exchange.getMessage().setBody(jsonResponse);
	        exchange.getMessage().setHeader("Content-Type", "application/json");
	    })
	    .log("BrockerServiceRoute>>>BrockerService Response sent to calling service-------  ${body}");		

	}

}
