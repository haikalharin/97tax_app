package com.repnox.nineseventax.features.ecommerce.chaseorbital;

public class CustomerObject {

	private String customerProfileRefNum;
	private String name;
	private String Address1;
	private String Address2;
	private String City;
	private String StateAbbr;
	private String ZipCode;
	private String Email;
	private String Phone;
	private CardBrand cardBrand;
	private String creditCardNumber;
	private String creditCardSecurityCode;
    private String ExpireDateMMYY;

	public CardBrand getCardBrand() {
		return cardBrand;
	}

	public String getCreditCardSecurityCode() {
		return creditCardSecurityCode;
	}

	public void setCreditCardSecurityCode(String creditCardSecurityCode) {
		this.creditCardSecurityCode = creditCardSecurityCode;
	}

	public String getCustomerProfileRefNum() {
		return customerProfileRefNum;
	}

	public void setCustomerProfileRefNum(String customerProfileRefNum) {
		this.customerProfileRefNum = customerProfileRefNum;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public String getExpireDateMMYY() {
		return ExpireDateMMYY;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public void setExpireDateMMYY(String expireDateMMYY) {
		ExpireDateMMYY = expireDateMMYY;
	}

	public String getAddress1() {
		return Address1;
	}

	public void setAddress1(String address1) {
		Address1 = address1;
	}

	public String getAddress2() {
		return Address2;
	}

	public void setAddress2(String address2) {
		Address2 = address2;
	}

	public String getCity()	{
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getStateAbbr() {
		return StateAbbr;
	}

	public void setStateAbbr(String stateAbbr) {
		StateAbbr = stateAbbr;
	}

	public String getZipCode() {
		return ZipCode;
	}

	public void setZipCode(String zipCode) {
		ZipCode = zipCode;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Address1 == null) ? 0 : Address1.hashCode());
		result = prime * result + ((Address2 == null) ? 0 : Address2.hashCode());
		result = prime * result + ((City == null) ? 0 : City.hashCode());
		result = prime * result + ((Email == null) ? 0 : Email.hashCode());
		result = prime * result + ((ExpireDateMMYY == null) ? 0 : ExpireDateMMYY.hashCode());
		result = prime * result + ((Phone == null) ? 0 : Phone.hashCode());
		result = prime * result + ((StateAbbr == null) ? 0 : StateAbbr.hashCode());
		result = prime * result + ((ZipCode == null) ? 0 : ZipCode.hashCode());
		result = prime * result + ((cardBrand == null) ? 0 : cardBrand.hashCode());
		result = prime * result + ((creditCardNumber == null) ? 0 : creditCardNumber.hashCode());
		result = prime * result + ((creditCardSecurityCode == null) ? 0 : creditCardSecurityCode.hashCode());
		result = prime * result + ((customerProfileRefNum == null) ? 0 : customerProfileRefNum.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerObject other = (CustomerObject) obj;
		if (Address1 == null) {
			if (other.Address1 != null)
				return false;
		} else if (!Address1.equals(other.Address1))
			return false;

        if (Address2 == null) {
			if (other.Address2 != null)
				return false;
		} else if (!Address2.equals(other.Address2))
			return false;

		if (City == null) {
			if (other.City != null)
				return false;
		} else if (!City.equals(other.City))
			return false;

        if (Email == null) {
			if (other.Email != null)
				return false;
		} else if (!Email.equals(other.Email))
			return false;

		if (ExpireDateMMYY == null) {
			if (other.ExpireDateMMYY != null)
				return false;
		} else if (!ExpireDateMMYY.equals(other.ExpireDateMMYY))
			return false;

		if (Phone == null) {
			if (other.Phone != null)
				return false;
		} else if (!Phone.equals(other.Phone))
			return false;

        if (StateAbbr == null) {
			if (other.StateAbbr != null)
				return false;
		} else if (!StateAbbr.equals(other.StateAbbr))
			return false;

		if (ZipCode == null) {
			if (other.ZipCode != null)
				return false;
		} else if (!ZipCode.equals(other.ZipCode))
			return false;

		if (cardBrand != other.cardBrand)
			return false;

		if (creditCardNumber == null) {
			if (other.creditCardNumber != null)
				return false;
		} else if (!creditCardNumber.equals(other.creditCardNumber))
			return false;

		if (creditCardSecurityCode == null) {
			if (other.creditCardSecurityCode != null)
				return false;
		} else if (!creditCardSecurityCode.equals(other.creditCardSecurityCode))
			return false;

		if (customerProfileRefNum == null) {
			if (other.customerProfileRefNum != null)
				return false;
		} else if (!customerProfileRefNum.equals(other.customerProfileRefNum))
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		return true;
	}

	@Override

	public String toString() {
		return "CustomerObject [customerProfileRefNum=" + customerProfileRefNum + ", name=" + name + ", Address1=" + Address1 + ", Address2=" + Address2 + ", City=" + City + ", StateAbbr=" + StateAbbr + ", ZipCode=" + ZipCode + ", Email=" + Email + ", Phone=" + Phone + ", cardBrand=" + cardBrand
				+ ", creditCardNumber=" + creditCardNumber + ", creditCardSecurityCode=" + creditCardSecurityCode + ", ExpireDateMMYY=" + ExpireDateMMYY + "]";
	}
}

