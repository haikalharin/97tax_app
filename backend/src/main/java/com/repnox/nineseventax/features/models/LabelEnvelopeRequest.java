package com.repnox.nineseventax.features.models;

public class LabelEnvelopeRequest{
	private LabelEnvelopeShipment shipment;
	private final String label_download_type = "inline";
	private final String label_format = "pdf";
	private final String display_scheme = "label";
	private final String label_layout = "4x6";
	

	public LabelEnvelopeShipment getShipment() {
		return shipment;
	}

	public void setShipment(LabelEnvelopeShipment shipment) {
		this.shipment = shipment;
	}
}