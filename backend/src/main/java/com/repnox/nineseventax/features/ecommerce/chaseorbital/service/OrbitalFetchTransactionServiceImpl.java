package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalFetchTransactionService;
import com.repnox.nineseventax.features.payment.request.OrbitalRequest;
import com.repnox.nineseventax.features.payment.response.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OrbitalFetchTransactionServiceImpl extends BaseOrbital implements OrbitalFetchTransactionService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public TransactionResponse fetch(String version, String bin, String terminalId, String txrefnum) throws Exception {
        log.debug("Fetching transaction for version {}, bin {}, terminalId {} and reference {}", version, bin, terminalId, txrefnum);
        HttpEntity entity = new HttpEntity(headers());

        String statusUri = transactionStatusUri();
        String url = constructFetchingTransactionUri(statusUri, version, bin, terminalId, txrefnum);
        ResponseEntity<TransactionResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET, entity, TransactionResponse.class);

        constructResponse(response, null);
        hitFailover(version, bin, terminalId, txrefnum);

        log.debug("Response Transaction : {}", mapper.writeValueAsString(response));

        return response.getBody();
    }


    public TransactionResponse fetchProfile(String version, String bin, String customerRefNumber) throws Exception {
        log.debug("Fetching transaction for version {}, bin {}, terminalId {} and reference {}", version, bin, terminalId, customerRefNumber);
        HttpEntity entity = new HttpEntity(headers());

        ResponseEntity<TransactionResponse> response = restTemplate.exchange(
                constructFetchingProfileUri(getOrbitalProfileUrl(), version, bin, terminalId, customerRefNumber),
                HttpMethod.GET, entity, TransactionResponse.class);

        constructResponse(response, null);
        hitFailover(version, bin, terminalId, customerRefNumber);

        log.debug("Response Transaction : {}", mapper.writeValueAsString(response));

        return response.getBody();
    }


    @Override
    boolean hitFailover(OrbitalRequest request) {
        return true;
    }

    boolean hitFailover(String version, String bin, String terminalId, String txrefnum) {
        HttpEntity entity = new HttpEntity(headers());

        ResponseEntity<TransactionResponse> response = restTemplate.exchange(
                constructFetchingTransactionUri(getOrbitalProfileUrl(), version, bin, terminalId, txrefnum),
                HttpMethod.GET, entity, TransactionResponse.class);

        return isOK(response);
    }

    private String constructFetchingTransactionUri(String baseUri, String version, String bin, String terminalId, String txrefnum) {
        return new StringBuilder().append(baseUri)
                .append("version/").append(version)
                .append("/txrefnum/").append(txrefnum)
                .append("/bin/").append(bin)
                .append("/terminalid/").append(terminalId)
                .toString();
    }

    private String constructFetchingProfileUri(String baseUri, String version, String bin, String terminalId, String txrefnum) {
        return new StringBuilder().append(baseUri)
                .append("version/").append(version)
                .append("/txrefnum/").append(txrefnum)
                .append("/bin/").append(bin)
                .append("/terminalid/").append(terminalId)
                .toString();
    }
}
