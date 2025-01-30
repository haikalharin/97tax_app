package com.repnox.nineseventax.features.admin;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;

public class OnSaveRequestPenalty {
	
	private PenaltyOrder orderRecord;
	private String content;
	private String trackingNumberContent;
	private String emailChangeContent;
	private String user;	
	
	public PenaltyOrder getOrderRecord() {
		return this.orderRecord;
	}	
	public void SetOrderRecord(PenaltyOrder arg) {
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
