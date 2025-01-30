package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.repnox.nineseventax.features.ecommerce.OrderInfo;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.ChaseOrbitalGatewayObject;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCreatePaymentService;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.payment.request.*;
import com.repnox.nineseventax.features.payment.response.CreatePaymentResponse;
import com.repnox.nineseventax.features.payment.response.ReversalResponse;
import com.repnox.nineseventax.features.utils.OrderUtil;
import kong.unirest.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static com.repnox.nineseventax.features.utils.TaxConstants.*;

@Service
@Slf4j
public class OrbitalCreatePaymentServiceImpl extends BaseOrbital implements OrbitalCreatePaymentService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Override
    // if the transaction has been settled already, then using function to reverse the payment processed
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) throws Exception {
//        HttpEntity<CreatePaymentRequest> entity = new HttpEntity(request, headers());
//
//        log.debug("Create payment request : {}", mapper.writeValueAsString(request));
//
//        ResponseEntity<CreatePaymentResponse> response = restTemplate.exchange(
//                createPaymentUri(), HttpMethod.POST, entity, CreatePaymentResponse.class);
//
//        constructResponse(response, request);
//
//        log.debug("Create payment response : {}", mapper.writeValueAsString(response.getBody()));
//
//        return response.getBody();

        Gson gson = new Gson();
        try {
            HttpResponse<String> response = postUnirestHandler(createPaymentUri()).body(mapper.writeValueAsString(request))
                    .asString();
            log.debug("create payment response = " + response.getBody());
            return gson.fromJson(response.getBody(), CreatePaymentResponse.class);
        } catch (Exception ex) {
            log.error("Unexpected error occurred, failover will be used", ex);

            HttpResponse<String> response = postUnirestHandler(createPaymentFailoverUri()).body(mapper.writeValueAsString(request))
                    .asString();
            log.debug("create payment response = " + response.getBody());
            return gson.fromJson(response.getBody(), CreatePaymentResponse.class);
        }
    }

    @Override
    public String createPayment4Logging(CreatePaymentRequest request) throws Exception {
//        HttpEntity<CreatePaymentRequest> entity = new HttpEntity(request, headers());
//
        log.debug("Create payment request : {}", mapper.writeValueAsString(request));
//
//        ResponseEntity<CreatePaymentResponse> response = restTemplate.exchange(
//                createPaymentUri(), HttpMethod.POST, entity, CreatePaymentResponse.class);
//
//        constructResponse(response, request);
//
//        log.debug("Create payment response : {}", mapper.writeValueAsString(response.getBody()));
//
//        return response.getBody();

        // TODO: Need to utilize failover URI in general
        try {
            HttpResponse<String> response = postUnirestHandler(createPaymentUri()).body(mapper.writeValueAsString(request))
                    .asString();
            log.debug("create payment response = " + response.getBody());
            return response.getBody();
        } catch (Exception ex) {
            log.error("Unexpected error occurred, failover will be used", ex);

            HttpResponse<String> response = postUnirestHandler(createPaymentFailoverUri()).body(mapper.writeValueAsString(request))
                    .asString();
            log.debug("create payment response = " + response.getBody());
            return response.getBody();
        }
    }

    @Override
    public ReversalResponse paymentRequestAfterSettled(CreatePaymentRequestAfterSettled paymentRequest) throws Exception {
        HttpEntity<Object> entity = new HttpEntity<>(paymentRequest, super.headers());
        log.info("sending payment refund request after the request has already been settled.");

        log.debug("Create payment request after order settled: {}", mapper.writeValueAsString(paymentRequest));

        try {
            ResponseEntity<ReversalResponse> response = this.restTemplate.exchange(super.createPaymentUri(), HttpMethod.POST, entity, ReversalResponse.class);

            ReversalResponse responseString = response.getBody();
            System.out.println(response.getStatusCode());
            return responseString;
        } catch (HttpClientErrorException httpClientErrorException) {
            System.out.println(httpClientErrorException);
            httpClientErrorException.printStackTrace();
            throw new Exception();
        } catch(RestClientException restClientException) {
            System.out.println(restClientException);
            restClientException.printStackTrace();
            throw new Exception();
        } catch (Exception exception) {
            System.out.println(exception);
            exception.printStackTrace();
            throw new Exception();
        }

    }


    @Override
    public CreatePaymentRequest buildPaymentRequest(OrderInfo orderInfo) throws Exception {
        log.debug("Prepared create payment request from order info ", mapper.writeValueAsString(orderInfo));

        CardRequest cardRequest = new CardRequest();
        String cardNumber = orderInfo.getCardNumber();
        cardRequest.setCardBrand(CardType.detect(cardNumber).toString());
        cardRequest.setCcAccountNum(cardNumber);
        cardRequest.setCcExp(orderInfo.getCardExpYear() + orderInfo.getCardExpMonth());

//        cardRequest.setCardType(CardType.detect(cardNumber).toString());

        PaymentInstrumentRequest paymentInstrumentRequest = new PaymentInstrumentRequest();
        paymentInstrumentRequest.setCard(cardRequest);
        paymentInstrumentRequest.setCustomerAccountType("CC");

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderID(orderInfo.getOrderNumber());
        orderRequest.setAmount(orderInfo.getAmount());
        orderRequest.setIndustryType("EC");
        orderRequest.setComments(this.getPaymentPlan(orderInfo));
        AdditionalAuthInfoRequest additionalAuthInfoRequest = new AdditionalAuthInfoRequest();
        additionalAuthInfoRequest.setAuthenticationECIInd(orderInfo.getEciNumber());

        CryptogramRequest cryptogramRequest = new CryptogramRequest();
        cryptogramRequest.setDigitalTokenCryptogram(orderInfo.getAuthCavv());
        cryptogramRequest.setVerifyByVisaXID("");
//        if ("MASTERCARD".equals(orderInfo.getCardBrand())) {
//            cryptogramRequest.setMcSecureCodeAAV(orderInfo.getAuthCavv());
//        } else if ("VISA".equals(orderInfo.getCardBrand())) {
//            cryptogramRequest.setVerifyByVisaCAVV(orderInfo.getAuthCavv());
//        }

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest(version, "AC", bin, terminalId);
        createPaymentRequest.setPaymentInstrument(paymentInstrumentRequest);
        createPaymentRequest.setOrder(orderRequest);
        createPaymentRequest.setProfile(buildProfile(orderInfo));
        createPaymentRequest.setAdditionalAuthInfo(additionalAuthInfoRequest);
        createPaymentRequest.setAvsBilling(buildProfileAvs(orderInfo));
        createPaymentRequest.setCardholderVerification(buildCardholderVerification(orderInfo));
        createPaymentRequest.setCryptogram(cryptogramRequest);

        return createPaymentRequest;
    }

    @Override
    public CreatePaymentRequest buildEinPaymentRequest(EinOrder orderInfo) throws Exception {
        log.debug("Prepared create payment request from order info ", mapper.writeValueAsString(orderInfo));

        CardRequest cardRequest = new CardRequest();
        String cardNumber = orderInfo.getCard_number();
        cardRequest.setCcAccountNum(cardNumber);
        cardRequest.setCcExp(orderInfo.getCardExpYear() + orderInfo.getCardExpMonth());

        cardRequest.setCardBrand(CardType.detect(cardNumber).toString());
        EuddRequest euddRequest = new EuddRequest();
        euddRequest.setEuddIBAN(cardNumber);
        PaymentInstrumentRequest paymentInstrumentRequest = new PaymentInstrumentRequest();
        paymentInstrumentRequest.setCard(cardRequest);
        paymentInstrumentRequest.setEudd(euddRequest);
        paymentInstrumentRequest.setCustomerAccountType("CC");

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderID(orderInfo.getId().toString());
        orderRequest.setAmount(orderInfo.getAmount().toString());
        orderRequest.setIndustryType("EC");

        OrderInfo stubOrderInfo = new OrderInfo();
        stubOrderInfo.setEmail(orderInfo.getEmail());
        stubOrderInfo.setOrderNumber(orderInfo.getId().toString());
        stubOrderInfo.setBillingFirstName(orderInfo.getFirst_name());
        stubOrderInfo.setBillingLastName(orderInfo.getLast_name());
        stubOrderInfo.setBillingAddress1(orderInfo.getAddress() + (StringUtils.isBlank(orderInfo.getApt_suite()) ? "" : " " + orderInfo.getApt_suite()));
        stubOrderInfo.setBillingCity(orderInfo.getCity());
        stubOrderInfo.setBillingState(orderInfo.getState());
        stubOrderInfo.setBillingPostalCode(orderInfo.getZip_code());
        stubOrderInfo.setBillingPhone(orderInfo.getPhone_number());

        stubOrderInfo.setShippingFirstName(orderInfo.getFirst_name());
        stubOrderInfo.setShippingLastName(orderInfo.getLast_name());
        stubOrderInfo.setShippingAddress1(orderInfo.getAddress() + (StringUtils.isBlank(orderInfo.getApt_suite()) ? "" : " " + orderInfo.getApt_suite()));
        stubOrderInfo.setShippingCity(orderInfo.getCity());
        stubOrderInfo.setShippingState(orderInfo.getState());
        stubOrderInfo.setShippingPostalCode(orderInfo.getZip_code());

        orderRequest.setComments("EIN Application");

        AdditionalAuthInfoRequest additionalAuthInfoRequest = new AdditionalAuthInfoRequest();

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest(version, "AC", bin, terminalId);
        createPaymentRequest.setPaymentInstrument(paymentInstrumentRequest);
        createPaymentRequest.setOrder(orderRequest);
        createPaymentRequest.setProfile(buildProfile(stubOrderInfo));
        createPaymentRequest.setAdditionalAuthInfo(additionalAuthInfoRequest);
        createPaymentRequest.setAvsBilling(buildProfileAvs(stubOrderInfo));
        createPaymentRequest.setCardholderVerification(buildCardholderVerification(stubOrderInfo));

        return createPaymentRequest;
    }

    private ProfileDetailRequest buildProfile(OrderInfo orderInfo) {
        ProfileDetailRequest request = new ProfileDetailRequest();
        request.setCustomerRefNum(orderInfo.getOrderNumber());
        String name = getCustomerName(orderInfo);
        request.setCustomerName(name);


        request.setCustomerPhone(orderInfo.getBillingPhone() != null ? orderInfo.getBillingPhone() : "");
        request.setAddProfileFromOrder("S");
        request.setProfileOrderOverideInd("NO");
        if(orderInfo.getEmail()!= null) {
            request.setCustomerEmail(orderInfo.getEmail());
        }
        if(orderInfo.getBillingPostalCode() != null) {
            request.setCustomerZIP(orderInfo.getBillingPostalCode());
        }
        else if(orderInfo.getShippingPostalCode() != null) {
            request.setCustomerZIP(orderInfo.getShippingPostalCode());
        }
        return request;
    }

    private String getCustomerName(OrderInfo orderInfo) {
        if(orderInfo.getBillingFirstName() != null && orderInfo.getBillingLastName() != null) {
            return orderInfo.getBillingFirstName() + " " + orderInfo.getBillingLastName();
        } else if (orderInfo.getShippingFirstName() != null && orderInfo.getShippingLastName() != null) {
            return orderInfo.getShippingFirstName() +" "+orderInfo.getShippingLastName();
        }
        else {
            return "";
        }
    }

    private AvsBilling buildProfileAvs(OrderInfo orderInfo) {
        AvsBilling request = new AvsBilling();
        // Max length for AvsAddress1 is 30, max length for AvsCity is 20
        request.setAvsAddress1(
                StringUtils.substring(
                        StringUtils.trimToEmpty(orderInfo.getBillingAddress1()), 0, 30)
        );
        request.setAvsAddress2(StringUtils.trimToEmpty(orderInfo.getBillingAddress2()));
        request.setAvsCity(
                StringUtils.substring(
                        StringUtils.trimToEmpty(orderInfo.getBillingCity()), 0, 20)
        );
        request.setAvsState(StringUtils.trimToEmpty(orderInfo.getBillingState()));
        if(orderInfo.getBillingPostalCode() != null) {
            request.setAvsZip(orderInfo.getBillingPostalCode());
        }
        else if(orderInfo.getShippingPostalCode() != null) {
            request.setAvsZip(orderInfo.getShippingPostalCode());
        }
        return request;
    }

    private CardholderVerificationRequest buildCardholderVerification(OrderInfo orderInfo) {
        CardholderVerificationRequest request = new CardholderVerificationRequest();
        request.setCcCardVerifyNum(orderInfo.getCardCvc());

        return request;
    }

    private String getPaymentPlan(OrderInfo info) {
        String planName = "";
        if(info.getIsGeorgia() != null && info.getIsGeorgia()) {
            planName = GEORGIA_PAYMENTPLAN;
        }
        else if(info.getIsCalifornia() != null && info.getIsCalifornia()) {
            planName = CALIFORNIA_PAYMENTPLAN;
        }
        else if(info.getIsIllinois() != null && info.getIsIllinois()) {
            planName = ILLINOIS_PAYMENTPLAN;
        }
        else if(info.getIsNewJersey() != null && info.getIsNewJersey()) {
            planName = NEW_JERSEY_PAYMENTPLAN;
        }
        else if(info.getEin() != null && !info.getEin().equalsIgnoreCase("")) {
            planName = EIN_IRS_NUMBER_PLAN;
        }
        else if(info.getIsMichigan()!= null) {
            if(info.getIsMichigan()) {
                planName = MICHIGAN_PAYMENTPLAN;
            }
        } else if(info.getIsPenaltyWaiver() != null && info.getIsPenaltyWaiver()) {
            planName = PENALTY_WAIVER_PAYMENTPLAN;
        }
        else {
            planName = IRS_PAYMENTPLAN;
        }

        return planName;
    }

    /**
     * Implied decimal, including those currencies that are a zero exponent.
     * For example, both $100.00 (an exponent of 2) and ¥100 (an exponent of 0) should be sent as amount=10000.
     * Note:  Currency and currency code are not passed in the request. It is implied by the Currency setup for the Merchant ID.
     *
     * https://developer.jpmorgan.com/products/orbital-api/guides/data-elements
     */
    private String constructAmount(String amount) {
        try {
            BigDecimal amountBd = new BigDecimal(amount).multiply(BigDecimal.valueOf(100));
            return amountBd.toString();
        } catch (Exception e) {
            log.error("ERROR: occurred while construct amount", e);
            throw e;
        }
    }

    @Override
    boolean hitFailover(OrbitalRequest request) {
        HttpEntity<CreatePaymentRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<CreatePaymentResponse> response = restTemplate.exchange(
                createPaymentFailoverUri(), HttpMethod.POST, entity, CreatePaymentResponse.class);

        return isOK(response);
    }

}
