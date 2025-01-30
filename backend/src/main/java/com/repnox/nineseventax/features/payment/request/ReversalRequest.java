package com.repnox.nineseventax.features.payment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReversalRequest implements OrbitalRequest {

    private String version;
    private MerchantRequest merchant;
    private OrderRequest order;

    public ReversalRequest(String version, String bin, String terminalId) {
        this.version = version;
        this.merchant = MerchantRequest.builder()
                .bin(bin)
                .terminalID(terminalId)
                .build();
    }

}
