package com.repnox.nineseventax.features.admin;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LabelEnvelopResponse {
	
	private String label_id;
	private String status;
	private String shipment_id;
	private String ship_date;
	private String created_at;
	private Object shipment_cost;
	private Object insurance_cost;
	private Object label_download;
	private ArrayList packages;
	
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
	public Object getLabel_download() {
		return label_download;
	}
	public void setLabel_download(Object label_download) {
		this.label_download = label_download;
	}
	public ArrayList getPackages() {
		return packages;
	}
	public void setPackages(ArrayList packages) {
		this.packages = packages;
	}
	
}