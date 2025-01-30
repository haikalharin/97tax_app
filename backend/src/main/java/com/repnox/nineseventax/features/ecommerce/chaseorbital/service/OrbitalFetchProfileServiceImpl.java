package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalFetchProfileService;
import com.repnox.nineseventax.features.payment.request.OrbitalRequest;
import com.repnox.nineseventax.features.payment.response.CreatePaymentResponse;
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
public class OrbitalFetchProfileServiceImpl extends BaseOrbital implements OrbitalFetchProfileService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public ProfileResponse fetch(String version, String bin, String customerRefNum) throws Exception {
        log.debug("Fetching profile for reference {}", customerRefNum);
        HttpEntity entity = new HttpEntity(headers());

        ResponseEntity<ProfileResponse> response = restTemplate.exchange(
                constructFetchingProfileUri(profileUri(), version, bin, customerRefNum),
                HttpMethod.GET, entity, ProfileResponse.class);

        constructResponse(response, null);
        hitFailover(version, bin, customerRefNum);

        log.debug("Response Fetch Profile : {}", mapper.writeValueAsString(response));

        return response.getBody();
    }

    public ProfileResponse fetchProfile(String version, String bin, String customerRefNum) throws Exception {
        /*
        HttpEntity entity = new HttpEntity(headers());
        String fetchProfileUri = constructFetchingProfileUri(profileUri(), version, bin, customerRefNum);

        log.debug("Request Fetch Profile Uri : " + fetchProfileUri);
        log.debug("Request Fetch Profile : " + mapper.writeValueAsString(entity));

        ResponseEntity<ProfileResponse> response = restTemplate.exchange(fetchProfileUri,
                HttpMethod.GET, entity, ProfileResponse.class);

        constructResponse(response, null);
        hitFailover(version, bin, customerRefNum);

        log.debug("Response Fetch Profile : {}", mapper.writeValueAsString(response));

        return response.getBody();*/

        String fetchProfileUri = constructFetchingProfileUri(profileUri(), version, bin, customerRefNum);
        log.debug("Fetch profile request {}", fetchProfileUri);
        HttpResponse<String> response = getUnirestHandler(fetchProfileUri).asString();
        log.debug("Fetch profile response = " + response.getBody());

        Gson gson = new Gson();
        ProfileResponse result = gson.fromJson(response.getBody(), ProfileResponse.class);
        return result;
    }


    @Override
    boolean hitFailover(OrbitalRequest request) {
        return true;
    }

    boolean hitFailover(String version, String bin, String customerRefNum) {
        HttpEntity entity = new HttpEntity(headers());

        ResponseEntity<ProfileResponse> response = restTemplate.exchange(
                constructFetchingProfileUri(profileFailoverUri(), version, bin, customerRefNum),
                HttpMethod.GET, entity, ProfileResponse.class);

        return isOK(response);
    }

    private String constructFetchingProfileUri(String baseUri, String version, String bin, String customerRefNum) {
        return new StringBuilder().append(baseUri)
                .append("version/").append(version)
                .append("/bin/").append(bin)
                .append("/customerrefnum/").append(customerRefNum)
                .toString();
    }
}
