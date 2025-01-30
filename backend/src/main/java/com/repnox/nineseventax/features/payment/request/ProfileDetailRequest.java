package com.repnox.nineseventax.features.payment.request;

import lombok.Data;

@Data
public class ProfileDetailRequest {

    private String addProfileFromOrder;
    private String customerRefNum;
    private String profileOrderOverideInd;
    private String customerName;
    private String customerAddress1;
    private String customerAddress2;
    private String customerCity;
    private String customerState;
    private String customerZIP;
    private String customerEmail;
    private String customerPhone;
    private String accountUpdaterEligibility;
    private String profileProcStatus;
    private String profileProcStatusMsg;
    private String profileAction;
    private String customerCountryCode;
    private StatusRequest status;
    private String customerProfileAction;
    private String scheduledDate;

}
