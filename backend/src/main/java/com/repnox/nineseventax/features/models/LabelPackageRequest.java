package com.repnox.nineseventax.features.models;

public class LabelPackageRequest{
	private LabelPackageShipment shipment;
	private final String label_download_type = "inline";
	private final String label_format = "pdf";
	private final String display_scheme = "label";
	private final String label_layout = "4x6";
	
	public LabelPackageShipment getShipment() {
		return shipment;
	}

	public void setShipment(LabelPackageShipment shipment) {
		this.shipment = shipment;
	}
}