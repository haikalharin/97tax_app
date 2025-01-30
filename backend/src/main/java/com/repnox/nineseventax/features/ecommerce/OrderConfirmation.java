package com.repnox.nineseventax.features.ecommerce;

import java.util.Date;
import java.math.BigDecimal;

public class OrderConfirmation {
    private Long userId;

    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private String amount;

    private String billingAddress1;

    private String billingAddress2;

    private String billingCity;

    private String billingState;

    private String billingZip;

    private String shippingAddress1;

    private String shippingAddress2;

    private String shippingCity;

    private String shippingState;

    private String shippingZip;

    private String orderNumber;

    private String orderId;

    private Date orderDate;

    private Boolean isOwedFromBusiness;

    private Boolean isCalifornia;

    private Boolean isNewJersey;
    private Boolean isGeorgia;
    private Boolean isIllinois;

    private BigDecimal totalDebt;

    private Integer monthlyPayment;

    private String paymentDayOfMonth;

    private String status;
    
    private String upsellProduct;

	private Integer upsellClicked;
    
    private Integer upsellShown; 

	private Boolean married;
    
    private String filingJointly;

    private Boolean isPenaltyWaiver;
    private String penaltyType;
    private String penaltyYear;
    private String highestPenalty;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBillingAddress1() {
        return billingAddress1;
    }

    public void setBillingAddress1(String billingAddress1) {
        this.billingAddress1 = billingAddress1;
    }

    public String getBillingAddress2() {
        return billingAddress2;
    }

    public void setBillingAddress2(String billingAddress2) {
        this.billingAddress2 = billingAddress2;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingState() {
        return billingState;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public String getBillingZip() {
        return billingZip;
    }

    public void setBillingZip(String billingZip) {
        this.billingZip = billingZip;
    }

    public String getShippingAddress1() {
        return shippingAddress1;
    }

    public void setShippingAddress1(String shippingAddress1) {
        this.shippingAddress1 = shippingAddress1;
    }

    public String getShippingAddress2() {
        return shippingAddress2;
    }

    public void setShippingAddress2(String shippingAddress2) {
        this.shippingAddress2 = shippingAddress2;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }

    public String getShippingZip() {
        return shippingZip;
    }

    public void setShippingZip(String shippingZip) {
        this.shippingZip = shippingZip;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Boolean getIsOwedFromBusiness() {
        return isOwedFromBusiness;
    }

    public void setIsOwedFromBusiness(Boolean isOwedFromBusiness) {
        this.isOwedFromBusiness = isOwedFromBusiness;
    }

    public Boolean getIsCalifornia() {
        return isCalifornia;
    }

    public void setIsCalifornia(Boolean isCalifornia) {
        this.isCalifornia = isCalifornia;
    }

    public Boolean getIsNewJersey() {
        return isNewJersey;
    }

    public void setIsNewJersey(Boolean isNewJersey) {
        this.isNewJersey = isNewJersey;
    }
    
    public void setIsGeorgia(Boolean isGeorgia) {
        this.isGeorgia = isGeorgia;
    }

    public Boolean getIsGeorgia() {
        return isGeorgia;
    }

    public void setIsIllinois(Boolean isIllinois) {
        this.isIllinois = isIllinois;
    }

    public Boolean getIsIllinois() {
        return isIllinois;
    }

    public BigDecimal getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(BigDecimal totalDebt) {
        this.totalDebt = totalDebt;
    }

    public Integer getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(Integer monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public String getPaymentDayOfMonth() {
        return paymentDayOfMonth;
    }

    public void setPaymentDayOfMonth(String paymentDayOfMonth) {
        this.paymentDayOfMonth = paymentDayOfMonth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUpsellProduct() {
		return upsellProduct;
	}

	public void setUpsellProduct(String upsellProduct) {
		this.upsellProduct = upsellProduct;
	}

	public Integer getUpsellClicked() {
		return upsellClicked;
	}

	public void setUpsellClicked(Integer upsellClicked) {
		this.upsellClicked = upsellClicked;
	}

	public Integer getUpsellShown() {
		return upsellShown;
	}

	public void setUpsellShown(Integer upsellShown) {
		this.upsellShown = upsellShown;
	}
    
	public Boolean getMarried() {
		return married;
	}

	public void setMarried(Boolean married) {
		this.married = married;
	}

	public String getfilingJointly() {
		return filingJointly;
	}

	public void setfilingJointly(String filingJointly) {
		this.filingJointly = filingJointly;
	}

    public Boolean getPenaltyWaiver() {
        return isPenaltyWaiver;
    }

    public void setPenaltyWaiver(Boolean penaltyWaiver) {
        isPenaltyWaiver = penaltyWaiver;
    }

    public String getPenaltyType() {
        return penaltyType;
    }

    public void setPenaltyType(String penaltyType) {
        this.penaltyType = penaltyType;
    }

    public String getPenaltyYear() {
        return penaltyYear;
    }

    public void setPenaltyYear(String penaltyYear) {
        this.penaltyYear = penaltyYear;
    }

    public String getHighestPenalty() {
        return highestPenalty;
    }

    public void setHighestPenalty(String highestPenalty) {
        this.highestPenalty = highestPenalty;
    }
}
