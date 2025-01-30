package com.repnox.nineseventax.features.payment.request;

import lombok.Data;

@Data
public class CryptogramRequest {

    private String digitalTokenCryptogram;
    private String verifyByVisaCAVV;
    private String mcSecureCodeAAV;
    private String verifyByVisaXID;

}
