package com.repnox.nineseventax.features.excel;

public class ExcelUsageReportRow {

    private String partnerName;

    private String totalDue;

    private String couponCode;

    private Long lifetimeUsage;

    private Long usageAtPrice;

    private Integer commission;

    private Integer commissionType;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private String notes;

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(String totalDue) {
        this.totalDue = totalDue;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Long getLifetimeUsage() {
        return lifetimeUsage;
    }

    public void setLifetimeUsage(Long lifetimeUsage) {
        this.lifetimeUsage = lifetimeUsage;
    }

    public Long getUsageAtPrice() {
        return usageAtPrice;
    }

    public void setUsageAtPrice(Long usageAtPrice) {
        this.usageAtPrice = usageAtPrice;
    }

    public Integer getCommission() {
        return commission;
    }

    public void setCommission(Integer commission) {
        this.commission = commission;
    }

    public Integer getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(Integer commissionType) {
        this.commissionType = commissionType;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
