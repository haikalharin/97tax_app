package com.repnox.nineseventax.features.payment.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AvsBilling{
    public String avsZip;
    public String avsAddress1;
    public String avsAddress2;
    public String avsCity;
    public String avsState;
    public String avsPhone;
    public String avsPhoneType;
    public String avsName;
    public String avsCountryCode;
}
