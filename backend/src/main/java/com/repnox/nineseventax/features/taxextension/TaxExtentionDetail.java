 package com.repnox.nineseventax.features.taxextension;

public class TaxExtentionDetail {
	private String password;
	
	public String getPassword() {

        return this.password;

    }
	
	public void setPassword(String password) {

        this.password = password;

    }
	public boolean checkPasswordIsMatch() {
		
		try {
			return Integer.parseInt(this.getPassword()) == 120863;
		} catch (Exception e) {
			return false;
		}
       

    }
	
}
