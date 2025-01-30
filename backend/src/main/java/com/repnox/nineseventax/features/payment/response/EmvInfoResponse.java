package com.repnox.nineseventax.features.payment.response;

import lombok.Data;

@Data
public class EmvInfoResponse {

    private String posEntryMode;
    private String dataEntrySource;
    private String panSequenceNumber;
    private String applicationId;
    private String applicationLabel;
    private String chipCardData;
    private String tvr;
    private String tsi;
    private String attendedTerminal;
    private String terminalLocation;
    private String cardholderPresent;
    private String cardholderActivatedTerminal;
    private String terminalEntry;
    private String terminalLaneId;
    private String latitudeLongitude;
    private String politicalTimeZone;
    private String vendorID;
    private String softwareID;
    private String mobileDeviceType;
    private String deviceID;
    private String localDateTime;
    private String cardPresentInd;
    private String readerSerialNumber;
    private String keySerialNumber;
    private String industrySupport;
    private String complianceClass;
    private String deviceSecuritySupport;
    private String emvSupport;
    private String peripheralSupport;
    private String communicationSupport;

}
