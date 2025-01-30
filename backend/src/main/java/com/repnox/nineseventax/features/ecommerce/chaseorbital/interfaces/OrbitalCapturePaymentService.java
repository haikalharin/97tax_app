package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.repnox.nineseventax.features.payment.request.CapturePaymentRequest;
import com.repnox.nineseventax.features.payment.response.CapturePaymentResponse;

public interface OrbitalCapturePaymentService {

    CapturePaymentResponse capture(CapturePaymentRequest request) throws Exception;

}
