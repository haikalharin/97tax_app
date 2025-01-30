package com.repnox.nineseventax.features.ecommerce.chaseorbital;

public class SemplestString {
	private String customString;

	public SemplestString(String s) {
		this.customString = s;
	}

	public SemplestString() {}

	public String getSemplestString() {
		return customString;
	}

	public void setSemplestString(String semplestString) {
		this.customString = semplestString;
	}

	public SemplestString toSemplestString(String string){
		SemplestString ret = new SemplestString(string);
		return ret;
	}

	@Override
	public String toString() {
		return "SemplestString [semplestString=" + customString + "]";
	}
}

