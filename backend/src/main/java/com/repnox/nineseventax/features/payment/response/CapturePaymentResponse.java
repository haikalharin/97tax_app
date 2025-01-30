package com.repnox.nineseventax.features.payment.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapturePaymentResponse implements OrbitalResponse {

    private String version;
    private MerchantResponse merchant;
    private OrderResponse order;

}
