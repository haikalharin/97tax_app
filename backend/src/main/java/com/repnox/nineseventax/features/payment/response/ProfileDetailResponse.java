package com.repnox.nineseventax.features.payment.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDetailResponse {

    private String addProfileFromOrder;
    private String customerRefNum;
    private String profileOrderOverideInd;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String accountUpdaterEligibility;
    private String profileProcStatus;
    private String profileProcStatusMsg;
    private String profileAction;
    private String customerAddress1;
    private String customerAddress2;
    private String customerCity;
    private String customerState;
    private String customerZIP;
    private String customerCountryCode;
    private String status;
    private String customerProfileAction;
    private String scheduledDate;
}
