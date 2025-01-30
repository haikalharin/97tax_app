package com.repnox.nineseventax.features.payment.request;

import lombok.Data;

@Data
public class EuddRequest {

    private String euddBankSortCode;
    private String euddCountryCode;
    private String euddRibCode;
    private String euddBankBranchCode;
    private String euddIBAN;
    private String euddBIC;
    private String euddMandateSignatureDate;
    private String euddMandateID;
        
}
