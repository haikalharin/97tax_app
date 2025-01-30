package com.repnox.nineseventax.features.ein;

import lombok.Data;

import java.io.Serializable;
@Data
public class EinAuthorizeInfo implements Serializable {
    private EinOrder order;
    private String responseJwt;
}
