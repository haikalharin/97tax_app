package com.repnox.nineseventax.features.ecommerce;

import com.google.gson.Gson;
import com.repnox.nineseventax.exceptions.ConflictException;
import com.repnox.nineseventax.features.admin.OrderSearchQuery;
import com.repnox.nineseventax.features.admin.OrderSearchStatuses;
import com.repnox.nineseventax.features.admin.OrderSpecs;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCreatePaymentService;
import com.repnox.nineseventax.features.ecommerce.util.JwtPayloadResponse;
import com.repnox.nineseventax.features.efile.util.IRSHoneypotRedirect;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.oic.OicController;
import com.repnox.nineseventax.features.oic.OicRepository;
import com.repnox.nineseventax.features.oic.model.OicModel;
import com.repnox.nineseventax.features.payment.request.CreatePaymentRequest;
import com.repnox.nineseventax.features.payment.response.CreatePaymentResponse;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanController;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanRepo;
import com.repnox.nineseventax.features.penalty.PenaltyAmountEstimate;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import com.repnox.nineseventax.features.productprices.ProductPriceRepo;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalDetails;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalRepo;
import com.repnox.nineseventax.features.utils.TaxConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.*;


@RestController()
@RequestMapping("/api/ecommerce")
@Slf4j
public class EcommerceController {

    @Autowired
    private CentinelFacade centinelFacade;

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private TransactionLogsRepo transactionLogsRepo;

    @Autowired
    private OicRepository oicRepository;

    @Autowired
    private TaxLienRemovalRepo taxLienRemovalRepo;

    @Autowired
    private PaymentPlanRepo paymentPlanRepo;

    @SuppressWarnings("unused")
    @Autowired
    private TransactionRecordRepo transactionRecordRepo;

    @Autowired
    private OrbitalCreatePaymentService createPaymentService;

    @Autowired
    private MailjetSender mailjetSender;

    @Autowired
    PenaltyRecordRepo penaltyRecordRepo;

    @Autowired
    ProductPriceRepo productPriceRepo;

    @Autowired
    EcommerceService ecommerceService;

    @Autowired
    OrderService orderService;

    @Autowired
    private EmailToUserIdMapService emailToUserIdMapService;

    @Autowired
    private OrderLockRepository orderLockRepository;

