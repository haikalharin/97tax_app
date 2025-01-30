package com.repnox.nineseventax.features.payment.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstrumentRequest {

    private CardRequest card;
//    private UseProfileRequest useProfile;
    private EcpRequest ecp;
    private EuddRequest eudd;
    private String targetCardBrand;
    private String customerAccountType;
    private UseProfile useProfile;



}
