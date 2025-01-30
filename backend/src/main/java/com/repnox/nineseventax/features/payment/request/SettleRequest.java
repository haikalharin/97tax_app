package com.repnox.nineseventax.features.payment.request;

import com.repnox.nineseventax.features.payment.response.MerchantResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettleRequest implements OrbitalRequest {

    private String version;
    private MerchantResponse merchant;

}
