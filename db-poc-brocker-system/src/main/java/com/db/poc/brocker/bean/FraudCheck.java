package com.db.poc.brocker.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "Fraud")
@Getter
@Setter
@ToString
public class FraudCheck {

	@XmlElement(name = "transectionID", required = true)
	private String transectionID;

	@XmlElement(name = "payerName", required = true)
	private String payerName;

	@XmlElement(name = "payerBank", required = true)
	private String payerBank;

	@XmlElement(name = "payerCountry", required = true)
	private String payerCountry;

	@XmlElement(name = "payeeName", required = true)
	private String payeeName;

	@XmlElement(name = "payeeBank", required = true)
	private String payeeBank;

	@XmlElement(name = "payeeCountry", required = true)
	private String payeeCountry;
	
	@XmlElement(name = "paymentInstruction", required = true)
	private String paymentInstruction;
	
	
}
