package com.repnox.nineseventax.features.models;

public class LabelShipToAndFrom{
	private String name = "";
	private String company_name = "";
	private String address_line1 = "";
	private String address_line2 = "";
	private String city_locality = "";
	private String state_province = "";
	private String postal_code = "";
	private String phone = "";
	private final String country_code = "US"; // Do Not Delete!
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompanyName() {
		return company_name;
	}
	public void setCompanyName(String companyName) {
		this.company_name = companyName;
	}
	
	public String getAddressLine1() {
		return address_line1;
	}
	public void setAddressLine1(String address_line1) {
		this.address_line1 = address_line1;
	}
	public String getAddressLine2() {
		return address_line2;
	}
	public void setAddressLine2(String address_line2) {
		this.address_line2 = address_line2;
	}
	public String getCityLocality() {
		return city_locality;
	}
	public void setCityLocality(String city_locality) {
		this.city_locality = city_locality;
	}
	public String getStateProvience() {
		return state_province;
	}
	public void setStateProvience(String state_provience) {
		this.state_province = state_provience;
	}
	public String getPostalCode() {
		return postal_code;
	}
	public void setPostalCode(String postal_code) {
		this.postal_code = postal_code;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}