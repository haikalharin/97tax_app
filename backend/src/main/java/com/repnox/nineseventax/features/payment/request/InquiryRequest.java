package com.repnox.nineseventax.features.payment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequest implements OrbitalRequest {

    private String version;
    private MerchantRequest merchant;
    private OrderRequest order;

}
