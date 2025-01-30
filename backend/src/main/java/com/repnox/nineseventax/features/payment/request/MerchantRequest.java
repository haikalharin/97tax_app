package com.repnox.nineseventax.features.payment.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantRequest {

    private String bin;
    private String terminalID;

}
