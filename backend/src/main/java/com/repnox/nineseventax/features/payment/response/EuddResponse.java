package com.repnox.nineseventax.features.payment.response;

import lombok.Data;

@Data
public class EuddResponse {

    private String euddBankSortCode;
    private String euddCountryCode;
    private String euddRibCode;
    private String euddBankBranchCode;
    private String euddIBAN;
    private String euddBIC;
    private String euddMandateSignatureDate;
    private String euddMandateID;
        
}
