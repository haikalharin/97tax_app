package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCapturePaymentService;
import com.repnox.nineseventax.features.payment.request.CapturePaymentRequest;
import com.repnox.nineseventax.features.payment.request.OrbitalRequest;
import com.repnox.nineseventax.features.payment.response.CapturePaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OrbitalCapturePaymentServiceImpl extends BaseOrbital implements OrbitalCapturePaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public CapturePaymentResponse capture(CapturePaymentRequest request) throws Exception {
        HttpEntity<CapturePaymentRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<CapturePaymentResponse> response = restTemplate.exchange(
                capturePaymentUri(), HttpMethod.POST, entity, CapturePaymentResponse.class);

        constructResponse(response, request);

        log.debug("Capture payment body : {}", response.getBody());

        return response.getBody();
    }

    @Override
    boolean hitFailover(OrbitalRequest request) {
        HttpEntity<CapturePaymentRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<CapturePaymentResponse> response = restTemplate.exchange(
                capturePaymentFailoverUri(), HttpMethod.POST, entity, CapturePaymentResponse.class);

        return isOK(response);
    }
}
