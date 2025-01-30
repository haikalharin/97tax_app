package com.repnox.nineseventax.features.admin;

import lombok.Data;

@Data
public class OrderSearchStatuses {

    private Boolean any;

    private Boolean incomplete;

    private Boolean cancelled;

    private Boolean processing;

    private Boolean onHold;

    private Boolean failed;

    private Boolean botError;

    private Boolean completed;

    private Boolean chargeback;

    private Boolean awaitingSignatureService;

    private Boolean signed;

    private Boolean deleted;

}
