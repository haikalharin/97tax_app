package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.repnox.nineseventax.features.ecommerce.OrderInfo;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.payment.request.CreatePaymentRequest;
import com.repnox.nineseventax.features.payment.response.CreatePaymentResponse;
import com.repnox.nineseventax.features.payment.response.ReversalResponse;
import org.springframework.http.ResponseEntity;
import com.repnox.nineseventax.features.payment.request.CreatePaymentRequestAfterSettled;


public interface OrbitalCreatePaymentService {

    CreatePaymentResponse createPayment(CreatePaymentRequest request) throws Exception;

    String createPayment4Logging(CreatePaymentRequest request) throws Exception;

    CreatePaymentRequest buildPaymentRequest(OrderInfo orderInfo) throws Exception ;

    CreatePaymentRequest buildEinPaymentRequest(EinOrder orderInfo) throws Exception ;

    ReversalResponse paymentRequestAfterSettled(CreatePaymentRequestAfterSettled paymentRequest) throws Exception;

}
