package com.repnox.nineseventax.features.order.repo;

public class FulfillmentOrder {

    public static final String ORDER_TYPE_PAYMENT_PLAN = "payment_plan";
    public static final String ORDER_TYPE_PENATY_WAIVER = "penalty_waiver";

    private String id;
    private String firstName;
    private String lastName;
    private String shippingAddress1;
    private String shippingAddress2;
    private String shippingCity;
    private String shippingState;
    private String shippingZip;
    private String phone;
    private String processingSpeed;
    private String orderType;

    private boolean isCalifornia;
    private boolean isNewJersey;
    private boolean isGeorgia;
    private boolean isIllinois;

    private String authorizeTransactionId;
    private String refundAuthTransId;

    private String notes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProcessingSpeed() {
        return processingSpeed;
    }

    public void setProcessingSpeed(String processingSpeed) {
        this.processingSpeed = processingSpeed;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public boolean getIsCalifornia() {
        return isCalifornia;
    }

    public void setIsCalifornia(boolean california) {
        isCalifornia = california;
    }

    public boolean getIsNewJersey() {
        return isNewJersey;
    }

    public void setIsNewJersey(boolean newJersey) {
        isNewJersey = newJersey;
    }

    public boolean getIsGeorgia() {
        return isGeorgia;
    }

    public void setIsGeorgia(boolean georgia) {
        isGeorgia = georgia;
    }

    public boolean getIsIllinois() {
        return isIllinois;
    }

    public void setIsIllinois(boolean illinois) {
        isIllinois = illinois;
    }

    public String getAuthorizeTransactionId() {
        return authorizeTransactionId;
    }

    public void setAuthorizeTransactionId(String authorizeTransactionId) {
        this.authorizeTransactionId = authorizeTransactionId;
    }

    public String getRefundAuthTransId() {
        return refundAuthTransId;
    }

    public void setRefundAuthTransId(String refundAuthTransId) {
        this.refundAuthTransId = refundAuthTransId;
    }


}
