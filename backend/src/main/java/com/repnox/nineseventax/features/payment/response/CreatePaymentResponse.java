package com.repnox.nineseventax.features.payment.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePaymentResponse implements OrbitalResponse {

    private String version;
    private String transType;
    private MerchantResponse merchant;
    private OrderResponse order;
    private String procStatus;
    private String procStatusMessage;

}
