package com.repnox.nineseventax.features.payment.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest implements OrbitalRequest {

    private String version;
    private MerchantRequest merchant;
    private ProfileDetailRequest profile;
    private OrderRequest order;
    private PaymentInstrumentRequest paymentInstrument;

}
