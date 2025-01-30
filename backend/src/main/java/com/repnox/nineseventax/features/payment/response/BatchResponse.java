package com.repnox.nineseventax.features.payment.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchResponse {

    private String settleRejectedHoldingBin;
    private String batchSeqNum;

}
