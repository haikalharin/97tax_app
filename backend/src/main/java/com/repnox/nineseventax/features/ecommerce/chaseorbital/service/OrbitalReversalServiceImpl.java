package com.repnox.nineseventax.features.ecommerce.chaseorbital.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.admin.request.VoidRequest;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCreatePaymentService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalReversalService;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.payment.request.*;
import com.repnox.nineseventax.features.payment.response.ProfileResponse;
import com.repnox.nineseventax.features.payment.response.ReversalResponse;
import com.repnox.nineseventax.features.payment.response.StatusResponse;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.utils.DefaultResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

@Service
@Slf4j
public class OrbitalReversalServiceImpl extends BaseOrbital implements OrbitalReversalService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private OrbitalFetchProfileServiceImpl orbitalFetchProfileService;

    @Autowired
    private OrbitalCreatePaymentService orbitalCreatePaymentService;

    @Override
    public ReversalResponse reverse(ReversalRequest request) throws Exception {
/*
        log.debug("Start reverse transaction for order id {}", request.getOrder().getOrderID());
        HttpEntity<ReversalRequest> entity = new HttpEntity(request, headers());

        String reversalUri = reversalUri();
        log.debug("Reversal Uri = " + reversalUri);
        log.debug("Reverse body : {}", mapper.writeValueAsString(entity));

        try {
            ResponseEntity<ReversalResponse> response = restTemplate.exchange(reversalUri, HttpMethod.POST, entity, ReversalResponse.class);
            constructResponse(response, request);
            log.debug("Successfully reverse order ID {} with amount {}",
                    request.getOrder().getOrderID(), request.getOrder().getAmount());
            log.debug("Reverse body : {}", mapper.writeValueAsString(response.getBody()));
            return response.getBody();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
 */

        try {
            log.debug("Start reverse transaction: " + mapper.writeValueAsString(request));
            HttpResponse<String> response = postUnirestHandler(this.reversalUri()).body(mapper.writeValueAsString(request))
                    .asString().ifFailure(error -> {
                        log.error("Oh No! Status" + error.getStatus());
                        if (error.getStatus() == 400) {
                            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                                    "Invalid Request : " + error.getBody());
                        }
                    });
            log.debug("Reversal response = " + response.getBody());

            Gson gson = new Gson();
            ReversalResponse result = gson.fromJson(response.getBody(), ReversalResponse.class);
            return result;
        } catch (RestClientException ex) {
            log.debug("Error For Orbital = " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

    @Override
    @Transactional
    public boolean reverse(String authTransactionId, String status, boolean isSendEmail, String username, VoidRequest voidRequest) throws Exception {

        ReversalRequest reversalRequest = new ReversalRequest(version, bin, terminalId);
        OrderRecord orderRecord = orderRecordRepo.findByAuthorizeTransactionId(authTransactionId);
        PenaltyOrder penaltyOrder = penaltyRecordRepo.findByAuthorizeTransactionId(authTransactionId);
        EinOrder einOrder = einRecordRepo.findByAuthorizeTransactionId(authTransactionId);
        ReversalResponse reversalResponse = null;

        if (orderRecord != null) {
            reversalRequest.setOrder(mapOrderRecord(orderRecord));
        } else if (penaltyOrder != null) {
            reversalRequest.setOrder(mapOrderRecord4Penalty(penaltyOrder));
        } else if (einOrder != null) {
            reversalRequest.setOrder(mapOrderRecord4Ein(einOrder));
        }

        try {
            reversalResponse = reverse(reversalRequest);
        } catch (HttpClientErrorException ex) {
            ex.printStackTrace();
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String requestOrderId = "";
                String requestAmount = "";

                if (orderRecord != null) {
                    requestOrderId = orderRecord.getOrderId();
                    requestAmount = orderRecord.getAmount();
                } else if (penaltyOrder != null) {
                    requestOrderId = penaltyOrder.getOrderId();
                    requestAmount = penaltyOrder.getAmount();
                } else if (einOrder != null) {
                    requestOrderId = einOrder.getOrderId();
                    requestAmount = einOrder.getAmount().toString();
                }

                try {
                    reversalResponse = this.orbitalCreatePaymentService.paymentRequestAfterSettled(
                            CreatePaymentRequestAfterSettled.builder()
                                    .version(version)
                                    .transType("R")
                                    .merchant(MerchantRequest.builder()
                                            .bin(bin)
                                            .terminalID(terminalId)
                                            .build()
                                    )
                                    .order(CreatePaymentRequestAfterSettled.Order.builder()
                                            .orderId(requestOrderId)
                                            .comments("")
                                            .industryType("EC")
                                            .amount(requestAmount)
                                            .build()
                                    )
                                    .paymentInstrument(CreatePaymentRequestAfterSettled.PaymentInstrument.builder()
                                            .useProfile(new UseProfile(requestOrderId))
                                            .build()
                                    )
                                    .build()
                    );
                } catch (Exception exception) {
                    if (orderRecord != null) {
                        updateOrderDetails(orderRecord, voidRequest, true);
                    } else if (penaltyOrder != null) {
                        updatePenaltyOrderDetails(penaltyOrder, voidRequest, true);
                    } else if (einOrder != null) {
                        updateEinOrderDetails(einOrder, voidRequest, true);
                    }
                    return false;
                }

                if (orderRecord != null) {
                    orderRecord.setRefundDate(new Date(System.currentTimeMillis()));
                    orderRecord.setRefundAuthTransId(reversalResponse.getOrder().getTxRefNum());
                } else if (penaltyOrder != null) {
                    penaltyOrder.setRefundDate(new Date(System.currentTimeMillis()));
                    penaltyOrder.setRefundAuthTransId(reversalResponse.getOrder().getTxRefNum());
                } else if (einOrder != null) {
                    einOrder.setRefundDate(new Date(System.currentTimeMillis()));
                    einOrder.setRefundAuthTransId(reversalResponse.getOrder().getTxRefNum());
                }
            }
        }

        if (reversalResponse != null) {
            StatusResponse statusResponse = reversalResponse.getOrder().getStatus();
            if (statusResponse.getProcStatus().equalsIgnoreCase("0")
                    && statusResponse.getApprovalStatus().equalsIgnoreCase("1")) {

                if (orderRecord != null) {
                    updateOrderDetails(orderRecord, voidRequest, false);
                } else if (penaltyOrder != null) {
                    updatePenaltyOrderDetails(penaltyOrder, voidRequest, false);
                } else if (einOrder != null) {
                    updateEinOrderDetails(einOrder, voidRequest, false);
                }

                if (isSendEmail) {
                    try {
                        if (orderRecord != null) {
                            mailjetSender.sendCancelledEmail(orderRecord);
                        } else if (penaltyOrder != null) {
                            mailjetSender.sendPenaltyOrderCancelledEmail(penaltyOrder);
                        } else if (einOrder != null) {
                            mailjetSender.sendEinOrderCancelledEmail(einOrder);
                        }
                    } catch (IOException e) {
                        log.error("ERROR: Unable to send email", e);
                    }
                }

                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public OrderRequest mapOrderRecord(OrderRecord orderRecord) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderID(orderRecord.getOrderId());
        orderRequest.setTxRefNum(orderRecord.getOrbitalTransactionNumber());
        orderRequest.setAmount(orderRecord.getAmount());
        return orderRequest;
    }

    public OrderRequest mapOrderRecord4Penalty(PenaltyOrder orderRecord) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderID(orderRecord.getOrderId());
        orderRequest.setTxRefNum(orderRecord.getOrbitalTransactionNumber());
        orderRequest.setAmount(orderRecord.getAmount());
        return orderRequest;
    }

    public OrderRequest mapOrderRecord4Ein(EinOrder orderRecord) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderID(orderRecord.getId().toString());
        orderRequest.setTxRefNum(orderRecord.getOrbitalTransactionNumber());
        orderRequest.setAmount(orderRecord.getAmount().toString());
        return orderRequest;
    }

    @Override
    boolean hitFailover(OrbitalRequest request) {
        HttpEntity<ReversalRequest> entity = new HttpEntity(request, headers());

        ResponseEntity<ReversalResponse> response = restTemplate.exchange(
                reversalFailoverUri(), HttpMethod.POST, entity, ReversalResponse.class);

        return isOK(response);
    }

    public void updateOrderDetails(OrderRecord orderRecord, VoidRequest voidRequest, boolean isManualActionRequired) {

        String note_detail = "";
        String content = voidRequest.getContent();
        String type = voidRequest.getType();
        String user = voidRequest.getUser();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String strDate = formatter.format(date);

        if (content.equals("CANCEL AND REFUND") && !orderRecord.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
            orderRecord.setStatus(OrderRecord.STATUS_CANCELLED);
        }
        if (content.contains("C/B DNS") && !orderRecord.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
            orderRecord.setStatus(OrderRecord.STATUS_CHARGE_BACK);
            orderRecord.setCbtype(voidRequest.getCbtype());
        }
        if (isManualActionRequired) {
            content = content + " - Manual Refund Needed";
        }
        note_detail = type + "=*=" + content + "=*=" + user + "=*=" + strDate;

        String details = "";
        if (orderRecord.getNotes() == null) {
            details = note_detail;
        } else {
            details = orderRecord.getNotes() + "###" + note_detail;
        }
        orderRecord.setNotes(details);
        orderRecordRepo.save(orderRecord);

    }

    public void updatePenaltyOrderDetails(PenaltyOrder existing, VoidRequest voidRequest, boolean isManualActionRequired) {

        String note_detail = "";
        String content = voidRequest.getContent();
        String type = voidRequest.getType();
        String user = voidRequest.getUser();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String strDate = formatter.format(date);

        if (content.equals("CANCEL AND REFUND") && !existing.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
            existing.setStatus(OrderRecord.STATUS_CANCELLED);
        }
        if (content.contains("C/B DNS") && !existing.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
            existing.setStatus(OrderRecord.STATUS_CHARGE_BACK);
            existing.setCbtype(voidRequest.getCbtype());
        }
        if (isManualActionRequired) {
            content = content + " - Manual Refund Needed";
        }
        note_detail = type + "=*=" + content + "=*=" + user + "=*=" + strDate;
        String details = "";
        if (existing.getNotes() == null) {
            details = note_detail;
        } else {
            details = existing.getNotes() + "###" + note_detail;
        }
        existing.setNotes(details);
        penaltyRecordRepo.save(existing);

    }

    public void updateEinOrderDetails(EinOrder existing, VoidRequest voidRequest, boolean isManualActionRequired) {

        String note_detail = "";
        String content = voidRequest.getContent();
        String type = voidRequest.getType();
        String user = voidRequest.getUser();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String strDate = formatter.format(date);

        if (content.equals("CANCEL AND REFUND") && !existing.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
            existing.setStatus(OrderRecord.STATUS_CANCELLED);
        }
        if (content.contains("C/B DNS") && !existing.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
            existing.setStatus(OrderRecord.STATUS_CHARGE_BACK);
            existing.setCbtype(voidRequest.getCbtype());
        }

        if (isManualActionRequired) {
            content = content + " - Manual Refund Needed";
        }
        note_detail = type + "=*=" + content + "=*=" + user + "=*=" + strDate;

        String details = "";
        if (existing.getNotes() == null) {
            details = note_detail;
        } else {
            details = existing.getNotes() + "###" + note_detail;
        }
        existing.setNotes(details);
        einRecordRepo.save(existing);
    }
}
