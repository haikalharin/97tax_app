package com.repnox.nineseventax.features.admin;

public class OrderSearchQuery {

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String product;

    private Boolean isCalifornia;

    private Boolean isNewJersey;
    private Boolean isGeorgia;
    private Boolean isMichigan;
    private Boolean isIllinois;

    private Boolean isOwedFromBusiness;

    private OrderSearchStatuses status;

    private Long orderId;

    private String createdAt;

    private DateRange dateRange;

    private String startAt;

    private String endAt;

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public OrderSearchStatuses getStatus() {
        return status;
    }

    public void setStatus(OrderSearchStatuses status) {
        this.status = status;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProduct() {
        return product;
    }

    public Boolean getIsOwedFromBusiness() {
        return isOwedFromBusiness;
    }

    public void setIsOwedFromBusiness(Boolean isOwedFromBusiness) {
        this.isOwedFromBusiness = isOwedFromBusiness;
    }

    public void setIsCalifornia(Boolean isCalifornia) {
        this.isCalifornia = isCalifornia;
    }

    public boolean getIsCalifornia() {
        return isCalifornia;
    }

    public void setIsNewJersey(Boolean isNewJersey) {
        this.isNewJersey = isNewJersey;
    }

    public boolean getIsNewJersey() {
        return isNewJersey;
    }

    public void setIsGeorgia(Boolean isGeorgia) {
        this.isGeorgia = isGeorgia;
    }

    public boolean getIsGeorgia() {
        return isGeorgia;
    }

    public Boolean getIsMichigan() {
        return isMichigan;
    }

    public void setIsMichigan(Boolean isMichigan) {
        this.isMichigan = isMichigan;
    }

    public void setIsIllinois(Boolean isIllinois) {
        this.isIllinois = isIllinois;
    }

    public boolean getIsIllinois() {
        return isIllinois;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        if(createdAt!="") {
//            	DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//            	LocalDate date = LocalDate.parse(createdAt, inputFormatter);
//            	this.createdAt=java.sql.Date.valueOf(date);
            this.createdAt=createdAt;
        }
        else {
            this.createdAt=null;
        }
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getEndAt() {
        return endAt;
    }
}