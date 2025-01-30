package com.repnox.nineseventax.features.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShippingTrackData{
	
	@JsonProperty(value = "label_url")
	private String labelUrl;
	@JsonProperty(value = "tracking_number")
	private String trackingNumber;
	@JsonProperty(value = "status_code")
	private String statusCode;
	@JsonProperty(value = "status_description")
	private String statusDescription;
	@JsonProperty(value = "carrier_status_code")
	private String carrierStatusCode;
	@JsonProperty(value = "carrier_detail_code")
	private String carrierDetailCode;
	@JsonProperty(value = "carrier_status_description")
	private String carrierStatusDescription;
	@JsonProperty(value = "ship_date")
	private String shipDate;
	@JsonProperty(value = "estimated_delivery_date")
	private String estimatedDeliveryDate;
	@JsonProperty(value = "actual_delivery_date")
	private String actualDeliveryDate;
	@JsonProperty(value = "exception_description")
	private String exceptionDescription;
	@JsonProperty(value = "events")
	private ArrayList<Object> events = new ArrayList<Object>();
	
	public String getLabelUrl() {
		return labelUrl;
	}
	public void setLabelUrl(String labelUrl) {
		this.labelUrl = labelUrl;
	}
	public String getTrackingNumber() {
		return trackingNumber;
	}
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	public String getCarrierStatusCode() {
		return carrierStatusCode;
	}
	public void setCarrierStatusCode(String carrierStatusCode) {
		this.carrierStatusCode = carrierStatusCode;
	}
	public String getCarrierDetailCode() {
		return carrierDetailCode;
	}
	public void setCarrierDetailCode(String carrierDetailCode) {
		this.carrierDetailCode = carrierDetailCode;
	}
	public String getCarrierStatusDescription() {
		return carrierStatusDescription;
	}
	public void setCarrierStatusDescription(String carrierStatusDescription) {
		this.carrierStatusDescription = carrierStatusDescription;
	}
	public String getShipDate() {
		return shipDate;
	}
	public void setShipDate(String shipDate) {
		this.shipDate = shipDate;
	}
	public String getEstimatedDeliveryDate() {
		return estimatedDeliveryDate;
	}
	public void setEstimatedDeliveryDate(String estimatedDeliveryDate) {
		this.estimatedDeliveryDate = estimatedDeliveryDate;
	}
	public String getActualDeliveryDate() {
		return actualDeliveryDate;
	}
	public void setActualDeliveryDate(String actualDeliveryDate) {
		this.actualDeliveryDate = actualDeliveryDate;
	}
	public String getExceptionDescription() {
		return exceptionDescription;
	}
	public void setExceptionDescription(String exceptionDescription) {
		this.exceptionDescription = exceptionDescription;
	}
	public ArrayList<Object> getEvents() {
		return events;
	}
	public void setEvents(ArrayList<Object> events) {
		this.events = events;
	}
	
}