package com.repnox.nineseventax.features.payment.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusRequest {

    private String procStatus;
    private String procStatusMessage;
    private String hostRespCode;
    private String actualRespCd;
    private String respCode;
    private String respCodeMessage;
    private String approvalStatus;
    private String authorizationCode;
    private String partialAuthOccurred;
    private String visaVbVRespCode;
    private String mcRecurringAdvCode;
    private String countryFraudFilterStatus;
    private String autoAuthProcStatus;
    private String autoAuthStatusMsg;
    private String autoAuthApprovalStatus;
    private String autoAuthResponseCodes;
    private String pymtBrandAuthResponseCode;
    private String pymtBrandResponseCodeCategory;
    private String fundingStatus;

}
