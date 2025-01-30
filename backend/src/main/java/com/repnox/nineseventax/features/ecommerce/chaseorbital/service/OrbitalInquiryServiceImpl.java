package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalInquiryService;
import com.repnox.nineseventax.features.payment.request.InquiryRequest;
import com.repnox.nineseventax.features.payment.request.OrbitalRequest;
import com.repnox.nineseventax.features.payment.response.InquiryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OrbitalInquiryServiceImpl extends BaseOrbital implements OrbitalInquiryService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public InquiryResponse inquiry(InquiryRequest request) throws Exception {
        log.debug("Start inquiry order ID {} ", request.getOrder().getOrderID());
        HttpEntity<InquiryRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<InquiryResponse> response = restTemplate.exchange(
                inquiryUri(), HttpMethod.POST, entity, InquiryResponse.class);

        constructResponse(response, request);
        log.debug("Inquiry body : {}", response.getBody());

        return response.getBody();
    }

    @Override
    boolean hitFailover(OrbitalRequest request) {
        HttpEntity<InquiryRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<InquiryResponse> response = restTemplate.exchange(
                inquiryFailoverUri(), HttpMethod.POST, entity, InquiryResponse.class);

        return isOK(response);
    }
}
