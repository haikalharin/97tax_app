package com.repnox.nineseventax.features.models;

public class LabelWeight{
	private Integer value;
	private String unit;
	
	public void setValue(Integer value) {
		this.value = value;
	}
	public Integer getValue() {
		return value;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getUnit() {
		return unit;
	}
}