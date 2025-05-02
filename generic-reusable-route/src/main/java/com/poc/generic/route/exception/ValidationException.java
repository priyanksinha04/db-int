package com.poc.generic.route.exception;

import org.apache.camel.Exchange;

public class ValidationException extends RuntimeException {
	
	private Exchange exchange;
	
	public ValidationException(String msg , Exception ex){
		super(msg,ex);
	}

	public ValidationException(String msg ){
		super(msg);
	}
	
	 public ValidationException(Exchange exchange, String msg) {
	        super(msg);
	        this.exchange = exchange;
	    }

	    public Exchange getExchange() {
	        return exchange;
	    }

	
}
