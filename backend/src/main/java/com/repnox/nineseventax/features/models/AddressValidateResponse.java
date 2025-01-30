package com.repnox.nineseventax.features.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressValidateResponse {
	
	private String status;
	@JsonProperty(value = "matched_address")
	private Object matched_address;
	@JsonProperty(value = "original_address")
	private OrignalAddressValidationResponse original_address;
	private ArrayList messages;
	
	
	public ArrayList getMessages() {
		return messages;
	}
	public void setMessages(ArrayList messages) {
		this.messages = messages;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Object getMatched_address() {
		return matched_address;
	}

	public void setMatched_address(Object matched_address) {
		this.matched_address = matched_address;
	}

	public OrignalAddressValidationResponse getOriginal_address() {
		return original_address;
	}

	public void setOriginal_address(OrignalAddressValidationResponse original_address) {
		this.original_address = original_address;
	}
}
