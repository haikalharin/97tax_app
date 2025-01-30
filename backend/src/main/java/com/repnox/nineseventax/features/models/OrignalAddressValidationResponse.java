package com.repnox.nineseventax.features.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrignalAddressValidationResponse {
	
	@JsonProperty(value = "name")
	private String name;
	@JsonProperty(value = "phone")
	private String phone;
	@JsonProperty(value = "company_name")
	private String companyName;
	@JsonProperty(value = "address_line1")
	private String addressLine1;
	@JsonProperty(value = "address_line2")
	private String addressLine2;
	@JsonProperty(value = "address_line3")
	private String addressLine3;
	@JsonProperty(value = "city_locality")
	private String cityLocality;
	@JsonProperty(value = "state_province")
	private String stateProvince;
	@JsonProperty(value = "postal_code")
	private String postalCode;
	@JsonProperty(value = "country_code")
	private String countryCode;
	@JsonProperty(value = "address_residential_indicator")
	private String addressResidentialIndicator;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getAddressLine3() {
		return addressLine3;
	}
	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}
	public String getCityLocality() {
		return cityLocality;
	}
	public void setCityLocality(String cityLocality) {
		this.cityLocality = cityLocality;
	}
	public String getStateProvince() {
		return stateProvince;
	}
	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getAddressResidentialIndicator() {
		return addressResidentialIndicator;
	}
	public void setAddressResidentialIndicator(String addressResidentialIndicator) {
		this.addressResidentialIndicator = addressResidentialIndicator;
	}
}
