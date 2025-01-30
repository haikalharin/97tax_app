package com.repnox.nineseventax.features.payment.response;

import lombok.Data;

@Data
public class PaymentInstrumentResponse {

    private CardResponse card;
    private UseProfileResponse useProfile;
    private EcpResponse ecp;
    private EuddResponse eudd;
    private String targetCardBrand;
    private String customerAccountType;

}
