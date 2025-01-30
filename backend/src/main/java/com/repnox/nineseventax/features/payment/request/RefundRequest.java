package com.repnox.nineseventax.features.payment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest implements OrbitalRequest {

    private String version;
    private MerchantRequest merchant;
    private OrderRequest order;
    private PaymentInstrumentRequest paymentInstrument;


    public RefundRequest(String version, String bin, String terminalId) {
        this.version = version;
        this.merchant = MerchantRequest.builder()
                .bin(bin)
                .terminalID(terminalId)
                .build();
    }

}
