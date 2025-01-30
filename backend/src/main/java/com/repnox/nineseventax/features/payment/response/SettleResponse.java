package com.repnox.nineseventax.features.payment.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SettleResponse implements OrbitalResponse {

    private String version;
    private MerchantResponse merchant;
    private OrderResponse order;
    private BatchResponse batch;
    private String procStatus;
    private String procStatusMessage;

}
