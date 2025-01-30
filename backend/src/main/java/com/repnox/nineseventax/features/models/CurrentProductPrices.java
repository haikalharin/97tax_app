package com.repnox.nineseventax.features.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentProductPrices {

    private float customEnrollmentFee;
    private float enrollmentFee;
    private float customChangeOfAddressFee;
    private float customExpressOptionFee;
    private float customPayrollFee;

}
