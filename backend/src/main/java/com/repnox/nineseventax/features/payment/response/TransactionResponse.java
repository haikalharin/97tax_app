package com.repnox.nineseventax.features.payment.response;

import lombok.Data;

@Data
public class TransactionResponse {

    private MerchantResponse merchant;
    private OrderResponse order;
    private AlternativePaymentMethodResponse alternativePaymentMethod;
    private String procStatus;
    private String procStatusMessage;

}
