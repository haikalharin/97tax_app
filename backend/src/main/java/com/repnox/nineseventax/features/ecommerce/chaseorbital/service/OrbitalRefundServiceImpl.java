package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalRefundService;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.payment.request.*;
import com.repnox.nineseventax.features.payment.response.ProfileResponse;
import com.repnox.nineseventax.features.payment.response.RefundResponse;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.utils.DefaultResponse;
import kong.unirest.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;

@Service
@Slf4j
public class OrbitalRefundServiceImpl extends BaseOrbital implements OrbitalRefundService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    OrbitalFetchProfileServiceImpl orbitalFetchProfileService;

    @Override
    public RefundResponse refundPayment(RefundRequest request) throws Exception {
        log.debug("Request refund : {}", mapper.writeValueAsString(request));
        /*
        try {
            log.debug("Start refund for order id {}", request.getOrder().getOrderID());
            String refundUri = refundUri();
            HttpEntity<RefundRequest> entity = new HttpEntity(request, headers());

            log.debug("Request refund Uri: {}", refundUri);
            log.debug("Request refund : {}", mapper.writeValueAsString(request));

            ResponseEntity<RefundResponse> response = restTemplate.exchange(refundUri, HttpMethod.POST, entity, RefundResponse.class);
            constructResponse(response, request);
            log.debug("Response refund : {}", mapper.writeValueAsString(response.getBody()));

            return response.getBody();
        } catch (Exception e) {
            log.error("An error has occurred ", e);
        }
        throw new Exception("An error has occurred");
        */
        HttpResponse<String> response = postUnirestHandler(refundUri()).body(mapper.writeValueAsString(request))
                .asString();
        log.debug("Refund response = " + response.getBody());

        Gson gson = new Gson();
        RefundResponse result = gson.fromJson(response.getBody(), RefundResponse.class);
        return result;
    }

    @Override
    public ResponseEntity refundPayment(String authTransactionId, String refundAmount, String status, boolean isSendEmail) throws Exception {
        return refundPayment(authTransactionId, status, isSendEmail);
    }

    @Override
    @Transactional
    public ResponseEntity refundPayment(String authTransactionId, String status, boolean isSendEmail) throws Exception {
        ProfileResponse profileResponse = null;
        OrderRequest orderRequest = null;
        OrderRecord orderRecord = orderRecordRepo.findByAuthorizeTransactionId(authTransactionId);
        PenaltyOrder penaltyOrder = penaltyRecordRepo.findByAuthorizeTransactionId(authTransactionId);
        EinOrder einOrder = einRecordRepo.findByAuthorizeTransactionId(authTransactionId);
        String orderId = "";
        PaymentInstrumentRequest paymentInstrument = new PaymentInstrumentRequest();

        if(orderRecord != null) {
            profileResponse = orbitalFetchProfileService.fetchProfile(version, bin, orderRecord.getOrderId());
            orderRequest = this.mapOrderRequest(orderRecord);
            orderId = orderRecord.getOrderNum().toString();
        } else if(penaltyOrder != null){
            profileResponse = orbitalFetchProfileService.fetchProfile(version, bin, penaltyOrder.getOrderId());
            orderRequest = this.mapOrderRequest4Penalty(penaltyOrder);
            orderId = penaltyOrder.getId().toString();
        } else if(einOrder != null) {
            profileResponse = orbitalFetchProfileService.fetchProfile(version, bin, einOrder.getId().toString());
            orderRequest = this.mapOrderRequest4Ein(einOrder);
            orderId = einOrder.getId().toString();
        }

        paymentInstrument.setUseProfile(this.mapUseProfile(profileResponse)); /* User Profile approach*/
        RefundResponse refundResponse = refundPayment(this.mapRefundRequest(orderRequest, paymentInstrument)); /*Calling refund API*/

        String procStatus = refundResponse.getProcStatus();

        if("0".equals(procStatus)) {
            log.info("Transaction has refunded now: " + orderId);

            if (orderRecord != null) {
                orderRecord.setStatus(status);
                orderRecord.setRefundDate(new Date(System.currentTimeMillis()));
                orderRecord.setRefundAuthTransId(refundResponse.getOrder().getTxRefNum());
                orderRecordRepo.save(orderRecord);
            } else if (penaltyOrder != null) {
                penaltyOrder.setStatus(status);
                penaltyOrder.setRefundDate(new Date(System.currentTimeMillis()));
                penaltyOrder.setRefundAuthTransId(refundResponse.getOrder().getTxRefNum());
                penaltyRecordRepo.save(penaltyOrder);
            } else if (einOrder != null) {
                einOrder.setStatus(status);
                einOrder.setRefundDate(new Date(System.currentTimeMillis()));
                einOrder.setRefundAuthTransId(refundResponse.getOrder().getTxRefNum());
                einRecordRepo.save(einOrder);
            }
            if (isSendEmail) {
                try {
                    if(orderRecord != null) {
                        mailjetSender.sendCancelledEmail(orderRecord);
                    } else if(penaltyOrder != null){
                        mailjetSender.sendPenaltyOrderCancelledEmail(penaltyOrder);
                    } else if(einOrder != null) {
                        mailjetSender.sendEinOrderCancelledEmail(einOrder);
                    }
                } catch (IOException e) {
                    log.error("ERROR: Unable to send email", e);
                }
            }

            return new ResponseEntity(new DefaultResponse("Success","Order: "+orderId+" has refunded","S001"), HttpStatus.OK);
        }
        else {
            return new ResponseEntity(new DefaultResponse("Failure","Order: "+orderId+" has failed for refund request","F001"), HttpStatus.OK);
        }

//        StatusResponse statusResponse = refundResponse.getOrder().getStatus();
//        if(statusResponse.getProcStatus().equalsIgnoreCase("0")
//                && statusResponse.getProcStatusMessage().equalsIgnoreCase("Approved")) {
//            return new ResponseEntity(new DefaultResponse("Success","Order: "+orderId+" has refunded","S001"), HttpStatus.OK);
//        }
//        else {
//            return new ResponseEntity(new DefaultResponse("Failure","Order: "+orderId+" has failed for refund request","F001"), HttpStatus.OK);
//        }
    }

    public OrderRequest mapOrderRequest(OrderRecord orderRecord) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderID(orderRecord.getOrderId());
        orderRequest.setIndustryType("EC");
        orderRequest.setAmount(orderRecord.getAmount());
        orderRequest.setComments("Order has refund now");
        return orderRequest;
    }

    public OrderRequest mapOrderRequest4Penalty(PenaltyOrder orderRecord) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderID(orderRecord.getOrderId());
        orderRequest.setIndustryType("EC");
        orderRequest.setAmount(orderRecord.getAmount());
        orderRequest.setComments("Penalty wavier order has refund now");
        return orderRequest;
    }

    public OrderRequest mapOrderRequest4Ein(EinOrder orderRecord) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderID(orderRecord.getId().toString());
        orderRequest.setIndustryType("EC");
        orderRequest.setAmount(orderRecord.getAmount().toString());
        orderRequest.setComments("Penalty wavier order has refund now");
        return orderRequest;
    }

    public UseProfile mapUseProfile(ProfileResponse profileResponse) {
        UseProfile profile = new UseProfile();
        profile.setUseCustomerRefNum(profileResponse.getProfile().getCustomerRefNum());
        return profile;
    }

    /**
     * Card number approach for PaymentInstrument
     * */
    public CardRequest mapCardRequest(ProfileResponse profileResponse) {
         CardRequest oldObj = profileResponse.getPaymentInstrument().getCard();
         CardRequest cardRequest = new CardRequest();
         cardRequest.setCcAccountNum(profileResponse.getProfile().getCustomerName());
         cardRequest.setCcExp(oldObj.getCcExp());
         return cardRequest;
    }

    public RefundRequest mapRefundRequest(OrderRequest orderRequest, PaymentInstrumentRequest paymentInstrument) {
        RefundRequest refundRequest = new RefundRequest(version, bin, terminalId);
        refundRequest.setOrder(orderRequest);
        refundRequest.setPaymentInstrument(paymentInstrument);
        return refundRequest;
    }

    @Override
    boolean hitFailover(OrbitalRequest request) {
/*
        HttpEntity<RefundRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<RefundResponse> response = restTemplate.exchange(
                refundFailoverUri(), HttpMethod.POST, entity, RefundResponse.class);

        return isOK(response);
*/
        try {
            log.debug("hitFailover request = " + mapper.writeValueAsString(request));
            HttpResponse<String> response = postUnirestHandler(refundUri()).body(mapper.writeValueAsString(request))
                    .asString();
            log.debug("hitFailover response = " + response.getBody());

            Gson gson = new Gson();
            RefundResponse result = gson.fromJson(response.getBody(), RefundResponse.class);

            if("0".equals(result.getProcStatus())) {
                return true;
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
