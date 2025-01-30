package com.repnox.nineseventax.features.admin;

import lombok.Data;

@Data
public class RefundTransactionRequest {

    private String cardNumber;
    private String expirationDate;
    private String amount;
    private String invoiceNumber;

}
