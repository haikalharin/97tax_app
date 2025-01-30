package com.repnox.nineseventax.features.payment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest implements OrbitalRequest {

    private String version;
    private String transType;
    private MerchantRequest merchant;
    private PaymentInstrumentRequest paymentInstrument;
    private OrderRequest order;
    private ProfileDetailRequest profile;
    private CryptogramRequest cryptogram;
    private AdditionalAuthInfoRequest additionalAuthInfo;
    private AvsBilling avsBilling;
    private CardholderVerificationRequest cardholderVerification;

    public CreatePaymentRequest(String version, String transType, String bin, String terminalId) {
        this.version = version;
        this.transType = transType;
        this.merchant = MerchantRequest.builder()
                .bin(bin)
                .terminalID(terminalId)
                .build();
    }

}
