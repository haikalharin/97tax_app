package com.repnox.nineseventax.features.payment.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

    private String orderID;
    private String amount;
    private String adjustedAmount;
    private String outstandingAmt;
    private String industryType;
    private String comments;
    private String txRefNum;
    private String customerProfileOrderOverideInd;
    private String customerProfileFromOrderInd;
    private String orderDefaultAmount;
    private StatusResponse status;
    private String txRefIdx;
    private String requestAmount;
    private String remainingBalance;
    private String redeemedAmount;
    private String shippingMethod;
    private String shippingRef;
    private String inquiryRetryNumber;
    private String retryAttempCount;

}
