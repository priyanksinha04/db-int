package com.db.poc.fraud.route.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.xpath.XPathBuilder;
import org.springframework.stereotype.Component;
@Component
public class FraudServiceRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("activemq:queue:bs.to.fcs?exchangePattern=InOut")
	    .routeId("fraudServiceRoute")
	    .log("FraudServiceRoute>>>Fraud Check Got Request ------------ ${body}")
	    .setProperty("originalXml", body())
	    .setProperty("fraudCheckError", constant(false))

	    // Step 1: Check payer/payee name
	    .choice()
	        .when(simple("${exchangeProperty.fraudCheckError} == false"))
	        .process(exchange -> {
	            String xml = exchange.getProperty("originalXml", String.class);
	            exchange.getIn().setBody(xml);

	            String payerName = XPathBuilder.xpath("/FraudCheck/payerName/text()").evaluate(exchange, String.class);
	            String payeeName = XPathBuilder.xpath("/FraudCheck/payeeName/text()").evaluate(exchange, String.class);

	            Set<String> names = new HashSet<>();
	            if (payerName != null) names.add(payerName);
	            if (payeeName != null) names.add(payeeName);

	            exchange.getIn().setHeader("fraudCheckValue", names);
	            exchange.getIn().setHeader("fraudCheckType", "payerPayeeName");
	        })
	        .to("direct:black-list-route")
	    .end()

	    // Step 2: Check country
	    .choice()
	        .when(simple("${exchangeProperty.fraudCheckError} == false"))
	        .process(exchange -> {
	            String xml = exchange.getProperty("originalXml", String.class);
	            exchange.getIn().setBody(xml);

	            String payerCountry = XPathBuilder.xpath("/FraudCheck/payerCountry/text()").evaluate(exchange, String.class);
	            String payeeCountry = XPathBuilder.xpath("/FraudCheck/payeeCountry/text()").evaluate(exchange, String.class);

	            Set<String> countries = new HashSet<>();
	            if (payerCountry != null) countries.add(payerCountry);
	            if (payeeCountry != null) countries.add(payeeCountry);

	            exchange.getIn().setHeader("fraudCheckValue", countries);
	            exchange.getIn().setHeader("fraudCheckType", "payerPayeeCountry");
	        })
	        .to("direct:black-list-route")
	    .end()

	    // Step 3: Check banks
	    .choice()
	        .when(simple("${exchangeProperty.fraudCheckError} == false"))
	        .process(exchange -> {
	            String xml = exchange.getProperty("originalXml", String.class);
	            exchange.getIn().setBody(xml);

	            String payerBank = XPathBuilder.xpath("/FraudCheck/payerBank/text()").evaluate(exchange, String.class);
	            String payeeBank = XPathBuilder.xpath("/FraudCheck/payeeBank/text()").evaluate(exchange, String.class);

	            Set<String> banks = new HashSet<>();
	            if (payerBank != null) banks.add(payerBank);
	            if (payeeBank != null) banks.add(payeeBank);

	            exchange.getIn().setHeader("fraudCheckValue", banks);
	            exchange.getIn().setHeader("fraudCheckType", "payerPayeeBank");
	        })
	        .to("direct:black-list-route")
	    .end()

	    // Step 4: Check payment instruction
	    .choice()
	        .when(simple("${exchangeProperty.fraudCheckError} == false"))
	        .process(exchange -> {
	            String xml = exchange.getProperty("originalXml", String.class);
	            exchange.getIn().setBody(xml);

	            String instruction = XPathBuilder.xpath("/FraudCheck/paymentInstruction/text()").evaluate(exchange, String.class);

	            Set<String> instructions = new HashSet<>();
	            if (instruction != null) instructions.add(instruction);

	            exchange.getIn().setHeader("fraudCheckValue", instructions);
	            exchange.getIn().setHeader("fraudCheckType", "paymentInstruction");
	        })
	        .to("direct:black-list-route")
	    .end()

	    // Final step - Create and return XML result
	    .process(exchange -> {
	        Boolean isFraud = exchange.getProperty("fraudCheckError", Boolean.class);
	        String resultXml;

	        if (Boolean.TRUE.equals(isFraud)) {
	            resultXml = "<FraudResponse><code>400</code><message>Suspicious payment detected</message></FraudResponse>";
	        } else {
	            resultXml = "<FraudResponse><code>200</code><message>Payment approved</message></FraudResponse>";
	        }

	        exchange.getMessage().setBody(resultXml);
	        exchange.getMessage().setHeader("Content-Type", "application/xml");

	        // Ensure correlation ID is set on response for InOut pattern
	        String correlationId = exchange.getIn().getHeader("JMSCorrelationID", String.class);
	        if (correlationId != null) {
	            exchange.getMessage().setHeader("JMSCorrelationID", correlationId);
	        }
	    })

	    .log("FraudServiceRoute >>>>  Fraud Check - Sending Response ------ ${body}");
	}

}
