package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalSettleService;
import com.repnox.nineseventax.features.payment.request.OrbitalRequest;
import com.repnox.nineseventax.features.payment.request.SettleRequest;
import com.repnox.nineseventax.features.payment.response.SettleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OrbitalSettleServiceImpl extends BaseOrbital implements OrbitalSettleService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public SettleResponse settle(SettleRequest request) throws Exception {
        log.debug("Start settling transaction for {}", request.getMerchant().getBin());
        HttpEntity<SettleRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<SettleResponse> response = restTemplate.exchange(
                settledUri(), HttpMethod.POST, entity, SettleResponse.class);

        constructResponse(response, request);
        log.debug("Settle response body : {}",  mapper.writeValueAsString(response.getBody()));

        return response.getBody();
    }

    @Override
    boolean hitFailover(OrbitalRequest request) {
        HttpEntity<SettleRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<SettleResponse> response = restTemplate.exchange(
                settledFailoverUri(), HttpMethod.POST, entity, SettleResponse.class);

        return isOK(response);
    }
}
