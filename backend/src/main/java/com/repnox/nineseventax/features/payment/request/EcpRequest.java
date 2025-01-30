package com.repnox.nineseventax.features.payment.request;

import lombok.Data;

@Data
public class EcpRequest {

    private String ecpCheckRT;
    private String ecpCheckDDA;
    private String ecpBankAcctType;
    private String ecpAuthMethod;
    private String ecpDelvMethod;
    private String ecpActionCode;
    private String ecpCheckSerialNumber;
    private String ecpTerminalCity;
    private String ecpTerminalState;
    private String ecpImageReferenceNumber;
    private String ecpSameDayInd;
    private String ecpReDepositFreq;
    private String ecpReDepositInd;
    
}