    @GetMapping("/order")
    public @ResponseBody OrderRecord getCurrentOrder(HttpSession session) {
        try {
            Object currentOrder = session.getAttribute(PaymentPlanController.CURRENT_ORDER);
            if (currentOrder != null && currentOrder instanceof OrderRecord) {
                return (OrderRecord) currentOrder;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.warn("Invalid session data", e);
            return null;
        }
    }

    @GetMapping("/confirmation/{correlationId}")
    public @ResponseBody OrderConfirmation getOrderConfirmation(@PathVariable String correlationId) {
        OrderRecord orderRecord = orderRecordRepo.findByCorrelationId(correlationId);
        PaymentPlanDetails paymentPlanDetails = paymentPlanRepo.findByOrderNum(orderRecord.getOrderNum());
        OrderConfirmation orderConfirmation = ecommerceService.getOrderConfirmation(orderRecord, paymentPlanDetails);
        orderConfirmation.setUserId(
            emailToUserIdMapService.getOrCreateRecordByEmail(orderRecord.getEmail()).getUserId()
        );
        return orderConfirmation;
    }

    @GetMapping("/confirmation/penaltyWaiver/{correlationId}")
    public @ResponseBody OrderConfirmation getOrderConfirmationForPenaltyOrder(@PathVariable String correlationId) {
        PenaltyOrder penaltyOrder = penaltyRecordRepo.findByCorrelationId(correlationId);
        Double totalDebt = Double.valueOf(penaltyOrder.getPenaltyAmountWaived());

        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.setOrderId(penaltyOrder.getId().toString());
        orderConfirmation.setAmount(penaltyOrder.getAmount());
        orderConfirmation.setBillingAddress1(penaltyOrder.getBillingAddress1());
        orderConfirmation.setBillingAddress2(penaltyOrder.getBillingAddress2());
        orderConfirmation.setBillingCity(penaltyOrder.getBillingCity());
        orderConfirmation.setBillingState(penaltyOrder.getBillingState());
        orderConfirmation.setBillingZip(penaltyOrder.getBillingPostalCode());
        orderConfirmation.setEmail(penaltyOrder.getEmail());
        orderConfirmation.setPhone(penaltyOrder.getBillingPhone());
        orderConfirmation.setFirstName(penaltyOrder.getFirstName());
        orderConfirmation.setLastName(penaltyOrder.getLastName());
        orderConfirmation.setShippingAddress1(penaltyOrder.getShippingAddress1());
        orderConfirmation.setShippingAddress2(penaltyOrder.getShippingAddress2());
        orderConfirmation.setShippingCity(penaltyOrder.getShippingCity());
        orderConfirmation.setShippingState(penaltyOrder.getShippingState());
        orderConfirmation.setShippingZip(penaltyOrder.getShippingPostalCode());
        orderConfirmation.setOrderDate(penaltyOrder.getStatusLastChanged());
        orderConfirmation.setStatus(penaltyOrder.getStatus());
        orderConfirmation.setTotalDebt(new BigDecimal(totalDebt));

        orderConfirmation.setPenaltyWaiver(true);
        orderConfirmation.setPenaltyType(penaltyOrder.getPenaltyType());
        orderConfirmation.setPenaltyYear(penaltyOrder.getPenaltyTaxYear());

        orderConfirmation.setHighestPenalty(penaltyOrder.extractHighestPenalty());
        orderConfirmation.setUserId(
            emailToUserIdMapService.getOrCreateRecordByEmail(penaltyOrder.getEmail()).getUserId()
        );

        return orderConfirmation;
    }

    @PostMapping("/jwt")
    public @ResponseBody JwtPayloadResponse requestJwt(HttpSession session, @RequestBody OrderInfo orderInfo) {

        OrderRecord orderRecord = getCurrentOrder(session);

        if (orderRecord == null) {
            orderRecord = new OrderRecord();
            orderRecord.setStatus(OrderRecord.STATUS_INCOMPLETE);
            orderRecordRepo.save(orderRecord);
            session.setAttribute(PaymentPlanController.CURRENT_ORDER, orderRecord);
        }
        orderInfo.setOrderNumber(orderRecord.getOrderNum().toString());

        JwtPayloadResponse jwtPayloadResponse = new JwtPayloadResponse();
        long nowMillis = System.currentTimeMillis();
        jwtPayloadResponse.setJwt(centinelFacade.generateSignature(nowMillis, orderInfo));
        jwtPayloadResponse.setPayload(centinelFacade.generatePayload(orderInfo));

        return jwtPayloadResponse;
    }

    private final static long HONEYPOT_DELAY_MIN = 3000;
    private boolean spamBotDetected(HttpSession session, OrderInfo orderInfo) {
        // Logic is currently needed only for IRS Payment Plan, but we do this check for state specific ones too
        OrderRecord orderRecord = getCurrentOrder(session);
        long previousMillis = orderRecord.getLastUpdatedMillis() == null ? 0 : orderRecord.getLastUpdatedMillis();
        return StringUtils.isNotBlank(orderInfo.getConfirmation()) ||
                (System.currentTimeMillis() - previousMillis <= HONEYPOT_DELAY_MIN);
    }

    @PutMapping("/presave")
    public ResponseEntity<Object> preSave(HttpSession session, @RequestBody OrderInfo orderInfo) throws Exception {
        boolean spam = spamBotDetected(session, orderInfo);
        if (spam) {
            Thread.sleep(120 * 1000);
            // Thread.sleep(5 * 1000);
            return ResponseEntity.ok(IRSHoneypotRedirect.builder().redirectTo("/error/555").build());
        }

        OrderRecord orderRecord = mapOrderRecord(session, orderInfo);
        orderRecordRepo.save(orderRecord);
        session.setAttribute(PaymentPlanController.CURRENT_ORDER, orderRecord);
        return ResponseEntity.ok(orderRecord);
    }

    @RequestMapping("/downloadpdf")
    public @ResponseBody byte[] saveAsPdf() {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
        try {
            File file = new File("new.pdf");
            bytesArray = new byte[(int) file.length()];
            // read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }

    @PutMapping("/fail")
    public void fail(HttpSession session, @RequestBody OrderInfo orderInfo) throws Exception {
        OrderRecord orderRecord = mapOrderRecord(session, orderInfo);
        orderRecord.setStatus(OrderRecord.STATUS_FAILED);
        orderRecord.setNotes(orderInfo.getFailureReason());
        session.setAttribute(PaymentPlanController.CURRENT_ORDER, orderRecord);
        orderRecordRepo.save(orderRecord);
    }

    public OrderRecord mapOrderRecord(HttpSession session, OrderInfo orderInfo) {
        OrderRecord orderRecord = getCurrentOrder(session);
        if (orderRecord == null) {
            if(orderInfo.getId() != null && !"".equals(orderInfo.getId()) ) {
                orderRecord = orderRecordRepo.findById(Long.valueOf(orderInfo.getId())).orElse(null);
            }

            if(orderRecord == null) {
                orderRecord = new OrderRecord();
                orderRecord.setStatus(OrderRecord.STATUS_INCOMPLETE);
                orderRecordRepo.save(orderRecord);
            }
        }

        mapOrderInfo(session, orderInfo, orderRecord);

        if (orderInfo.getCardNumber() != null) {
            orderInfo.setCardNumber(orderInfo.getCardNumber().replaceAll("[^0-9]", ""));
        }

        autoPopulateMissingData(orderInfo);

        orderRecord.setProduct(orderInfo.getProduct());
        orderRecord.setCorrelationId(UUID.randomUUID().toString());
        orderRecord.setFirstName(orderInfo.getBillingFirstName());
        orderRecord.setLastName(orderInfo.getBillingLastName());
        orderRecord.setPhone(orderInfo.getBillingPhone());
        orderRecord.setSecondaryPhone(orderInfo.getSecondaryPhone());
        orderRecord.setEmail(orderInfo.getEmail());
        orderRecord.setSsn(orderInfo.getSsn());
        orderRecord.setEin(orderInfo.getEin());
        orderRecord.setBusinessName(orderInfo.getBusinessName());
        orderRecord.setDba(orderInfo.getDba());
        orderRecord.setIllinoisAccountId(orderInfo.getIllinoisAccountId());
        orderRecord.setMobile(orderInfo.getMobile());
        orderRecord.setGoodFaithPayment(orderInfo.getGoodFaithPayment());
        orderRecord.setIsOwedFromBusiness(orderInfo.getIsOwedFromBusiness());
        orderRecord.setTotalDebt(orderInfo.getTotalDebt());
        orderRecord.setIsCalifornia(orderInfo.getIsCalifornia());
        orderRecord.setIsNewJersey(orderInfo.getIsNewJersey());
        orderRecord.setIsGeorgia(orderInfo.getIsGeorgia());
        orderRecord.setIsIllinois(orderInfo.getIsIllinois());
        orderRecord.setIsMichigan(orderInfo.getIsMichigan());
        orderRecord.setPaymentMonths(orderInfo.getPaymentMonths());

        orderRecord.setBillingAddress1(orderInfo.getBillingAddress1());
        orderRecord.setBillingAddress2(orderInfo.getBillingAddress2());
        orderRecord.setBillingCity(orderInfo.getBillingCity());
        orderRecord.setBillingState(orderInfo.getBillingState());
        orderRecord.setBillingZip(orderInfo.getBillingPostalCode());

        orderRecord.setShippingAddress1(orderInfo.getShippingAddress1());
        orderRecord.setShippingAddress2(orderInfo.getShippingAddress2());
        //String validAddr2 = validateShippingAddr2(orderInfo.getShippingAddress2());
        //orderRecord.setShippingAddress2(validAddr2);

        orderRecord.setShippingCity(orderInfo.getShippingCity());
        orderRecord.setShippingState(orderInfo.getShippingState());
        orderRecord.setShippingZip(orderInfo.getShippingPostalCode());

        orderRecord.setEmployerName(orderInfo.getEmployerName());
        orderRecord.setEmployerAddress1(orderInfo.getEmployerAddress1());
        orderRecord.setEmployerAddress2(orderInfo.getEmployerAddress2());
        orderRecord.setEmployerCity(orderInfo.getEmployerCity());
        orderRecord.setEmployerState(orderInfo.getEmployerState());
        orderRecord.setEmployerZip(orderInfo.getEmployerZip());
        orderRecord.setPayFrequency(orderInfo.getPayFrequency());
        orderRecord.setEmployerContactName(orderInfo.getEmployerContactName());
        orderRecord.setEmployerContactPhoneNumber(orderInfo.getEmployerContactPhoneNumber());
        orderRecord.setPayrollDeduction(orderInfo.getPayrollDeduction());

        orderRecord.setBusinessAddress1(orderInfo.getBusinessAddress1());
        orderRecord.setBusinessAddress2(orderInfo.getBusinessAddress2());
        orderRecord.setBusinessCity(orderInfo.getBusinessCity());
        orderRecord.setBusinessState(orderInfo.getBusinessState());
        orderRecord.setBusinessZip(orderInfo.getBusinessPostalCode());
        orderRecord.setPhysNotMailing(orderInfo.getPhysNotMailing());

        orderRecord.setAmount(orderInfo.getAmount());
        orderRecord.setPartnerCode(orderInfo.getPartnerCode());
        orderRecord.setProcessingSpeed(orderInfo.getProcessingSpeed());
        orderRecord.setHasOldAddress(orderInfo.getHasOldAddress());
        orderRecord.setOldAddress1(orderInfo.getOldAddress1());
        orderRecord.setOldAddress2(orderInfo.getOldAddress2());
        orderRecord.setOldCity(orderInfo.getOldCity());
        orderRecord.setOldState(orderInfo.getOldState());
        orderRecord.setOldZip(orderInfo.getOldZip());

        orderRecord.setHasPriorNames(orderInfo.getHasPriorNames());
        orderRecord.setPriorNames(orderInfo.getPriorNames());
        // Set order status to process if the amount is zero
        if ( orderInfo.getAmount().equals("0") ) {
            orderRecord.setStatus(OrderRecord.STATUS_PROCESSING);
        }

        orderRecord.setSubmissionDate(new Date());
        orderRecord.setBankName(orderInfo.getBankName());
        orderRecord.setBankAddress1(orderInfo.getBankAddress1());
        orderRecord.setBankAddress2(orderInfo.getBankAddress2());
        orderRecord.setBankCity(orderInfo.getBankCity());
        orderRecord.setBankState(orderInfo.getBankState());
        orderRecord.setBankZip(orderInfo.getBankZip());
        orderRecord.setBusinessEntityType(orderInfo.getBusinessEntityType());
        orderRecord.setTreasuryAccountNumber(orderInfo.getTreasuryAccountNumber());
        if (CollectionUtils.isNotEmpty(orderInfo.getAssessmentNumbers())) {
            orderRecord.setAssessmentNumber(String.join(",", orderInfo.getAssessmentNumbers()));
        }

        return orderRecord;
    }

    private void mapOrderInfo(HttpSession session, OrderInfo orderInfo, OrderRecord orderRecord) {
        if (TaxConstants.PAYMENTPLAN.equals(orderInfo.getProduct())) {
            if (orderInfo.getIsCalifornia() != null && orderInfo.getIsCalifornia() == true) {
                orderInfo.setItem_Name_1("California Payment Plan");
                orderInfo.setItem_Desc_1("Custom California payment plan from 97tax.com");
            } else if (orderInfo.getIsNewJersey() != null && orderInfo.getIsNewJersey() == true) {
                orderInfo.setItem_Name_1("New Jersey Payment Plan");
                orderInfo.setItem_Desc_1("Custom New Jersey payment plan from 97tax.com");
            } else if (orderInfo.getIsGeorgia() != null && orderInfo.getIsGeorgia() == true) {
                orderInfo.setItem_Name_1("Georgia Payment Plan");
                orderInfo.setItem_Desc_1("Custom Georgia payment plan from 97tax.com");
            } else if (orderInfo.getIsIllinois() != null && orderInfo.getIsIllinois() == true) {
                orderInfo.setItem_Name_1("Illinois Payment Plan");
                orderInfo.setItem_Desc_1("Custom Illinois payment plan from 97tax.com");
            } else if (orderInfo.getIsMichigan() != null && orderInfo.getIsMichigan() == true) {
                orderInfo.setItem_Name_1("Michigan Payment Plan");
                orderInfo.setItem_Desc_1("Custom Michigan payment plan from 97tax.com");
            }  else {
                orderInfo.setItem_Name_1("IRS Payment Plan");
                orderInfo.setItem_Desc_1("Custom IRS payment plan from 97tax.com");
            }
            orderInfo.setItem_Quantity_1("1");
            orderInfo.setItem_Price_1(orderInfo.getAmount());
            orderInfo.setStatus(orderRecord.getStatus());

            PaymentPlanDetails deets = paymentPlanRepo.findByOrderNum(orderRecord.getOrderNum());
            if (deets == null) {
                deets = new PaymentPlanDetails();
            }
            deets.setOrderNum(orderRecord.getOrderNum());
            deets.setFirstName(orderInfo.getBillingFirstName());
            deets.setLastName(orderInfo.getBillingLastName());
            deets.setEmail(orderInfo.getEmail());
            deets.setPhone(orderInfo.getBillingPhone());
            deets.setSecondaryPhone(orderInfo.getSecondaryPhone());
            deets.setIsOwedFromBusiness(orderInfo.getIsOwedFromBusiness());
            deets.setBusinessName(orderInfo.getBusinessName());
            deets.setEin(orderInfo.getEin());
            deets.setDba(orderInfo.getDba());
            deets.setIllinoisAccountId(orderInfo.getIllinoisAccountId());
            deets.setMobile(orderInfo.getMobile());
            deets.setGoodFaithPayment(orderInfo.getGoodFaithPayment());
            deets.setMarried("true".equals(orderInfo.getMarried()));
            deets.setSpouseFirstName(orderInfo.getSpouseFirstName());
            deets.setSpouseLastName(orderInfo.getSpouseLastName());
            deets.setSpouseSsn(orderInfo.getSpouseSsn());
            deets.setPaymentDayOfMonth(orderInfo.getPaymentDayOfMonth());
            deets.setTimeToCall(orderInfo.getTimeToCall());
            deets.setTotalDebt(orderInfo.getTotalDebt());
            deets.setIsCalifornia(orderInfo.getIsCalifornia());
            deets.setIsNewJersey(orderInfo.getIsNewJersey());
            deets.setIsGeorgia(orderInfo.getIsGeorgia());
            deets.setIsMichigan(orderInfo.getIsMichigan());
            deets.setIsIllinois(orderInfo.getIsIllinois());
            deets.setPaymentMonths(orderInfo.getPaymentMonths());
            deets.setPayrollDeduction(orderInfo.getPayrollDeduction());
            deets.setProcessingSpeed(orderInfo.getProcessingSpeed());
            Boolean isIllinois= orderInfo.getIsIllinois();
            if(isIllinois!=null && isIllinois){
                deets.setMonthlyPayment(deets.getMonthlyPayment());
            }else{
                deets.setMonthlyPayment(
                    PaymentPlanController.calculatePaymentAmount(orderInfo.getTotalDebt(), orderInfo.getPaymentMonths()));
            }

            paymentPlanRepo.save(deets);
        } else if ("TaxLienRemoval".equals(orderInfo.getProduct())) {
            orderInfo.setItem_Name_1("IRS Tax Lien Removal");
            orderInfo.setItem_Desc_1("Custom IRS tax lien removal plan from 97tax.com");
            orderInfo.setItem_Quantity_1("1");
            orderInfo.setItem_Price_1(orderInfo.getAmount());

            TaxLienRemovalDetails deets = taxLienRemovalRepo.getByOrderNum(orderRecord.getOrderNum());
            if (deets == null) {
                deets = new TaxLienRemovalDetails();
                deets.setOrderNum(orderRecord.getOrderNum());
            }

            deets.setFirstname(orderInfo.getBillingFirstName());
            deets.setLastname(orderInfo.getBillingLastName());
            deets.setPhone(orderInfo.getBillingPhone());
            deets.setEmail(orderInfo.getEmail());
            deets.setTaxLienIsBusiness(orderInfo.getTaxLienIsBusiness());
            deets.setTaxLienBusinessEin(orderInfo.getTaxLienBusinessEin());
            deets.setTaxLienBusinessName(orderInfo.getTaxLienBusinessName());
            deets.setTaxLienRemediationDescription(orderInfo.getTaxLienRemediationDescription());
            deets.setTaxLienAutomaticDebit(orderInfo.getTaxLienAutomaticDebit());
            deets.setTaxLienRemediationType(orderInfo.getTaxLienRemediationType());
            deets.setTaxLienType(orderInfo.getTaxLienType());
            deets.setSerialNumber(orderInfo.getSerialNumber());

            taxLienRemovalRepo.save(deets);

        } else if ("OfferInCompromise".equals(orderInfo.getProduct())) {
            orderInfo.setAmount("39700");
            orderInfo.setItem_Name_1("IRS Offer-In-Compromise");
            orderInfo.setItem_Desc_1("Custom IRS offer-in-compromise from 97tax.com");
            orderInfo.setItem_Quantity_1("1");
            orderInfo.setItem_Price_1("39700");

            String oicKey = (String) session.getAttribute(OicController.OIC_KEY);
            OicModel oic = oicRepository.getByKey(oicKey);
            oic.setOrderNum(orderRecord.getOrderNum());
            oicRepository.save(oic);
        } else {
            throw new RuntimeException("Invalid product");
        }
    }

    @PutMapping("/order/id/{orderNum}/update")
    public void updateOrder(HttpSession session, @PathVariable Long orderNum, @RequestBody OrderInfo orderInfo) {
    	Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            OrderRecord existingRecord = optRecord.get();
            existingRecord.setUpsellShown(orderInfo.getUpsellShown());
            existingRecord.setUpsellClicked(orderInfo.getUpsellClicked());
            existingRecord.setUpsellProduct(orderInfo.getUpsellProduct());
            orderRecordRepo.save(existingRecord); 
        }
    }
    @GetMapping("/orders/listInProcessAndComplete/{orderNum}")
    public List<OrderRecord> getOrderListByEmail(HttpSession session, @PathVariable Long orderNum) {
    	List<OrderRecord> orderInProcessAndComplete = new ArrayList<>();
        try {
        	Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
            if (optRecord.isPresent()) {
            	orderInProcessAndComplete = orderRecordRepo
    					.findByStatusAndEmailAndProduct(optRecord.get().getEmail(),
    							Arrays.asList(OrderRecord.STATUS_PROCESSING, OrderRecord.STATUS_COMPLETE),
    							optRecord.get().getProduct());
            }
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            log.warn("Invalid query data", e);
        }
		return orderInProcessAndComplete;
    }
    
    private void sendIncompleteEmail(OrderRecord orderRecord) {
        try {
            OrderSearchQuery orderSearchQuery = new OrderSearchQuery();
            orderSearchQuery.setEmail(orderRecord.getEmail());
            orderSearchQuery.setCreatedAt(orderRecord.getCreatedDate().toString());
            orderSearchQuery.setProduct(orderRecord.getProduct());

            OrderSearchStatuses statuses = new OrderSearchStatuses();
            statuses.setFailed(true);
            statuses.setIncomplete(false);
            orderSearchQuery.setStatus(statuses);

            Optional<OrderRecord> order = orderRecordRepo
                    .findOne(OrderSpecs.orderSearchForIncompleteOrFailedEmail(orderSearchQuery));
            if (!order.isPresent()) {
                if (TaxConstants.PAYMENTPLAN.equals(orderRecord.getProduct())) {
                    PaymentPlanDetails deets = paymentPlanRepo.findByOrderNum(orderRecord.getOrderNum());
                    mailjetSender.sendPaymentPlanFailedEmail(orderRecord, deets);
                } else if (TaxConstants.TAXLIEN_REMOVAL.equals(orderRecord.getProduct())) {
                    mailjetSender.sendTaxLienFailedEmail(orderRecord);
                }
            }
        } catch (Exception e) {
            log.error("ERROR: occurred while sending incomplete mail", e);
        }
    }

    @PostMapping("/authorize")
    public @ResponseBody AuthorizeResult authorizeTransaction(HttpSession session, HttpServletRequest httpServletRequest, @RequestBody OrderInfo orderInfo) throws ParseException {
        List<OrderRecord> potentialDupes = orderService.findPossibleDuplicate(orderInfo);
        if (potentialDupes.size() > 0) {
            throw new ConflictException();
        }

        Map payload = centinelFacade.validateJwt(orderInfo.getResponseJwt());
        log.debug("Payload JWT : {}", payload);
        Map paymentMap = (Map) payload.get("Payment");
        Map extendedDataMap = (Map) paymentMap.get("ExtendedData");
        String eciFlag = (String) extendedDataMap.get("ECIFlag");
        String enrolled = (String) extendedDataMap.get("Enrolled");
        OrderRecord orderRecord = getCurrentOrder(session);

        if (orderRecord == null) {
            orderRecord = new OrderRecord();
            orderRecord.setStatus(OrderRecord.STATUS_INCOMPLETE);
            orderRecordRepo.save(orderRecord);
        }

        List<String> validStatuses = Arrays.asList(OrderRecord.STATUS_INCOMPLETE, OrderRecord.STATUS_FAILED);
        if (!validStatuses.contains(orderRecord.getStatus())) {
            throw new ConflictException();
        }

        mapOrderInfo(session, orderInfo, orderRecord);

        orderRecord = toOrderRecord(orderRecord, orderInfo, httpServletRequest.getHeader("x-forwarded-for"));
        orderRecordRepo.save(orderRecord);

        Optional<OrderLock> orderLockOptional = orderLockRepository.findByOrderId(orderRecord.getOrderNum());
        OrderLock orderLock = orderLockOptional.orElse(null);
        if (orderLock == null) {
            orderLock = new OrderLock();
            orderLock.setOrderId(orderRecord.getOrderNum());
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

        orderInfo.setOrderNumber(orderRecord.getOrderNum().toString());
        orderRecord.setAuthErrorNo(((Integer) payload.get("ErrorNumber")).toString());
        orderRecord.setAuthErrorDesc((String) payload.get("ErrorDescription"));
        orderRecord.setTransactionId((String) paymentMap.get("ProcessorTransactionId"));
        orderRecord.setPayloadResponseStatus((String) extendedDataMap.get("PAResStatus"));
        orderRecord.setSignatureVerification((String) extendedDataMap.get("SignatureVerification"));
        orderRecord.setAuthEci(eciFlag);
        orderRecord.setEciFlag(eciFlag);
        if (extendedDataMap.get("CAVV") != null) {
            orderRecord.setAuthCavv((String) extendedDataMap.get("CAVV"));
        } else if (extendedDataMap.get("AAV") != null) {
            orderRecord.setAuthCavv((String) extendedDataMap.get("AVV"));
        }
        orderRecord.setAuthXid((String) extendedDataMap.get("XID"));
        orderRecord.setEnrolled((String) extendedDataMap.get("Enrolled"));

        AuthorizeResult authorizeResult = new AuthorizeResult();
        authorizeResult.setCorrelationId(orderRecord.getCorrelationId());

        SimpleDateFormat formatter1=new SimpleDateFormat("M/d/yyyy hh:mm:ss a");
        Set<Partner> partners = new HashSet<>();
        if (CollectionUtils.isNotEmpty(orderInfo.getPartners())) {
            for (PartnerInfo partnerInfo : orderInfo.getPartners()) {
                Partner partner = new Partner();
                BeanUtils.copyProperties(partnerInfo, partner, "");
                String date = partnerInfo.getPartnerEffectiveMonth() + "/" + partnerInfo.getPartnerEffectiveDay() +
                        "/" + partnerInfo.getPartnerEffectiveYear();
                partner.setPartnerEffectiveDate(formatter1.parse(date + " 08:00:00 AM"));
                partner.setOrderRecordId(orderRecord);

                partners.add(partner);
            }
            orderRecord.setPartners(partners);
        }

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

        if ("00".equals(eciFlag) || "07".equals(eciFlag) /*|| !"Y".equals(enrolled) || ("06".equals(eciFlag) && !"Y".equals(enrolled))*/) {
            log.error("ECI Flag is: " + eciFlag);
            String errMsg = "Authentication is unsuccessful or not attempted. The credit card is either a non-3D card or card issuing bank does not handle it as a 3D transaction.";
            exception_message = errMsg;

            authorizeResult.setSuccess(false);
            authorizeResult.setMessage(errMsg);

            orderRecord.setStatus(OrderRecord.STATUS_FAILED);
            orderRecord.setNotes(authorizeResult.getMessage());

            orderLockRepository.delete(orderLock);  // allow retry if failed
        } else {
            try {
                CreatePaymentRequest buildPaymentRequest = createPaymentService.buildPaymentRequest(orderInfo);
                orbital_payment_response = createPaymentService.createPayment4Logging(buildPaymentRequest);

                CreatePaymentResponse createPaymentResponse = gson.fromJson(orbital_payment_response, CreatePaymentResponse.class);

                String approvalStatus = createPaymentResponse.getOrder().getStatus().getApprovalStatus();
                String respCodeMessage = createPaymentResponse.getOrder().getStatus().getRespCodeMessage();
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
                    log.error("Error in authorize");

                    exception_message = "Error in authorize: approvalStatus is not 1";

                    orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                    authorizeResult.setSuccess(false);
                    authorizeResult.setMessage("There was an unexpected error on card transaction.");

                    orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                    orderRecord.setNotes(authorizeResult.getMessage());

                    orderLockRepository.delete(orderLock);  // allow retry if failed
                }

                try {
                    mailjetSender.sendProcessingEmail(orderRecord);
                } catch (Exception e) {
                    log.error("Unable to send confirmation email ", e);
                }
            } catch (Exception e) {
                log.error("Error in authorize", e);

                exception_message = "Error in authorize: " + e.getMessage();

                authorizeResult.setSuccess(false);
                authorizeResult.setMessage("Card Transaction Failed!");

                orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                orderRecord.setNotes(authorizeResult.getMessage());

                orderLockRepository.delete(orderLock);  // allow retry if failed
            }
        }

        orderRecordRepo.save(orderRecord);

        TransactionLogs trxLog = new TransactionLogs();
        trxLog.setOrder_id(orderRecord.getOrderNum());
        trxLog.setCardinal_response(payload_response);
        trxLog.setOrbital_payment_response(orbital_payment_response);
        trxLog.setException_message(exception_message);
        trxLog.setCreatedDate(new Date());

        transactionLogsRepo.save(trxLog);

        httpServletRequest.getSession().setAttribute(PaymentPlanController.CURRENT_ORDER, null);
        httpServletRequest.getSession().setAttribute("orderInfo", null);
        httpServletRequest.getSession().setAttribute(OicController.OIC_KEY, null);

        // Send email if processing or completed order for same email does not exist
        // after 30min
        if (OrderRecord.STATUS_FAILED.equals(orderRecord.getStatus())
                && (TaxConstants.PAYMENTPLAN.equals(orderInfo.getProduct())
                        || TaxConstants.TAXLIEN_REMOVAL.equals(orderInfo.getProduct()))) {

            final OrderRecord tempOrderRecord = orderRecord;
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

            timer1.schedule(delayedThreadStartTask1, 30 * 60 * 1000);
            timer2.schedule(delayedThreadStartTask2, 3 * 24 * 3600 * 1000);
        }

        return authorizeResult;
    }

    private void autoPopulateMissingData(@RequestBody OrderInfo orderInfo) {
        if (StringUtils.isBlank(orderInfo.getShippingAddress1())) {
            orderInfo.setShippingAddress1(orderInfo.getBillingAddress1());
            orderInfo.setShippingAddress2(orderInfo.getBillingAddress2());
            orderInfo.setShippingCity(orderInfo.getBillingCity());
            orderInfo.setShippingState(orderInfo.getBillingState());
            orderInfo.setShippingPostalCode(orderInfo.getBillingPostalCode());
        }

        if (StringUtils.isBlank(orderInfo.getBillingAddress1())) {
            orderInfo.setBillingAddress1(orderInfo.getShippingAddress1());
            orderInfo.setBillingAddress2(orderInfo.getShippingAddress2());
            orderInfo.setBillingCity(orderInfo.getShippingCity());
            orderInfo.setBillingState(orderInfo.getShippingState());
            orderInfo.setBillingPostalCode(orderInfo.getShippingPostalCode());
        }

        if (StringUtils.isBlank(orderInfo.getShippingFirstName())) {
            orderInfo.setShippingFirstName(orderInfo.getBillingFirstName());
            orderInfo.setShippingLastName(orderInfo.getBillingLastName());
        }
        if(orderInfo.getPhysNotMailing() != null && orderInfo.getPhysNotMailing().equals(false)){
            orderInfo.setBusinessAddress1(orderInfo.getShippingAddress1());
            orderInfo.setBusinessAddress2(orderInfo.getShippingAddress2());
            orderInfo.setBusinessCity(orderInfo.getShippingCity());
            orderInfo.setBusinessState(orderInfo.getShippingState());
            orderInfo.setBusinessPostalCode(orderInfo.getShippingPostalCode());
        }
    }

    private OrderRecord toOrderRecord(OrderRecord orderRecord, OrderInfo orderInfo, String ipAddress) {
        if (orderInfo.getCardNumber() != null) {
            orderInfo.setCardNumber(orderInfo.getCardNumber().replaceAll("[^0-9]", ""));
        }

        autoPopulateMissingData(orderInfo);

        orderRecord.setProduct(orderInfo.getProduct());

        orderRecord.setCorrelationId(UUID.randomUUID().toString());
        orderRecord.setFirstName(orderInfo.getBillingFirstName());
        orderRecord.setLastName(orderInfo.getBillingLastName());
        orderRecord.setPhone(orderInfo.getBillingPhone());
        orderRecord.setEmail(orderInfo.getEmail());
        orderRecord.setSsn(orderInfo.getSsn());
        orderRecord.setEin(orderInfo.getEin());
        orderRecord.setIsOwedFromBusiness(orderInfo.getIsOwedFromBusiness());
        orderRecord.setBusinessName(orderInfo.getBusinessName());
        orderRecord.setDba(orderInfo.getDba());
        orderRecord.setIllinoisAccountId(orderInfo.getIllinoisAccountId());
        orderRecord.setMobile(orderInfo.getMobile());
        orderRecord.setGoodFaithPayment(orderInfo.getGoodFaithPayment());
        orderRecord.setTotalDebt(orderInfo.getTotalDebt());

        orderRecord.setBillingAddress1(orderInfo.getBillingAddress1());
        orderRecord.setBillingAddress2(orderInfo.getBillingAddress2());
        orderRecord.setBillingCity(orderInfo.getBillingCity());
        orderRecord.setBillingState(orderInfo.getBillingState());
        orderRecord.setBillingZip(orderInfo.getBillingPostalCode());
        orderRecord.setShippingAddress1(orderInfo.getShippingAddress1());
        orderRecord.setShippingAddress2(orderInfo.getShippingAddress2());
        orderRecord.setShippingCity(orderInfo.getShippingCity());
        orderRecord.setShippingState(orderInfo.getShippingState());
        orderRecord.setShippingZip(orderInfo.getShippingPostalCode());
        orderRecord.setBusinessAddress2(orderInfo.getBusinessAddress2());
        orderRecord.setBusinessCity(orderInfo.getBusinessCity());
        orderRecord.setBusinessState(orderInfo.getBusinessState());
        orderRecord.setBusinessZip(orderInfo.getBusinessPostalCode());
        orderRecord.setPhysNotMailing(orderInfo.getPhysNotMailing());
        orderRecord.setAmount(orderInfo.getAmount());
        orderRecord.setSubmissionDate(new Date());
        orderRecord.setCustomerIpAddress(ipAddress);

        return orderRecord;
    }
}
