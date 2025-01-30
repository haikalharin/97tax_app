package com.repnox.nineseventax.features.payment.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlternativePaymentMethodResponse {

    private String paymentMethod;
    private String hostTxRefNum;
    private String redirectURL;
    private String redirectSecret;
    private String minAmt;
    private String maxAmt;

}
