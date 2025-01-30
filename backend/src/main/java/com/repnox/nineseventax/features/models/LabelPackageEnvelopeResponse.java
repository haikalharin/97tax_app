package com.repnox.nineseventax.features.models;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LabelPackageEnvelopeResponse{

		@JsonProperty(value = "label_id")
		private String label_id;
		
		@JsonProperty(value = "status")
		private String status;
		
		@JsonProperty(value = "shipment_id")
		private String shipment_id;
		
		@JsonProperty(value = "ship_date")
		private String ship_date;
		
		@JsonProperty(value = "created_at")
		private String created_at;
		
		@JsonProperty(value = "shipment_cost")
		private Object shipment_cost;
		
		@JsonProperty(value = "insurance_cost")
		private Object insurance_cost;
		
		@JsonProperty(value = "tracking_number")
		private String tracking_number;
		
		@JsonProperty(value = "is_international")
		private String is_international;
		
		@JsonProperty(value = "carrier_id")
		private String carrier_id;
		
		@JsonProperty(value = "service_code")
		private String service_code;
		
		@JsonProperty(value = "package_code")
		private String package_code;
	
		@JsonProperty(value = "label_format")
		private String label_format;
		
		@JsonProperty(value = "display_scheme")
		private String display_scheme;
		
		@JsonProperty(value = "label_layout")
		private String label_layout;
		
		@JsonProperty(value = "tracking_status")
		private String tracking_status;
		
		@JsonProperty("label_download")
		private LabelDownload label_download;
		
		@JsonProperty("packages")
		private ArrayList<Object> packages;
		
		@JsonProperty(value = "charge_event")
		private String chargeEvent;

	public String getLabel_id() {
		return label_id;
	}

	public void setLabel_id(String label_id) {
		this.label_id = label_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getShipment_id() {
		return shipment_id;
	}

	public void setShipment_id(String shipment_id) {
		this.shipment_id = shipment_id;
	}

	public String getShip_date() {
		return ship_date;
	}

	public void setShip_date(String ship_date) {
		this.ship_date = ship_date;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public Object getShipment_cost() {
		return shipment_cost;
	}

	public void setShipment_cost(Object shipment_cost) {
		this.shipment_cost = shipment_cost;
	}

	public Object getInsurance_cost() {
		return insurance_cost;
	}

	public void setInsurance_cost(Object insurance_cost) {
		this.insurance_cost = insurance_cost;
	}

	public String getTracking_number() {
		return tracking_number;
	}

	public void setTracking_number(String tracking_number) {
		this.tracking_number = tracking_number;
	}

	public String getIs_international() {
		return is_international;
	}

	public void setIs_international(String is_international) {
		this.is_international = is_international;
	}

	public String getCarrier_id() {
		return carrier_id;
	}

	public void setCarrier_id(String carrier_id) {
		this.carrier_id = carrier_id;
	}

	public String getService_code() {
		return service_code;
	}

	public void setService_code(String service_code) {
		this.service_code = service_code;
	}

	public String getPackage_code() {
		return package_code;
	}

	public void setPackage_code(String package_code) {
		this.package_code = package_code;
	}

	public String getLabel_format() {
		return label_format;
	}

	public void setLabel_format(String label_format) {
		this.label_format = label_format;
	}

	public String getDisplay_scheme() {
		return display_scheme;
	}

	public void setDisplay_scheme(String display_scheme) {
		this.display_scheme = display_scheme;
	}

	public String getLabel_layout() {
		return label_layout;
	}

	public void setLabel_layout(String label_layout) {
		this.label_layout = label_layout;
	}

	public String getTracking_status() {
		return tracking_status;
	}

	public void setTracking_status(String tracking_status) {
		this.tracking_status = tracking_status;
	}

	public LabelDownload getLabel_download() {
		return label_download;
	}

	public void setLabel_download(LabelDownload label_download) {
		this.label_download = label_download;
	}

	public ArrayList<Object> getPackages() {
		return packages;
	}

	public void setPackages(ArrayList<Object> packages) {
		this.packages = packages;
	}

	public String getChargeEvent() {
		return chargeEvent;
	}

	public void setChargeEvent(String chargeEvent) {
		this.chargeEvent = chargeEvent;
	}
}