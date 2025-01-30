package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalDeleteProfileService;
import com.repnox.nineseventax.features.payment.request.OrbitalRequest;
import com.repnox.nineseventax.features.payment.request.ProfileRequest;
import com.repnox.nineseventax.features.payment.response.ProfileResponse;
import kong.unirest.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OrbitalDeleteProfileServiceImpl extends BaseOrbital implements OrbitalDeleteProfileService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public ProfileResponse delete(ProfileRequest request) throws Exception {
        /*
        HttpEntity<ProfileRequest> entity = new HttpEntity(request, headers());

        log.debug("Request Delete Profile : {}", mapper.writeValueAsString(request));

        ResponseEntity<ProfileResponse> response = restTemplate.exchange(
                profileUri(), HttpMethod.DELETE, entity, ProfileResponse.class);

        constructResponse(response, request);

        log.debug("Response Delete Profile : {}", mapper.writeValueAsString(response.getBody()));

        return response.getBody();
        */

        log.debug("Request Delete Profile : {}", mapper.writeValueAsString(request));
        HttpResponse<String> response = deleteUnirestHandler(profileUri()).body(mapper.writeValueAsString(request))
                .asString();
        log.debug("Response Delete Profile = " + response.getBody());

        Gson gson = new Gson();
        ProfileResponse result = gson.fromJson(response.getBody(), ProfileResponse.class);

        return result;
    }

    @Override
    boolean hitFailover(OrbitalRequest request) {
        HttpEntity<ProfileRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<ProfileResponse> response = restTemplate.exchange(
                profileFailoverUri(), HttpMethod.DELETE, entity, ProfileResponse.class);

        return isOK(response);
    }
}
