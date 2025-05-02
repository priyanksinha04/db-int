package com.db.poc.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component

public class PaymentValidationRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		System.out.print("------PaymentValidationRoute Check --------");

		from("direct:validation-route").log("PaymentValidationRoute---${body}")
				.setHeader("countryCode", jsonpath("$.payerCountryCode"))
				.setHeader("countryCodeField", constant("payerCountryCode"))
		.to("direct:iso-country-check-route")
				.setHeader("countryCode", jsonpath("$.payeeCountryCode"))
				.setHeader("countryCodeField", constant("payeeCountryCode"))
		.to("direct:iso-country-check-route")
				.setHeader("curencyCode", jsonpath("$.currency"))
				.setHeader("curencyCodeField", constant("currency"))
		.to("direct:iso-currency-check-route");

	}

}
