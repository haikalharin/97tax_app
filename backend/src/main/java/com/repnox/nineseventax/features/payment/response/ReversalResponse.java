package com.repnox.nineseventax.features.payment.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReversalResponse {

    private String version;
    private MerchantResponse merchant;
    private OrderResponse order;
    private EmvInfoResponse emvInfo;

}
