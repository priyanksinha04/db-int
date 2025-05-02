package com.db.poc.brocker.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.db.poc.brocker.bean.FraudCheck;
import com.jayway.jsonpath.JsonPath;

@Component
public class JsonToXmlRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		// Create a JAXB data format and set context path to the package of FraudCheck
//        JaxbDataFormat jaxb = new JaxbDataFormat();
//        jaxb.setContextPath("com.db.poc.brocker.bean");  // << this is key

		from("direct:convert-json-to-xml").routeId("json-to-xml-route").log("Input JSON: ${body}").process(exchange -> {
			String json = exchange.getIn().getBody(String.class);

			FraudCheck fraudCheck = new FraudCheck();
			fraudCheck.setPayerCountry(JsonPath.read(json, "$.payerCountryCode"));
			fraudCheck.setPayeeBank(JsonPath.read(json, "$.payeeBank"));
			fraudCheck.setPayeeName(JsonPath.read(json, "$.payeeName"));
			fraudCheck.setPayeeCountry(JsonPath.read(json, "$.payeeCountryCode"));
			fraudCheck.setPaymentInstruction(JsonPath.read(json, "$.paymentInstruction"));
			fraudCheck.setPayerBank(JsonPath.read(json, "$.payerBank"));
			fraudCheck.setPayerName(JsonPath.read(json, "$.payerName"));
			fraudCheck.setTransectionID(JsonPath.read(json, "$.transactionID"));

			com.fasterxml.jackson.dataformat.xml.XmlMapper xmlMapper = new com.fasterxml.jackson.dataformat.xml.XmlMapper();
			String xml = xmlMapper.writeValueAsString(fraudCheck);

			exchange.getIn().setBody(xml, String.class);
		})
				// .marshal().jacksonXml(true) // Use properly configured JaxbDataFormat
				.log("Generated XML: ${body}");
	}
}
