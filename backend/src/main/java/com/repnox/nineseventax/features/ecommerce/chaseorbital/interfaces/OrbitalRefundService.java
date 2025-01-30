package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.repnox.nineseventax.features.payment.request.RefundRequest;
import com.repnox.nineseventax.features.payment.response.RefundResponse;
import org.springframework.http.ResponseEntity;

public interface OrbitalRefundService {

    RefundResponse refundPayment(RefundRequest request) throws Exception;

    ResponseEntity refundPayment(String authTransactionId, String refundAmount, String status, boolean isSendEmail) throws Exception;
    ResponseEntity refundPayment(String authTransactionId, String status, boolean isSendEmail) throws Exception;

}
