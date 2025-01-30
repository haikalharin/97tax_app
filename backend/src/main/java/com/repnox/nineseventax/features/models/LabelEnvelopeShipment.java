package com.repnox.nineseventax.features.models;

import java.util.ArrayList;

public class LabelEnvelopeShipment{
	private String carrier_id = "";
	private String service_code = "";
	private String external_order_id;	
	private LabelShipToAndFrom ship_to;
	private LabelShipToAndFrom ship_from;
	private ArrayList<LabelPackages> packages = new ArrayList<LabelPackages>();
	
	public String getCarrierId() {
		return carrier_id;
	}
	public void setCarrierId(String carrier_id) {
		this.carrier_id = carrier_id;
	}
	public String getServiceCode() {
		return service_code;
	}
	public void setServiceCode(String service_code) {
		this.service_code = service_code;
	}
	
	public LabelShipToAndFrom getShipTo() {
		return ship_to;
	}
	public void setShipTo(LabelShipToAndFrom ship_to) {
		this.ship_to = ship_to;
	}
	public LabelShipToAndFrom getShipFrom() {
		return ship_from;
	}
	public void setShipFrom(LabelShipToAndFrom ship_from) {
		this.ship_from = ship_from;
	}
	public String getExternalOrderId() {
		return external_order_id;
	}
	public void setExternalOrderId(String external_order_id) {
		this.external_order_id = external_order_id;
	}
	public ArrayList<LabelPackages> getPackages() {
		return packages;
	}
	public void setPackages(LabelPackages packages) {
		this.packages.add(packages);
	}
}