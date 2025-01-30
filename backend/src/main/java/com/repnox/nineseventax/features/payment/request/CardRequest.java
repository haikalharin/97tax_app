package com.repnox.nineseventax.features.payment.request;

import lombok.Data;

@Data
public class CardRequest {

    private String cardBrand;
    private String ccAccountNum;
    private String magStripeTrack1;
    private String magStripeTrack2;
    private String dpanInd;
    private String tokenTxnType;
    private String ccExp;
    private String dpanAccountStatus;
    private String safetechToken;
    private String cardType;
    private String startAccountNum;
    private String healthBenefitCardInd;

}
