package com.db.poc.brocker.route.service;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.xpath.XPathBuilder;
import org.springframework.stereotype.Component;

@Component
public class BrockerServiceRestRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

	from("direct:brocker-service-rest-route")
		.log("BrockerServiceRestRoute  recived ------- body: ${body}")
    .to("direct:convert-json-to-xml")
    	.log("BrockerServiceRestRoute -- Converted XML: ${body}")
    	.setHeader("endpoint").simple("{{fraud.endpoint}}")
    	.setHeader("httpMethod", constant("POST"))
    	.setHeader("contentType", constant("application/xml"))
    .to("activemq:queue:bs.to.fcs?exchangePattern=InOut")
    .log("BrockerServiceRestRoute>>>Got Response back from downstream system --------- ${body}")
    .process(exchange -> {
        String responseXml = exchange.getIn().getBody(String.class);
        String bodyCode = XPathBuilder.xpath("/FraudResponse/code/text()")
                .evaluate(exchange.getContext(), responseXml, String.class);

        String jsonResponse;
        

        if ("400".equals(bodyCode)) {
        	exchange.getMessage().setHeader("fraudResponseCode", 400);
            jsonResponse = "{ \"status\": 400, \"message\": \"Fraud detected - Suspicious payment.\" }";
        } else if ("200".equals(bodyCode) ) {
        	exchange.getMessage().setHeader("fraudResponseCode", 200);
	    jsonResponse = "{ \"status\": 200, \"message\": \"Nothing found, all okay.\" }";
            
        } else {
        	exchange.getMessage().setHeader("fraudResponseCode", 500);
             jsonResponse = "{ \"status\": 500, \"message\": \"Internal Server error\" }";
        }
       
        
        System.out.println("____________9999999_________________");
        //System.out.println(httpCode);
        exchange.getMessage().setBody(jsonResponse);
        exchange.getMessage().setHeader("Content-Type", "application/json");
    })
    .log("BrockerServiceRestRoute>>>BrockerService Response sent to calling service-------  ${body}");		

		
		

	}

}
