package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.ein.EinRecordRepo;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.payment.request.OrbitalRequest;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

public abstract class BaseOrbital {

    @Value("${orbital.uri}")
    private String baseUri;

    @Value("${orbital.uri.failover}")
    private String baseFailoverUri;

    @Value("${orbital.uri.createPayment}")
    private String createPaymentUri;

    @Value("${orbital.uri.capturePayment}")
    private String capturePaymentUri;

    @Value("${orbital.uri.refund}")
    private String refundUri;

    @Value("${orbital.uri.profile}")
    private String profileUri;

    @Value("${orbital.uri.inquiry}")
    private String inquiryUri;

    @Value("${orbital.uri.settle}")
    private String settledUri;

    @Value("${orbital.uri.reversal}")
    private String reversalUri;

    @Value("${orbital.uri.transactionstatus}")
    private String transactionStatusUri;

    @Value("${orbital.username}")
    private String orbitalUsername;

    @Value("${orbital.password}")
    private String orbitalPassword;

    @Value("${orbital.merchantId}")
    private String orbitalMerchantId;

    @Value("${orbital.api.version}")
    protected String version;

    @Value("${orbital.bin}")
    protected String bin;

    @Value("${orbital.terminalId}")
    protected String terminalId;

    @Autowired
    protected OrderRecordRepo orderRecordRepo;

    @Autowired
    protected PenaltyRecordRepo penaltyRecordRepo;

    @Autowired
    protected EinRecordRepo einRecordRepo;

    @Autowired
    protected MailjetSender mailjetSender;

    protected StringBuilder baseUri() {
        return new StringBuilder().append(baseUri);
    }

    protected StringBuilder baseFailoverUri() {
        return new StringBuilder().append(baseFailoverUri);
    }

    protected String refundUri() {
        return baseUri().append(refundUri).toString();
    }

    protected String refundFailoverUri() {
        return baseFailoverUri().append(refundUri).toString();
    }

    protected String reversalUri() {
        return baseUri().append(reversalUri).toString();
    }

    protected String reversalFailoverUri() {
        return baseFailoverUri().append(reversalUri).toString();
    }

    protected String createPaymentUri() {
        return baseUri().append(createPaymentUri).toString();
    }

    protected String createPaymentFailoverUri() {
        return baseFailoverUri().append(createPaymentUri).toString();
    }

    protected String capturePaymentUri() {
        return baseUri().append(capturePaymentUri).toString();
    }

    protected String capturePaymentFailoverUri() {
        return baseFailoverUri().append(capturePaymentUri).toString();
    }

    protected String profileUri() {
        return baseUri().append(profileUri).toString();
    }

    protected String profileFailoverUri() {
        return baseFailoverUri().append(profileUri).toString();
    }

    protected String settledUri() {
        return baseUri().append(settledUri).toString();
    }

    protected String settledFailoverUri() {
        return baseFailoverUri().append(settledUri).toString();
    }

    protected String inquiryUri() {
        return baseUri().append(inquiryUri).toString();
    }

    protected String inquiryFailoverUri() {
        return baseFailoverUri().append(inquiryUri).toString();
    }

    protected String transactionStatusUri() {
        return baseUri().append(transactionStatusUri).toString();
    }

    protected String getOrbitalProfileUrl() {
        return baseUri().append(profileUri).toString();
    }
    protected String transactionStatusFailoverUri() {
        return baseFailoverUri().append(transactionStatusUri).toString();
    }

    protected HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("OrbitalConnectionUsername", orbitalUsername);
        headers.set("OrbitalConnectionPassword", orbitalPassword);
        headers.set("MerchantID", orbitalMerchantId);

        return headers;
    }

    protected HttpRequestWithBody postUnirestHandler(String uri) {
        return Unirest.post(uri)
                .header("Content-Type", "application/json")
                .header("OrbitalConnectionUsername", orbitalUsername)
                .header("OrbitalConnectionPassword", orbitalPassword)
                .header("MerchantID", orbitalMerchantId);
    }

    protected GetRequest getUnirestHandler(String uri) {
        return Unirest.get(uri)
                .header("Content-Type", "application/json")
                .header("OrbitalConnectionUsername", orbitalUsername)
                .header("OrbitalConnectionPassword", orbitalPassword)
                .header("MerchantID", orbitalMerchantId);
    }

    protected HttpRequestWithBody deleteUnirestHandler(String uri) {
        return Unirest.delete(uri)
                .header("Content-Type", "application/json")
                .header("OrbitalConnectionUsername", orbitalUsername)
                .header("OrbitalConnectionPassword", orbitalPassword)
                .header("MerchantID", orbitalMerchantId);
    }

    abstract boolean hitFailover(OrbitalRequest request);

    protected HttpStatus constructResponse(ResponseEntity response, OrbitalRequest request) {
        if (response.getStatusCodeValue() == 400) {
            throw new HttpClientErrorException(response.getStatusCode(),
                    "Invalid Request : " + printRequest(request));
        } else if (response.getStatusCodeValue() == 403) {
            throw new HttpClientErrorException(response.getStatusCode(),
                    "Forbidden. SSL Connection Required.");
        } else if (response.getStatusCodeValue() == 412) {
            throw new HttpClientErrorException(response.getStatusCode(),
                    "Precondition Failed: Security Information is missing.");
        } else if (response.getStatusCodeValue() == 500) {
            throw new HttpClientErrorException(response.getStatusCode(),
                    "Internal Server Error.");
        } else if (response.getStatusCodeValue() == 502) {
            throw new HttpClientErrorException(response.getStatusCode(),
                    "Connection Error.");
        } else {
            checkFailover(request, response);
        }

        return HttpStatus.OK;
    }

    private String printRequest(OrbitalRequest request) {
        if (Objects.nonNull(request)) {
            return request.toString();
        }

        return StringUtils.EMPTY;
    }

    private void checkFailover(OrbitalRequest request, ResponseEntity response) {
        if (response.getStatusCodeValue() == 503) {
            boolean isSuccess = hitFailover(request);
            if (!isSuccess) {
                throw new HttpClientErrorException(response.getStatusCode(),
                        "Server Unavailable: Please Try Again Later.");
            }
        } else if (response.getStatusCodeValue() == 408) {
            boolean isSuccess = hitFailover(request);
            if (!isSuccess) {
                throw new HttpClientErrorException(response.getStatusCode(),
                        "Request timeout.");
            }
        }
    }

    protected boolean isOK(ResponseEntity response) {
        if (response.getStatusCodeValue() == 200) {
            return true;
        }

        return false;
    }

}
