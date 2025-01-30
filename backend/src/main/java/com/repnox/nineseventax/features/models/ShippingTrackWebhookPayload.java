package com.repnox.nineseventax.features.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShippingTrackWebhookPayload {
	
	@JsonProperty(value = "resource_url")
	private String resourceUrl;
	@JsonProperty(value = "resource_type")
	private String resourceType;
	@JsonProperty(value = "data")
	private ShippingTrackData shippingTrackData;
	public String getResourceUrl() {
		return resourceUrl;
	}
	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public ShippingTrackData getShippingTrackData() {
		return shippingTrackData;
	}
	public void setShippingTrackData(ShippingTrackData shippingTrackData) {
		this.shippingTrackData = shippingTrackData;
	}

	
}