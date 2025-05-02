package com.db.poc.route.service;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentServiceRestRoute extends RouteBuilder {

	@Autowired
	GetLoggingBean getLoggingBean;

	@Override
	public void configure() throws Exception {

		from("direct:payment-service-route-rest")
	    .bean(getLoggingBean, "getLog")
	    .setHeader("schemaLocation", constant("schema/paymentschema.json"))
	    .to("direct:validate-Json")            // Schema validation
	    .to("direct:validation-route")         // Business validation
	    .bean(getLoggingBean, "getLog")
	    .setHeader("endpoint").simple("{{broker.endpoint}}")
	    .setHeader("httpMethod", constant("POST"))
	    .setHeader("contentType", constant("application/json"))
	    
	    // Call HTTP REST
	    .to("direct:http-rest-call")

	    // Process based on response
	    .choice()
	        .when(header("CamelHttpResponseCode").isEqualTo(200))
	            .log("HTTP 200 received - calling post-REST routes")
	            .to("direct:postPayment")
	            .process(exchange -> {
	            	Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
	                exchange.getMessage().setHeader("responseCode", httpCode);
	                String successJson = String.format(
	                    "{ \"status\": %d, \"message\": \"Payment Success\" }", 200
	                );    /////////////////////////
	                exchange.getMessage().setBody(successJson);
	                exchange.getMessage().setHeader("Content-Type", "application/json");
	            })
	        .otherwise()
	            .log("HTTP error received - generating error response")
	            .process(exchange -> {
	            	Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
	                exchange.getMessage().setHeader("responseCode", httpCode);
	            	
	                int code = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
	                String errorJson = String.format(
	                    "{ \"status\": %d, \"message\": \"Payment Failed due to Fraud check fail\" }", code
	                );    /////////////////////////
	                exchange.getMessage().setBody(errorJson);
	                exchange.getMessage().setHeader("Content-Type", "application/json");
	            })
	    .end()
	    .log("Final Body after Broker Check === ${body}");

		// TO DO :: Error handling and payment processing and success message
		// in case of success or error message
	}
}

@Component
class GetLoggingBean {

	Logger log = LoggerFactory.getLogger(GetLoggingBean.class);

	public void getLog(String message) {
		log.info("This PaymentServiceRoute logger :: {}", message);
		// return "changed";
	}
}
