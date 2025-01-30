package com.repnox.nineseventax.features.admin;

import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.admin.request.VoidRequest;
import com.repnox.nineseventax.features.auth.AuthRole;
import com.repnox.nineseventax.features.auth.AuthService;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.ecommerce.TransactionRecord;
import com.repnox.nineseventax.features.ecommerce.TransactionRecordRepo;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCreatePaymentService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalFetchTransactionService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalRefundService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalReversalService;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.ein.EinRecordRepo;
import com.repnox.nineseventax.features.mailroom.model.Batch;
import com.repnox.nineseventax.features.mailroom.model.BatchRepo;
import com.repnox.nineseventax.features.order.interfaces.MailRoomOrderProcessService;
import com.repnox.nineseventax.features.order.interfaces.OrderProcessService;
import com.repnox.nineseventax.features.payment.request.CreatePaymentRequestAfterSettled;
import com.repnox.nineseventax.features.payment.request.MerchantRequest;
import com.repnox.nineseventax.features.payment.request.UseProfile;
import com.repnox.nineseventax.features.payment.response.ReversalResponse;
import com.repnox.nineseventax.features.payment.response.StatusResponse;
import com.repnox.nineseventax.features.payment.response.TransactionResponse;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyOrderRepo;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import com.repnox.nineseventax.features.processordersschedule.ProcessOrdersSchedule;
import com.repnox.nineseventax.features.processordersschedule.ProcessOrdersScheduleRepo;
import com.repnox.nineseventax.features.utils.AdminUtil;
import com.repnox.nineseventax.features.utils.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/v2/admin")
@Slf4j
public class AdminV2Controller {

    public static final long INTERVAL_1DAY = 24 * 60 * 60 * 1000;

    @Value("${orbital.api.version}")
    private String version;

    @Value("${orbital.bin}")
    private String bin;

    @Value("${orbital.terminalId}")
    private String terminalId;

    private Timer timer;

    @Autowired
    private AuthService authService;
    @Autowired
    private OrbitalRefundService refundService;
    @Autowired
    private OrbitalReversalService reversalService;
    @Autowired
    private TransactionRecordRepo transactionRecordRepo;
    @Autowired
    private OrderRecordRepo orderRecordRepo;
    @Autowired
    private ProcessOrdersScheduleRepo processOrdersScheduleRepo;
    @Autowired
    private BatchRepo batchRepo;
    @Autowired
    private OrderProcessService orderProcessService;
    @Autowired
    private MailRoomOrderProcessService mailRoomOrderProcessService;
    @Autowired
    private PenaltyRecordRepo penaltyRecordRepo;
    @Autowired
    private EinRecordRepo einRecordRepo;
    @Autowired
    private OrbitalCreatePaymentService orbitalCreatePaymentService;

    @PutMapping("/transaction/{authTransactionId}/refund")
    public boolean refundTransaction(HttpSession session, @RequestBody RefundTransactionRequest request,
                                  @PathVariable String authTransactionId) throws Exception {
        List<AuthRole> roles = new ArrayList<>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        refundService.refundPayment(authTransactionId, OrderRecord.STATUS_CANCELLED, true);
        return true;
    }

