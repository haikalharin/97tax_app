package com.repnox.nineseventax.features.penalty;

import com.google.gson.Gson;
import com.repnox.nineseventax.exceptions.ConflictException;
import com.repnox.nineseventax.features.ecommerce.*;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCreatePaymentService;
import com.repnox.nineseventax.features.ecommerce.util.JwtPayloadResponse;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.oic.OicController;
import com.repnox.nineseventax.features.payment.request.CreatePaymentRequest;
import com.repnox.nineseventax.features.payment.response.CreatePaymentResponse;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanController;

import com.repnox.nineseventax.features.utils.AddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController()
@RequestMapping("/api/penalty")
public class PenaltyController {
    @Autowired
    private CentinelFacade centinelFacade;

    @Autowired
    private AuthorizeNetFacade authorizeNetFacade;

    @Autowired
    private OrbitalCreatePaymentService createPaymentService;

    private static final Logger logger = LoggerFactory.getLogger(PenaltyController.class);
    public static final String CURRENT_ORDER = "currentOrder";
    private static final long TIME_1ST_INCOMPLETE_EMAIL_SENDING = 30 * 60 * 1000;
    private static final long TIME_2ND_INCOMPLETE_EMAIL_SENDING = 3 * 24 * 3600 * 1000;

    @Autowired
    PenaltyRecordRepo penaltyRecordRepo;

    @Autowired
    PenaltyOrderRepo penaltyOrderRepo;

    @Autowired
    private TransactionLogsRepo transactionLogsRepo;

    @Autowired
    MailjetSender mailjetSender;

    @Autowired
    private OrderLockRepository orderLockRepository;

    @PostMapping
    public @ResponseBody
    PenaltyOrder createPenalty(HttpSession session,
                               @RequestBody PenaltyOrder penaltyOrder) {
        penaltyOrder.setStatus(OrderRecord.STATUS_INCOMPLETE);
        if (penaltyOrder.getCorrelationId() == null || !penaltyOrder.getCorrelationId().isEmpty()) {
            penaltyOrder.setCorrelationId(UUID.randomUUID().toString());
        }

        penaltyRecordRepo.save(penaltyOrder);
        session.setAttribute(CURRENT_ORDER, penaltyOrder);

        final PenaltyOrder tempOrderRecord = penaltyOrder;
        Timer timer1 = new Timer();
        Timer timer2 = new Timer();
        TimerTask delayedThreadStartTask1 = new TimerTask() {
            @Override
            public void run() {
                sendIncompleteEmail(tempOrderRecord);
            }
        };
        TimerTask delayedThreadStartTask2 = new TimerTask() {
            @Override
            public void run() {
                sendIncompleteEmail(tempOrderRecord);
            }
        };

        timer1.schedule(delayedThreadStartTask1, TIME_1ST_INCOMPLETE_EMAIL_SENDING);
        timer2.schedule(delayedThreadStartTask2, TIME_2ND_INCOMPLETE_EMAIL_SENDING);

        return penaltyOrder;  //here we have id, status, correlationId
    }


