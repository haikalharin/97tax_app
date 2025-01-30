package com.repnox.nineseventax.features.partnercode.model;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;

// @SqlResultSetMapping(
//     name = "SomeMapping",
//     classes = {
//         @ConstructorResult(targetClass = PCUsageReport.class,
//             columns = {
//                 @ColumnResult(name = "partnerName", type = String.class),
//                 @ColumnResult(name = "totalDue", type = String.class),
//                 @ColumnResult(name = "lifetimeUsage", type = Long.class)
//             }
//         )
//     }
// )

public class PCUsageReport {

	private String partnerName;

	private String totalDue;
	
	private String partnerCode;

	private Long lifetimeUsage;

	private Long usageAtPrice;

	private Integer commission;

	private Integer commissionTypeId;

	private String contactName;

	private String phone;

	private String email;

	private String notes;

	public PCUsageReport(String partnerName, String totalDue, String partnerCode, Long lifetimeUsage, Integer commission, Integer commissionTypeId, String contactName, String phone, String email, String notes) {
		this.partnerName = partnerName;
		this.totalDue = totalDue;
		this.partnerCode = partnerCode;
		this.lifetimeUsage = lifetimeUsage;
		this.usageAtPrice = lifetimeUsage;
		this.commission = commission;
		this.commissionTypeId = commissionTypeId;
		this.contactName = contactName;
		this.phone = phone;
		this.email = email;
		this.notes = notes;
	}

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

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
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

	public Integer getCommissionTypeId() {
		return commissionTypeId;
	}

	public void setCommissionTypeId(Integer commissionTypeId) {
		this.commissionTypeId = commissionTypeId;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
