package com.repnox.nineseventax.features.admin;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;

public class OnSaveRequest {
	
	private OrderRecord orderRecord;
	private String content;
	private String trackingNumberContent;
	private String emailChangeContent;
	private String user;	
	
	public OrderRecord getOrderRecord() {
		return this.orderRecord;
	}	
	public void SetOrderRecord(OrderRecord arg) {
		this.orderRecord = arg;
	}
	
	public String getContent() {
    	return this.content;
    }
    public void setContent(String arg) {
    	this.content = arg;
    }
    
	public String getTrackingNumberContent() {
		return trackingNumberContent;
	}
	public void setTrackingNumberContent(String trackingNumberContent) {
		this.trackingNumberContent = trackingNumberContent;
	}
	public String getEmailChangeContent() {
		return emailChangeContent;
	}
	public void setEmailChangeContent(String emailChangeContent) {
		this.emailChangeContent = emailChangeContent;
	}
	
    public String getUser() {
    	return this.user;    	
    }
    public void SetUser(String arg) {
    	this.user = arg;
    }
 }
