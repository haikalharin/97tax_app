package com.repnox.nineseventax.features.payment.request;

import lombok.Data;

@Data
public class CardholderVerificationRequest {
    private String ccCardVerifyPresenceInd = "1";
    private String ccCardVerifyNum = "";
}