    @PostMapping("/authorize")
    public @ResponseBody
    AuthorizeResult authorizeTransaction(HttpSession session,
                                         HttpServletRequest httpServletRequest, @RequestBody OrderInfo orderInfo) throws URISyntaxException {

        Map payload = centinelFacade.validateJwt(orderInfo.getResponseJwt());
        Map paymentMap = (Map) payload.get("Payment");
        Map extendedDataMap = (Map) paymentMap.get("ExtendedData");
        String eciFlag = (String) extendedDataMap.get("ECIFlag");
        String enrolled = (String) extendedDataMap.get("Enrolled");

        PenaltyOrder orderRecord = getCurrentOrder(session);

        if (orderRecord == null) {
            Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(Long.valueOf(orderInfo.getOrderNumber()));
            if(optRecord.isPresent()) {
                orderRecord = optRecord.get();
            } else {
                AuthorizeResult authorizeResult = new AuthorizeResult();
                authorizeResult.setSuccess(false);
                authorizeResult.setMessage("Your session is expired. Please retry from beginning.!");

                return authorizeResult;
            }
        }

        List<String> validStatuses = Arrays.asList(OrderRecord.STATUS_INCOMPLETE, OrderRecord.STATUS_FAILED);
        if (!validStatuses.contains(orderRecord.getStatus())) {
            throw new ConflictException();
        }

        Optional<OrderLock> orderLockOptional = orderLockRepository.findByOrderId(orderRecord.getId());
        OrderLock orderLock = orderLockOptional.orElse(null);
        if (orderLock == null) {
            orderLock = new OrderLock();
            orderLock.setOrderId(orderRecord.getId());
            orderLock.setLockedAt(new Timestamp(System.currentTimeMillis()));
            orderLockRepository.save(orderLock);
        } else {
            // Throw conflict exception if order locked less than 10 minutes ago
            long lockedAt = orderLock.getLockedAt().getTime();
            long now = System.currentTimeMillis();
            if (now - lockedAt < 10 * 60 * 1000) {
                throw new ConflictException();
            }
        }

        if (orderRecord.getCardNumber() != null) {
            orderRecord.setCardNumber(orderRecord.getCardNumber().replaceAll("[^0-9]", ""));
        }

        orderInfo.setOrderNumber(orderRecord.getId().toString());

        orderRecord.setAuthorizeErrorCode(((Integer) payload.get("ErrorNumber")).toString());
        orderRecord.setAuthorizeErrorMessage((String) payload.get("ErrorDescription"));
        orderRecord.setTransactionId((String) paymentMap.get("ProcessorTransactionId"));
        orderRecord.setPayloadResponseStatus((String) extendedDataMap.get("PAResStatus"));
        orderRecord.setSignatureVerification((String) extendedDataMap.get("SignatureVerification"));
        orderRecord.setAuthEci(eciFlag);
        orderRecord.setEciFlag(eciFlag);
        orderRecord.setAuthXid((String) extendedDataMap.get("XID"));
        orderRecord.setEnrolled((String) extendedDataMap.get("Enrolled"));

        if (extendedDataMap.get("CAVV") != null) {
            orderRecord.setAuthCavv((String) extendedDataMap.get("CAVV"));
        } else if (extendedDataMap.get("AAV") != null) {
            orderRecord.setAuthCavv((String) extendedDataMap.get("AVV"));
        }

        String ipAddress = httpServletRequest.getHeader("x-forwarded-for");
        if (ipAddress == null) {
            ipAddress = httpServletRequest.getRemoteAddr();
        }
        orderRecord.setCustomerIpAddress(ipAddress);

        AuthorizeResult authorizeResult = new AuthorizeResult();
        authorizeResult.setCorrelationId(orderRecord.getCorrelationId());

        if ("02".equals(eciFlag)) {
            orderRecord.setCardBrand("MASTERCARD");
            orderInfo.setCardBrand("MASTERCARD");
        } else if ("05".equals(eciFlag)) {
            orderRecord.setCardBrand("VISA");
            orderInfo.setCardBrand("VISA");
        }
        orderInfo.setAuthCavv(orderRecord.getAuthCavv());
        orderInfo.setEciNumber(orderRecord.getEciFlag());

        Gson gson = new Gson();
        String payload_response = gson.toJson(payload);
        String orbital_payment_response = "";
        String exception_message = "";

        if ("00".equals(eciFlag) || "07".equals(eciFlag)/* || !"Y".equals(enrolled) || ("06".equals(eciFlag) && !"Y".equals(enrolled))*/) {

            logger.error( "ECI Flag is: " + eciFlag + ", Enrolled status : " + enrolled);
            String errMsg = "Authentication is unsuccessful or not attempted. The credit card is either a non-3D card or card issuing bank does not handle it as a 3D transaction.";
            exception_message = errMsg;

            authorizeResult.setSuccess(false);
            authorizeResult.setMessage(errMsg);
            orderRecord.setStatus(OrderRecord.STATUS_FAILED);
            orderRecord.setNotes(authorizeResult.getMessage());

            orderLockRepository.delete(orderLock);  // allow retry if failed
        } else {
            try {
                // AuthorizeResult authCardResult = authorizeNetFacade.authorizeCard(orderRecord, ipAddress);
                // authorizeResult.setSuccess(authCardResult.getSuccess());
                // authorizeResult.setMessage(authCardResult.getMessage());

                orderInfo.setIsPenaltyWaiver(true);

                CreatePaymentRequest buildPaymentRequest = createPaymentService.buildPaymentRequest(orderInfo);
                //CreatePaymentResponse createPaymentResponse = createPaymentService.createPayment(buildPaymentRequest);

                orbital_payment_response = createPaymentService.createPayment4Logging(buildPaymentRequest);

                CreatePaymentResponse createPaymentResponse = gson.fromJson(orbital_payment_response, CreatePaymentResponse.class);

                String approvalStatus = createPaymentResponse.getOrder().getStatus().getApprovalStatus();
                String respCodeMessage = createPaymentResponse.getOrder().getStatus().getRespCodeMessage();
                if (StringUtils.isBlank(respCodeMessage) && createPaymentResponse.getOrder().getStatus().getProcStatusMessage().equals("Approved")) {
                    respCodeMessage = "Success";
                }
                String respCode = createPaymentResponse.getOrder().getStatus().getRespCode();

                orderRecord.setOrderId(createPaymentResponse.getOrder().getOrderID());
                orderRecord.setOrbitalTransactionId(createPaymentResponse.getOrder().getTxRefIdx());
                orderRecord.setOrbitalTransactionNumber(createPaymentResponse.getOrder().getTxRefNum());
                orderRecord.setAuthorizeTransactionId(createPaymentResponse.getOrder().getTxRefNum());
                orderRecord.setTransactionType(createPaymentResponse.getTransType());

                orderRecord.setInquiryRetryNumber(createPaymentResponse.getOrder().getInquiryRetryNumber());
                orderRecord.setRetryAttempCount(createPaymentResponse.getOrder().getRetryAttempCount());
                orderRecord.setAuthorizeAuthCode(createPaymentResponse.getOrder().getStatus().getAuthorizationCode());
                orderRecord.setAuthorizeErrorMessage(respCodeMessage);
                orderRecord.setAuthorizeErrorCode(respCode);

                String cardNumber = orderInfo.getCardNumber();
                String last4CardNumber = cardNumber.substring(cardNumber.length() - 4);
                orderRecord.setLast4DigitsCard(last4CardNumber);

                if("1".equals(approvalStatus)) {
                    orderRecord.setStatus(OrderRecord.STATUS_PROCESSING);
                    authorizeResult.setSuccess(true);
                    authorizeResult.setMessage("Successfully process payment");
                } else {
                    logger.error("Error in authorize");

                    exception_message = "Error in authorize: approvalStatus is not 1";

                    orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                    authorizeResult.setSuccess(false);
                    authorizeResult.setMessage("There was an unexpected error on card transaction.");

                    orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                    orderRecord.setNotes(authorizeResult.getMessage());

                    orderLockRepository.delete(orderLock);  // allow retry if failed
                }

            } catch (Exception e) {
                logger.error("Error in authorize", e);

                exception_message = "Error in authorize: " + e.getMessage();

                authorizeResult.setSuccess(false);
                authorizeResult.setMessage("Card Transaction Failed!");
                orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                orderRecord.setNotes(authorizeResult.getMessage());

                orderLockRepository.delete(orderLock);  // allow retry if failed
            }
        }

        if (authorizeResult.getSuccess() == Boolean.FALSE) {
            orderRecord.setStatus(OrderRecord.STATUS_FAILED);
        }

        orderRecord.setAmount(orderInfo.getAmount());

        orderRecord = AddressUtil.resolveIrsAddress(orderRecord);
        penaltyRecordRepo.save(orderRecord);

        TransactionLogs trxLog = new TransactionLogs();
        trxLog.setOrder_id(orderRecord.getId());
        trxLog.setCardinal_response(payload_response);
        trxLog.setOrbital_payment_response(orbital_payment_response);
        trxLog.setException_message(exception_message);
        trxLog.setCreatedDate(new Date());

        transactionLogsRepo.save(trxLog);

        httpServletRequest.getSession().setAttribute(PaymentPlanController.CURRENT_ORDER, null);
        httpServletRequest.getSession().setAttribute("orderInfo", null);
        httpServletRequest.getSession().setAttribute(OicController.OIC_KEY, null);
        if (OrderRecord.STATUS_FAILED.equals(orderRecord.getStatus())) {

            final PenaltyOrder tempOrderRecord = orderRecord;
            Timer timer1 = new Timer();
            TimerTask delayedThreadStartTask1 = new TimerTask() {
                @Override
                public void run() {
                    mailjetSender.sendPenaltyOrderFailedEmail(tempOrderRecord);
                }
            };

            timer1.schedule(delayedThreadStartTask1, 30 * 60 * 1000);
        } else if (OrderRecord.STATUS_PROCESSING.equals(orderRecord.getStatus())) {

            String dateFormatted = new SimpleDateFormat("M/d/yyyy hh:mm:ss a").format(orderRecord.getCreatedDate());
            logger.debug("AUTHROZE CREATED TIME = " + dateFormatted + " , " + orderRecord.getCreatedDate().getHours() + ":" + orderRecord.getCreatedDate().getMinutes() + ":" + orderRecord.getCreatedDate().getSeconds());
            mailjetSender.sendPenaltyOrderProcessingEmail(orderRecord);
        }
        /*else if (OrderRecord.STATUS_INCOMPLETE.equals(orderRecord.getStatus())) {

            final PenaltyOrder tempOrderRecord = orderRecord;
            Timer timer1 = new Timer();
            Timer timer2 = new Timer();
            TimerTask delayedThreadStartTask1 = new TimerTask() {
                @Override
                public void run() {
                mailjetSender.sendPenaltyOrderIncompleteEmail(tempOrderRecord);
                }
            };
            TimerTask delayedThreadStartTask2 = new TimerTask() {
                @Override
                public void run() {
                mailjetSender.sendPenaltyOrderIncompleteEmail(tempOrderRecord);
                }
            };

            timer1.schedule(delayedThreadStartTask1, TIME_1ST_INCOMPLETE_EMAIL_SENDING);
            timer2.schedule(delayedThreadStartTask2, TIME_2ND_INCOMPLETE_EMAIL_SENDING);

        }*/

        return authorizeResult;
    }

