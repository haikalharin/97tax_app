package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalUpdateProfileService;
import com.repnox.nineseventax.features.payment.request.OrbitalRequest;
import com.repnox.nineseventax.features.payment.request.ProfileRequest;
import com.repnox.nineseventax.features.payment.response.ProfileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OrbitalUpdateProfileServiceImpl extends BaseOrbital implements OrbitalUpdateProfileService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ProfileResponse update(ProfileRequest request) throws Exception {
        log.debug("Start updating profile for {}", request.getProfile().getCustomerName());
        HttpEntity<ProfileRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<ProfileResponse> response = restTemplate.exchange(
                profileUri(), HttpMethod.PUT, entity, ProfileResponse.class);

        constructResponse(response, request);
        log.debug("Successfully updating profile for {}", request.getProfile().getCustomerName());
        log.debug("Update profile body : {}", response.getBody());

        return response.getBody();
    }

    @Override
    boolean hitFailover(OrbitalRequest request) {
        HttpEntity<ProfileRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<ProfileResponse> response = restTemplate.exchange(
                profileFailoverUri(), HttpMethod.PUT, entity, ProfileResponse.class);

        return isOK(response);
    }
}
