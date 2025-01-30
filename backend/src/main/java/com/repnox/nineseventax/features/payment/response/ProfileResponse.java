package com.repnox.nineseventax.features.payment.response;

import com.repnox.nineseventax.features.payment.request.PaymentInstrumentRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse implements OrbitalResponse {

    private String version;
    private MerchantResponse merchant;
    private ProfileDetailResponse profile;
    private OrderResponse order;
    private TokenResponse token;
    private String procStatus;
    private String procStatusMessage;
    private PaymentInstrumentRequest paymentInstrument;


}