    @PutMapping("/fail")
    public void fail(HttpSession session, @RequestBody OrderInfo orderInfo) throws Exception {
        PenaltyOrder orderRecord = penaltyRecordRepo.findById(Long.valueOf(orderInfo.getId())).orElse(null);

        if(orderRecord != null) {
            orderRecord.setStatus(OrderRecord.STATUS_FAILED);
            orderRecord.setNotes(orderInfo.getFailureReason());
            session.setAttribute(PaymentPlanController.CURRENT_ORDER, orderRecord);
            penaltyRecordRepo.save(orderRecord);
        }
    }

    private void sendIncompleteEmail(PenaltyOrder orderRecord) {
        try {
            int total = penaltyOrderRepo.getProcessingFailedCompleteEmails(orderRecord.getEmail());
            if (total == 0) {
                mailjetSender.sendPenaltyOrderIncompleteEmail(orderRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PenaltyOrder mapPenaltyOrderRecord(HttpSession session, PenaltyOrder newData) {

        PenaltyOrder orderRecord = getCurrentOrder(session);

        if (orderRecord == null) {

            Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(newData.getId());
            if(optRecord.isPresent()) {
                orderRecord = optRecord.get();
            } else {
                orderRecord = new PenaltyOrder();
            }

            orderRecord.setStatus(OrderRecord.STATUS_INCOMPLETE);
            penaltyRecordRepo.save(orderRecord);
        }


        mapCardData(newData, orderRecord);

        if (StringUtils.isNotBlank(newData.getCorrelationId()))
            orderRecord.setCorrelationId(newData.getCorrelationId());
        else
            orderRecord.setCorrelationId(UUID.randomUUID().toString());

        orderRecord.setFirstName(newData.getFirstName());
        orderRecord.setLastName(newData.getLastName());
        orderRecord.setEmail(newData.getEmail());
        orderRecord.setBillingAddress1(newData.getBillingAddress1());
        orderRecord.setBillingAddress2(newData.getBillingAddress2());
        orderRecord.setBillingCity(newData.getBillingCity());
        orderRecord.setBillingState(newData.getBillingState());
        orderRecord.setBillingPostalCode(newData.getBillingPostalCode());
        orderRecord.setBillingPhone(newData.getBillingPhone());
        orderRecord.setShippingAddress1(newData.getShippingAddress1());
        orderRecord.setShippingAddress2(newData.getShippingAddress2());
        orderRecord.setShippingCity(newData.getShippingCity());
        orderRecord.setShippingState(newData.getShippingState());
        orderRecord.setShippingPostalCode(newData.getShippingPostalCode());
        orderRecord.setSsn(newData.getSsn());
        orderRecord.setMarried(newData.getMarried());
        orderRecord.setLargestPenalty(newData.getLargestPenalty());
        orderRecord.setPenaltyAmountWaived(newData.getPenaltyAmountWaived());
        orderRecord.setPenaltyWaivedYear(newData.getPenaltyWaivedYear());
        orderRecord.setPenaltyWaivedType(newData.getPenaltyWaivedType());
        orderRecord.setIsPenaltyPaid(newData.getIsPenaltyPaid());
        orderRecord.setIsLast3Filed(newData.getIsLast3Filed());
        orderRecord.setIsLast3IrsWavied(newData.getIsLast3IrsWavied());
        orderRecord.setIsDecreaseIncomeTax(newData.getIsDecreaseIncomeTax());
        orderRecord.setPartnerCode(newData.getPartnerCode());

        if (StringUtils.isNotBlank(newData.getStatus())) {
            orderRecord.setStatus(newData.getStatus());
        } else if (!StringUtils.isNotBlank(orderRecord.getStatus())) {
            orderRecord.setStatus(OrderRecord.STATUS_INCOMPLETE); //if not set already and newData.status == null then need to set new/DEFAULT
        }
        return orderRecord;
    }

    private void mapCardData(PenaltyOrder newData, PenaltyOrder orderRecord) {
        if (newData.getCardNumber() != null) {
            orderRecord.setCardNumber(newData.getCardNumber().replaceAll("[^0-9]", ""));
        }
        if (newData.getCardExpMonth() != null) {
            orderRecord.setCardExpMonth(newData.getCardExpMonth());
        }
        if (newData.getCardExpYear() != null) {
            orderRecord.setCardExpYear(newData.getCardExpYear());
        }
        if (newData.getCardCvc() != null) {
            orderRecord.setCardCvc(newData.getCardCvc());
        }
        if (newData.getPartnerCode() != null) {
            orderRecord.setPartnerCode(newData.getPartnerCode());
        }

    }

    @PostMapping("/jwt")
    public @ResponseBody
    JwtPayloadResponse requestJwt(HttpSession session, @RequestBody PenaltyOrder orderInfo) throws Exception {
        System.out.println("updatePenaltyOrder" + orderInfo.getId());

        PenaltyOrder orderRecord = getCurrentOrder(session);

        if (orderRecord == null) {
            Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(orderInfo.getId());
            if(optRecord.isPresent()) {
                orderRecord = optRecord.get();
            } else {
                throw new Exception("Order record is not available.");
            }
        }
        if (!StringUtils.isNotBlank(orderInfo.getId().toString())) {
            orderInfo.setId(orderRecord.getId());
        }

        JwtPayloadResponse jwtPayloadResponse = new JwtPayloadResponse();
        long nowMillis = System.currentTimeMillis();
        jwtPayloadResponse.setJwt(centinelFacade.generateSignature(nowMillis, orderInfo));
        jwtPayloadResponse.setPayload(centinelFacade.generatePayload(orderInfo));
        orderRecord = mapPenaltyOrderRecord(session, orderInfo);
        session.setAttribute(CURRENT_ORDER, orderRecord);
        return jwtPayloadResponse;
    }

    @PostMapping("/presave")
    public PenaltyOrder presavePenaltyOrder(HttpSession session, @RequestBody PenaltyOrder penaltyOrder) {
        System.out.println("updatePenaltyOrder" + penaltyOrder.getId());
        penaltyRecordRepo.save(penaltyOrder);
        return penaltyOrder;
    }

    @PutMapping
    public PenaltyOrder updatePenaltyOrder(HttpSession session, @RequestBody PenaltyOrder penaltyOrder) {
        System.out.println("updatePenaltyOrder");

        penaltyOrder = mapPenaltyOrderRecord(session, penaltyOrder);
        penaltyRecordRepo.save(penaltyOrder);
        session.setAttribute(CURRENT_ORDER, penaltyOrder);
        return penaltyRecordRepo.findById(penaltyOrder.getId()).orElse(penaltyOrder);
    }


    @GetMapping("/order")
    public @ResponseBody
    PenaltyOrder getCurrentOrder(HttpSession session) {
        try {
            Object currentOrder = session.getAttribute(CURRENT_ORDER);
            if (currentOrder != null && currentOrder instanceof PenaltyOrder) {
                return (PenaltyOrder) currentOrder; //here we returned id, status, correlationId
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.warn("Invalid session data", e);
            return null;
        }
    }
}