    @PutMapping("/transaction/{authTransactionId}/void")
    public ResponseEntity voidTransaction(HttpSession session, @PathVariable String authTransactionId, @RequestBody VoidRequest request) throws Exception {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        String username = authService.getAuthSession(session).getAuthUser().getUsername();
        boolean response = reversalService.reverse(authTransactionId, OrderRecord.STATUS_CANCELLED, true, username, request);
        if (response) {
            return new ResponseEntity(new DefaultResponse("Success", "Order: " + request.getOrderNum() + " has voided", "S001"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new DefaultResponse("Failure", "Order: " + request.getOrderNum() + " has failed for voided request", "F001"), HttpStatus.OK);
        }
    }

    @PutMapping("/transaction/{authTransactionId}/voidOrRefundOnChargeBack")
    public ResponseEntity refundTransactionOnChargeBack(HttpSession session, @RequestBody VoidRequest request,
                                              @PathVariable String authTransactionId) throws Exception {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        String username = authService.getAuthSession(session).getAuthUser().getUsername();
        boolean response = false;
        if (request.getCbtype() == 1) {
            response = reversalService.reverse(authTransactionId, OrderRecord.STATUS_CHARGE_BACK, true, username, request);
        } else {
            OrderRecord orderRecord = orderRecordRepo.findByAuthorizeTransactionId(authTransactionId);
            PenaltyOrder penaltyOrder = penaltyRecordRepo.findByAuthorizeTransactionId(authTransactionId);
            EinOrder einOrder = einRecordRepo.findByAuthorizeTransactionId(authTransactionId);
            if (orderRecord != null) {
                reversalService.updateOrderDetails(orderRecord, request, false);
            } else if (penaltyOrder != null) {
                reversalService.updatePenaltyOrderDetails(penaltyOrder, request, false);
            } else if (einOrder != null) {
                reversalService.updateEinOrderDetails(einOrder, request, false);
            }
            response = true;
        }
        if (response) {
            return new ResponseEntity(new DefaultResponse("Success", "Order: " + request.getOrderNum() + " has refunded", "S001"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new DefaultResponse("Failure", "Order: " + request.getOrderNum() + " has failed for refund request", "F001"), HttpStatus.OK);
        }

//        refundService.refundPayment(authTransactionId, request.getAmount(), OrderRecord.STATUS_CHARGE_BACK, false);
    }

    @PutMapping("/transaction/{authTransactionId}/voidOnChargeBack")
    public void voidTransactionOnChargeBack(HttpSession session, @PathVariable String authTransactionId) throws Exception {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        String username = authService.getAuthSession(session).getAuthUser().getUsername();
        reversalService.reverse(authTransactionId, OrderRecord.STATUS_CHARGE_BACK, false, username, null);
    }

    /**
     * Get Transaction Status
     * */
    @GetMapping("/transaction/{authTransactionId}")
    public TransactionRecord getTransactionRecord(HttpSession session, @PathVariable String authTransactionId) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        OrderRecord orderRecord = orderRecordRepo.findByAuthorizeTransactionId(authTransactionId);

       // TransactionResponse response = fetchTransactionService.fetch(version, bin, terminalId, orderRecord.getOrbitalTransactionNumber());
        //log.debug("Transaction Response : {}", response.toString());

     //   if (response != null) {
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setAuthorizeTransactionId(authTransactionId);
           // if (response.getOrder() != null) {
                transactionRecord.setTransactionType(orderRecord.getTransactionType());
                transactionRecord.setTransactionStatus(orderRecord.getStatus());
             //   if (response.getOrder().getAmount() != null) {
                    transactionRecord.setAuthAmount(orderRecord.getAmount());
              //  }
                transactionRecord.setAuthCode(orderRecord.getAuthorizeAuthCode());
                /*transactionRecord.setAvsResponse(response.getTransaction().getAVSResponse());
                transactionRecord.setCardCodeResponse(response.getTransaction().getCardCodeResponse());
                transactionRecord.setCavvResponse(response.getTransaction().getCAVVResponse());
                transactionRecord
                        .setResponseReasonCode(String.valueOf(response.getTransaction().getResponseReasonCode()));
                transactionRecord
                        .setResponseReasonDescription(response.getTransaction().getResponseReasonDescription());*/
           // }
            transactionRecordRepo.save(transactionRecord);
            return transactionRecord;
    //    } else {
      //      return null;
      //  }
    }

    @GetMapping("/processorders")
    public HashMap<String, Object> processOrders(HttpSession session, HttpServletResponse response) throws Exception {
        return orderProcessService.process();
    }

    @PutMapping("/processordersschedule/current")
    public @ResponseBody
    ProcessOrdersSchedule updateProcessOrdersSchedule(
            @RequestBody ProcessOrdersSchedule scheduleInfo) throws Exception {
        Boolean isAutomatic = scheduleInfo.getIsAutomatic();
        Date automatedTime = scheduleInfo.getAutomatedTime();
        String[] excludedHolidayArr = scheduleInfo.getExcludedHolidays().split(",");
        List<String> excludedHolidays = ArrayUtils.isNotEmpty(excludedHolidayArr) ? Arrays.asList(excludedHolidayArr) : Collections.EMPTY_LIST;
        processOrdersScheduleRepo.save(scheduleInfo);
/*
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
        }

        if (isAutomatic == null || !isAutomatic) {
            return scheduleInfo;
        }

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        if (automatedTime.compareTo(currentDate) <= 0) {
            Calendar newCal = Calendar.getInstance();
            newCal.setTime(automatedTime);
            newCal.add(Calendar.DAY_OF_MONTH, 1);
            automatedTime = newCal.getTime();
        }

        Calendar newCal = Calendar.getInstance();
        newCal.add(Calendar.SECOND, 2);
        automatedTime = newCal.getTime();

        this.timer = new Timer();
        TimerTask scheduledTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Calendar innerCal = Calendar.getInstance();
                    Date innerCurrentDate = innerCal.getTime();
                    SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String innerCurrentDateStr = dateOnlyFormat.format(innerCurrentDate);
                    if (excludedHolidays.contains(innerCurrentDateStr)) {
                        return;
                    }

                    if (innerCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                            || innerCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        return;
                    }

                    ArrayList<Batch> batches = AdminUtil.convertBatch(batchRepo.findAllBatch());
                    for (Batch batchItem : batches) {
                        Date createdAt = batchItem.getCreatedAt();
                        String createdAtStr = dateOnlyFormat.format(createdAt);
                        if (createdAtStr != null && createdAtStr.equals(innerCurrentDateStr)) {
                            return;
                        }
                    }

                    mailRoomOrderProcessService.process();
                } catch (Exception e) {
                    log.error("ERROR: occurred while running automated fulfillment scheduler", e);
                }
            }
        };
        this.timer.schedule(scheduledTask, automatedTime, INTERVAL_1DAY); // every day interval

 */
        return scheduleInfo;
    }

    @GetMapping("/transaction/penalty/{authTransactionId}")
    public TransactionRecord getTransactionPenaltyRecord(HttpSession session,
                                                  @PathVariable String authTransactionId) throws Exception {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        PenaltyOrder orderRecord = penaltyRecordRepo.findByAuthorizeTransactionId(authTransactionId);

        // TransactionResponse response = fetchTransactionService.fetch(version, bin, terminalId, orderRecord.getOrbitalTransactionNumber());
        //log.debug("Transaction Response : {}", response.toString());

        //   if (response != null) {
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setAuthorizeTransactionId(authTransactionId);
        // if (response.getOrder() != null) {
        transactionRecord.setTransactionType(orderRecord.getTransactionType());
        transactionRecord.setTransactionStatus(orderRecord.getStatus());
        //   if (response.getOrder().getAmount() != null) {
        transactionRecord.setAuthAmount(orderRecord.getAmount());
        //  }
        transactionRecord.setAuthCode(orderRecord.getAuthorizeAuthCode());
                /*transactionRecord.setAvsResponse(response.getTransaction().getAVSResponse());
                transactionRecord.setCardCodeResponse(response.getTransaction().getCardCodeResponse());
                transactionRecord.setCavvResponse(response.getTransaction().getCAVVResponse());
                transactionRecord
                        .setResponseReasonCode(String.valueOf(response.getTransaction().getResponseReasonCode()));
                transactionRecord
                        .setResponseReasonDescription(response.getTransaction().getResponseReasonDescription());*/
        // }
        transactionRecordRepo.save(transactionRecord);
        return transactionRecord;
        //    } else {
        //      return null;
        //  }
    }

    @PutMapping("/transaction/penalty/{authTransactionId}/void")
    public ResponseEntity voidTransactionPenalty(HttpSession session, @PathVariable String authTransactionId, @RequestBody VoidRequest request) throws Exception {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        String username = authService.getAuthSession(session).getAuthUser().getUsername();
        boolean response= reversalService.reverse(authTransactionId, OrderRecord.STATUS_CANCELLED, true, username, request);
        PenaltyOrder penaltyOrder = penaltyRecordRepo.findByAuthorizeTransactionId(authTransactionId);
        if (response) {
            return new ResponseEntity(new DefaultResponse("Success", "Order: " + penaltyOrder.getOrderId() + " has voided", "S001"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new DefaultResponse("Failure", "Order: " + penaltyOrder.getOrderId() + " has failed for voided request", "F001"), HttpStatus.OK);
        }
    }

    @PutMapping("/transaction/penalty/{authTransactionId}/refund")
    public ResponseEntity<DefaultResponse> refundTransactionPenalty(HttpSession session, @RequestBody RefundTransactionRequest request,
                                  @PathVariable String authTransactionId) throws Exception {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        ResponseEntity result = refundService.refundPayment(authTransactionId, request.getAmount(), OrderRecord.STATUS_CANCELLED, true);
        return result;
    }

    @GetMapping("/transaction/ein/{authTransactionId}")
    public TransactionRecord getTransactionEinRecord(HttpSession session,
                                                         @PathVariable String authTransactionId) throws Exception {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        EinOrder orderRecord = einRecordRepo.findByAuthorizeTransactionId(authTransactionId);

        // TransactionResponse response = fetchTransactionService.fetch(version, bin, terminalId, orderRecord.getOrbitalTransactionNumber());
        //log.debug("Transaction Response : {}", response.toString());

        //   if (response != null) {
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setAuthorizeTransactionId(authTransactionId);
        // if (response.getOrder() != null) {
        transactionRecord.setTransactionType(orderRecord.getTransactionType());
        transactionRecord.setTransactionStatus(orderRecord.getStatus());
        //   if (response.getOrder().getAmount() != null) {
        transactionRecord.setAuthAmount(orderRecord.getAmount().toString());
        //  }
        transactionRecord.setAuthCode(orderRecord.getAuthorizeAuthCode());
                /*transactionRecord.setAvsResponse(response.getTransaction().getAVSResponse());
                transactionRecord.setCardCodeResponse(response.getTransaction().getCardCodeResponse());
                transactionRecord.setCavvResponse(response.getTransaction().getCAVVResponse());
                transactionRecord
                        .setResponseReasonCode(String.valueOf(response.getTransaction().getResponseReasonCode()));
                transactionRecord
                        .setResponseReasonDescription(response.getTransaction().getResponseReasonDescription());*/
        // }
        transactionRecordRepo.save(transactionRecord);
        return transactionRecord;
        //    } else {
        //      return null;
        //  }
    }

    @PutMapping("/transaction/ein/{authTransactionId}/void")
    public ResponseEntity voidTransactionEin(HttpSession session, @PathVariable String authTransactionId) throws Exception {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);


        String username = authService.getAuthSession(session).getAuthUser().getUsername();
        boolean response = reversalService.reverse(authTransactionId, OrderRecord.STATUS_CANCELLED, true, username, null);
        EinOrder einOrder = einRecordRepo.findByAuthorizeTransactionId(authTransactionId);
        if (response) {
            return new ResponseEntity(new DefaultResponse("Success", "Order: " + einOrder.getId() + " has voided", "S001"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new DefaultResponse("Failure", "Order: " + einOrder.getId() + " has failed for voided request", "F001"), HttpStatus.OK);
        }
    }

    @PutMapping("/transaction/ein/{authTransactionId}/refund")
    public void refundTransactionEin(HttpSession session, @RequestBody RefundTransactionRequest request,
                                         @PathVariable String authTransactionId) throws Exception {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        refundService.refundPayment(authTransactionId, request.getAmount(), OrderRecord.STATUS_CANCELLED, true);
    }

}
