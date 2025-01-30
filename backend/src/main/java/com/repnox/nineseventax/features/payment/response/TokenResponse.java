package com.repnox.nineseventax.features.payment.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {

    private String tokenRequestorID;
    private String actionCode;

}
