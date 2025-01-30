package com.repnox.nineseventax.features.ecommerce;

import lombok.Data;

import java.io.Serializable;

@Data
public class PartnerInfo implements Serializable {

    private String partnerName;
    private String partnerTitle;
    private String partnerEffectiveDay;
    private String partnerEffectiveMonth;
    private String partnerEffectiveYear;
    private String partnerAddress1;
    private String partnerAddress2;
    private String partnerCity;
    private String partnerState;
    private String partnerZip;
    private String partnerPhoneNumber;
    private String partnerSsn;
    private String partnerPercentOwnership;

}
