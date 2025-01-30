package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.repnox.nineseventax.features.admin.request.VoidRequest;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.payment.request.ReversalRequest;
import com.repnox.nineseventax.features.payment.response.ReversalResponse;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;

public interface OrbitalReversalService {

    ReversalResponse reverse(ReversalRequest request) throws Exception;

    boolean reverse(String authTransactionId, String status, boolean isSendEmail, String username, VoidRequest voidRequest) throws Exception;

    void updateOrderDetails(OrderRecord existing, VoidRequest voidRequest, boolean isManualActionRequired);

    void updatePenaltyOrderDetails(PenaltyOrder existing, VoidRequest voidRequest, boolean isManualActionRequired);

    void updateEinOrderDetails(EinOrder existing, VoidRequest voidRequest, boolean isManualActionRequired);
}
