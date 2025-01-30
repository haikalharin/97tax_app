package com.repnox.nineseventax.features.payment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Used for payment refund
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UseProfile {
    private String useCustomerRefNum;
}
