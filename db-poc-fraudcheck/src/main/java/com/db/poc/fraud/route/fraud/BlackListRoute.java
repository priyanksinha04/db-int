package com.db.poc.fraud.route.fraud;

import java.util.Map;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class BlackListRoute extends RouteBuilder {
	 
   private static final Map<String, Set<String>> blackListMap = Map.of(
		    "payerPayeeName", Set.of("Mark Imaginary", "Govind Real", "Shakil Maybe", "Chang Imagine"),
		    "payerPayeeCountry", Set.of("CUB", "IRQ", "IRN", "PRK", "SDN", "SYR"),
		    "payerPayeeBank", Set.of("BANK OF KUNLUN", "KARAMAY CITY COMMERCIAL BANK"),
		    "paymentInstruction", Set.of("Artillery Procurement", "Lethal Chemicals payment")
		);
  
   
	@Override
	public void configure() throws Exception {
		
		from("direct:black-list-route")
	    .routeId("checkValueInListRoute")
	    .process(exchange -> {
	    	String fieldName = exchange.getIn().getHeader("fraudCheckType", String.class);
	    	Set<String> value = exchange.getIn().getHeader("fraudCheckValue", Set.class);
	    	System.out.println("=================");
	    	System.out.println(fieldName);
	    	value.forEach(a->System.out.println(a));
	    	
	    	
	    	String outComeXml = "<FraudResponse>" +
	    							"<code>200</code>" +
	    							"<message>Nothing found, all okay</message>" +
	    					   "</FraudResponse>";
	    	
	    	boolean fraudCheckError = false;
	     
	        if (value.stream().anyMatch(blackListMap.get(fieldName)::contains)) {
	        	outComeXml = "<FraudResponse>" +
                                       "<code>400</code>" +
                                       "<message>Suspicious payment</message>" +
                                    "</FraudResponse>";
	        	fraudCheckError=true;
	        }
	        
	        exchange.setProperty("fraudCheckError", fraudCheckError);
	        System.out.println("SSSSSSSSSSSSSSSSSS");
	        System.out.println(outComeXml);
	        System.out.println(fraudCheckError);
	        exchange.getIn().setBody(outComeXml);
	    });
		
	}

}
