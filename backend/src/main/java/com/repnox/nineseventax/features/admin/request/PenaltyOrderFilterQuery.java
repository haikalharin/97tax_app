package com.repnox.nineseventax.features.admin.request;

import com.repnox.nineseventax.features.admin.OrderSearchStatuses;

public class PenaltyOrderFilterQuery {
    private String orderId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private OrderSearchStatuses status;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public OrderSearchStatuses getStatus() {
        return status;
    }

    public void setStatus(OrderSearchStatuses status) {
        this.status = status;
    }
}
