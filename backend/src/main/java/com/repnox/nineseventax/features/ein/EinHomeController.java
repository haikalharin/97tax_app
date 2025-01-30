package com.repnox.nineseventax.features.ein;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.gson.Gson;
import com.repnox.nineseventax.common.PaginationResponse;
import com.repnox.nineseventax.exceptions.ConflictException;
import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.alerts.model.AlertConfig;
import com.repnox.nineseventax.features.alerts.model.AlertConfigRepo;
import com.repnox.nineseventax.features.auth.AuthRole;
import com.repnox.nineseventax.features.auth.AuthService;
import com.repnox.nineseventax.features.aws.interfaces.UploadFileToAwsService;
import com.repnox.nineseventax.features.signatureapi.SignatureAPIService;
import com.repnox.nineseventax.features.signatureapi.SignatureAPIService.SignatureAPIRedirect;
import com.repnox.nineseventax.features.ecommerce.*;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCreatePaymentService;
import com.repnox.nineseventax.features.ecommerce.util.JwtPayloadResponse;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.payment.request.CreatePaymentRequest;
import com.repnox.nineseventax.features.payment.response.CreatePaymentResponse;
import com.repnox.nineseventax.features.payment.response.OrderResponse;
import com.repnox.nineseventax.features.payment.response.StatusResponse;
import com.repnox.nineseventax.features.pdfcreator.PdfFormFilling;
import com.repnox.nineseventax.features.pdforderversions.PdfEmail;
import com.repnox.nineseventax.features.pdforderversions.PdfOrderVersions;
import com.repnox.nineseventax.features.pdforderversions.PdfOrderVersionsRepo;
import com.repnox.nineseventax.features.utils.DateUtil;
import com.repnox.nineseventax.features.utils.TaxConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

import static com.repnox.nineseventax.features.email.MailjetTemplates.einCompleteTemplateId;

@RestController
@RequestMapping("/api/ein")
public class EinHomeController {

    private static final Logger logger = LoggerFactory.getLogger(EinHomeController.class);

    @Autowired
    EinRecordRepo einRecordRepo;

    @Autowired
    EinBotLogRepo einBotLogRepo;

    @Autowired
    private PdfOrderVersionsRepo pdfOrderVersionRepo;

    @Autowired
    private CentinelFacade centinelFacade;

    @Autowired
    private TransactionLogsRepo transactionLogsRepo;

    @Autowired
    private OrbitalCreatePaymentService createPaymentService;

    @Autowired
    private MailjetSender mailjetSender;

    @Autowired
    private PdfFormFilling pdfCreator;

    @Autowired
    private SignatureAPIService signatureAPIService;

    @Autowired
    private PdfEmail pdfEmail;

    @Autowired
    private AlertConfigRepo alertConfigRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private UploadFileToAwsService uploadFileToAwsService;

    @Autowired
    private EmailToUserIdMapService emailToUserIdMapService;

    @Autowired
    private OrderLockRepository orderLockRepository;

    @Value("${aws.bucket.name}")
    private String awsBucketName;

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    public String getPdfBase64(@PathVariable String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {

        File f = ResourceUtils.getFile("classpath:servicepdf/" + filename);

        String path = f.getAbsolutePath();
        byte[] input_file = Files.readAllBytes(Paths.get(path));

        byte[] encodedBytes = Base64.getEncoder().encode(input_file);
        String encodedString = new String(encodedBytes);

        return encodedString;
    }

    private static final String PAGE_APPLICATION_SUMMARY = "application-summary";

    @PostMapping("/save/{id}")
    public @ResponseBody String saveEINOrder(@PathVariable Long id, @RequestBody EinOrder newData) {
        EinOrder order = einRecordRepo.findById(id).orElse(null);
        if (order == null) {
            logger.error("ERROR: Not Found Order: saveOrder(): 810");
            throw new NotFoundException();
        }

        copyEinRecord(newData, order);
        einRecordRepo.save(order);
        return "Successful";
    }

    private EinOrder copyEinRecord(EinOrder src, EinOrder dest) {
        dest.setOrder_type(StringUtils.trimToNull(src.getOrder_type()));
        dest.setSub_type(StringUtils.trimToNull(src.getSub_type()));
        dest.setReason(src.getReason());
        dest.setLlc_number_members(src.getLlc_number_members());
        dest.setFirst_name(src.getFirst_name());
        dest.setMiddle_name(src.getMiddle_name());
        dest.setNo_middle_name(src.getNo_middle_name());
        dest.setLast_name(src.getLast_name());
        dest.setTitle(src.getTitle());
        dest.setIs_sec645(src.getIs_sec645());
        dest.setSuffix(StringUtils.trimToNull(src.getSuffix()));

        dest.setSecondary_first_name(src.getSecondary_first_name());
        dest.setSecondary_middle_name(src.getSecondary_middle_name());
        dest.setSecondary_no_middle_name(src.getSecondary_no_middle_name());
        dest.setSecondary_last_name(src.getSecondary_last_name());
        dest.setSecondary_suffix(StringUtils.trimToNull(src.getSecondary_suffix()));
        dest.setSecondary_ssn(src.getSecondary_ssn());

        dest.setEmail(src.getEmail());
        dest.setSsn(src.getSsn());
        dest.setAddress(src.getAddress());
        dest.setApt_suite(src.getApt_suite());
        dest.setCity(src.getCity());
        dest.setState(src.getState());
        dest.setCounty(src.getCounty());
        dest.setZip_code(src.getZip_code());
        dest.setPhone_number(src.getPhone_number());
        dest.setIs_diff_mailing_address(src.getIs_diff_mailing_address());
        dest.setMailing_address(src.getMailing_address());
        dest.setMailing_apt_suite(src.getMailing_apt_suite());
        dest.setMailing_city(src.getMailing_city());
        dest.setMailing_country(src.getMailing_country());
        dest.setMailing_state(src.getMailing_state());
        dest.setMailing_zip_code(src.getMailing_zip_code());
        dest.setRequest_reason(StringUtils.trimToNull(src.getRequest_reason()));
        dest.setBusiness_country(src.getBusiness_country());
        dest.setBusiness_state(src.getBusiness_state());
        dest.setBusiness_type(StringUtils.trimToNull(src.getBusiness_type()));
        dest.setBusiness_sub_type(StringUtils.trimToNull(src.getBusiness_sub_type()));
        dest.setBusiness_sub_type_2(StringUtils.trimToNull(src.getBusiness_sub_type_2()));
        dest.setBusiness_sub_type_3(StringUtils.trimToNull(src.getBusiness_sub_type_3()));
        dest.setBusiness_sub_type_4(StringUtils.trimToNull(src.getBusiness_sub_type_4()));
        dest.setBusiness_details(src.getBusiness_details());
        dest.setIs_previous_ein(src.getIs_previous_ein());
        dest.setPrevious_ein(src.getPrevious_ein());
        dest.setStart_date_month(src.getStart_date_month());
        dest.setStart_date_year(src.getStart_date_year());
        dest.setLegal_name(src.getLegal_name());
        dest.setState_incorporated(src.getState_incorporated());
        dest.setIs_same_physical_address(src.getIs_same_physical_address());
        dest.setAccounting_close_month(src.getAccounting_close_month());
        dest.setReit_type(StringUtils.trimToNull(src.getReit_type()));
        dest.setIs_atf(src.getIs_atf());
        dest.setIs_gambling(src.getIs_gambling());
        dest.setIs_excise_tax_form720(src.getIs_excise_tax_form720());
        dest.setIs_large_motor_vehicle(src.getIs_large_motor_vehicle());
        dest.setIs_w2_employees(src.getIs_w2_employees());
        dest.setDate_first_wages_month(src.getDate_first_wages_month());
        dest.setDate_first_wages_year(src.getDate_first_wages_year());
        dest.setMax_ees_next12mos_agri(src.getMax_ees_next12mos_agri());
        dest.setMax_ees_next12mos_household(src.getMax_ees_next12mos_household());
        dest.setMax_ees_next12mos_other(src.getMax_ees_next12mos_other());
        dest.setIs_employment_tax_liability(src.getIs_employment_tax_liability());
        dest.setCard_partner_code(src.getCard_partner_code());
        dest.setCard_holder_name(src.getCard_holder_name());
        dest.setCard_number(src.getCard_number());
        dest.setCard_expire_date(src.getCard_expire_date());
        dest.setCard_cvc(src.getCard_cvc());
        dest.setAmount(src.getAmount());
        dest.setLast_saved_page(src.getLast_saved_page());

        return dest;
    }

    @GetMapping("/fss4/{id}")
    public void getFormSS4PDF(@PathVariable Long id, HttpServletResponse response) throws IOException {
        EinOrder order = einRecordRepo.findById(id).orElse(null);

        InputStream is = pdfCreator.PdfCreatorForFSS4(order, true);
        long len = is.available();
        response.setContentType(MediaType.APPLICATION_PDF.getType());
        response.setHeader("Content-Disposition", "attachment; filename=fss4.pdf");
        response.setContentLength((int) len);

        FileCopyUtils.copy(is, response.getOutputStream());
    }

    @GetMapping("/fss4/{id}/start-sign")
    public @ResponseBody SignatureAPIRedirect startFormSS4Sign(@PathVariable Long id, HttpServletResponse response) throws Exception {
        EinOrder order = einRecordRepo.findById(id).orElse(null);

        SignatureAPIRedirect redirect = signatureAPIService.getFSS4Redirect(order);

        String redirectEnvelopeId = redirect.getEnvelopeId();
        if (!StringUtils.equals(order.getDsEnvelopeId(), redirectEnvelopeId)) { // new envelope created
            order.setDsEnvelopeId(redirectEnvelopeId);
            order.setDsEnvelopeMillis(System.currentTimeMillis());
        }

        order.setStatus(OrderRecord.STATUS_AWAITING_SIGNATURE_SERVICE);
        einRecordRepo.save(order);

        Timer timer = new Timer();
        TimerTask delayedTask = new TimerTask() {
            @Override
            public void run() {
                EinOrder theOrder = einRecordRepo.findById(order.getId()).orElse(null);
                // if not signed after 30 minutes - send email
                if (theOrder != null && OrderRecord.STATUS_AWAITING_SIGNATURE_SERVICE.equals(theOrder.getStatus())) {
                    mailjetSender.sendEinAwaitingSignatureServiceEmail(theOrder);
                }
            }
        };
        timer.schedule(delayedTask, 30 * 60 * 1000); // start after 30 minutes delay

        return redirect;
    }

    @PostMapping("/jwt")
    public @ResponseBody JwtPayloadResponse requestJwt(HttpSession session, @RequestBody EinOrder orderInfo) {
        EinOrder order = einRecordRepo.findById(orderInfo.getId()).orElse(null);
        if (null == order) {
            logger.error("ERROR: Not Found Order: requestJwt(): 810");
            throw new NotFoundException();
        }

        List<String> validStatuses = Arrays.asList(OrderRecord.STATUS_SIGNED, OrderRecord.STATUS_FAILED);
        if (!validStatuses.contains(order.getStatus())) {
            throw new ConflictException();
        }

        order.setCorrelationId(UUID.randomUUID().toString());
        order.setCard_holder_name(orderInfo.getCard_holder_name());
        order.setCard_number(orderInfo.getCard_number());
        order.setCard_expire_date(orderInfo.getCard_expire_date());
        order.setCard_cvc(orderInfo.getCard_cvc());
        order.setBusiness_state(orderInfo.getBusiness_state());
        einRecordRepo.save(order);

        JwtPayloadResponse jwtPayloadResponse = new JwtPayloadResponse();
        long nowMillis = System.currentTimeMillis();
        jwtPayloadResponse.setJwt(centinelFacade.generateSignature(nowMillis, order));
        jwtPayloadResponse.setPayload(centinelFacade.generatePayload(order));

        return jwtPayloadResponse;
    }

    @GetMapping("/id/{orderNum}")
    public @ResponseBody EinOrder getCurrentOrder(HttpSession session, @PathVariable Long orderNum) {
        EinOrder order = einRecordRepo.findById(orderNum).orElse(null);
        if (null == order) {
            throw new NotFoundException();
        }
        return order;
    }

    @PostMapping("/init")
    public @ResponseBody EinOrder initOrder(HttpSession session, @RequestBody EinOrder orderInfo) {
        orderInfo.setId(null);
        orderInfo.setStatus(OrderRecord.STATUS_INCOMPLETE);
        EinOrder created = einRecordRepo.save(orderInfo);

        Long createdId = created.getId();
        Timer timer = new Timer();
        TimerTask delayedTask = new TimerTask() {
            @Override
            public void run() {
                EinOrder order = einRecordRepo.findById(createdId).orElse(null);
                // if incomplete after 1 hour - send email
                if (order != null && OrderRecord.STATUS_INCOMPLETE.equals(order.getStatus())) {
                    mailjetSender.sendEinIncompleteEmail(order);
                }
            }
        };
        timer.schedule(delayedTask, 30 * 60 * 1000); // start after 30 minutes delay

        return created;
    }

    @PostMapping("/authorize")
    public @ResponseBody
    AuthorizeResult authorizeTransaction(HttpSession session,
                                         HttpServletRequest httpServletRequest, @RequestBody EinAuthorizeInfo authInfo) throws URISyntaxException {

        Map payload = centinelFacade.validateJwt(authInfo.getResponseJwt());
        Map paymentMap = (Map) payload.get("Payment");
        Map extendedDataMap = (Map) paymentMap.get("ExtendedData");
        String eciFlag = (String) extendedDataMap.get("ECIFlag");

        EinOrder order = authInfo.getOrder();
        logger.debug("AUTHORIZE TRANSACTION:" + order.getId() + ", ECI:" + eciFlag);

        Optional<OrderLock> orderLockOptional = orderLockRepository.findByOrderId(order.getId());
        OrderLock orderLock = orderLockOptional.orElse(null);
        if (orderLock == null) {
            orderLock = new OrderLock();
            orderLock.setOrderId(order.getId());
            orderLock.setLockedAt(new Timestamp(System.currentTimeMillis()));
            orderLockRepository.save(orderLock);
        } else {
            // Throw conflict exception if order locked less than 10 minutes ago
            long lockedAt = orderLock.getLockedAt().getTime();
            long now = System.currentTimeMillis();
            if (now - lockedAt < 10 * 60 * 1000) {
                logger.error("ERROR: Order " + order.getId() + " is locked less than 10 minutes ago.");
                throw new ConflictException();
            }
        }

        // copy status from database
        EinOrder orderDB = einRecordRepo.findById(order.getId()).get();
        order.setCreatedDate(orderDB.getCreatedDate());
        order.setCorrelationId(orderDB.getCorrelationId());

        if (order.getCard_number() != null) {
            order.setCard_number(order.getCard_number().replaceAll("[^0-9]", ""));
        }

        order.setAuthorizeErrorCode(((Integer) payload.get("ErrorNumber")).toString());
        order.setAuthorizeErrorMessage((String) payload.get("ErrorDescription"));
        order.setTransactionId((String) paymentMap.get("ProcessorTransactionId"));
        order.setPayloadResponseStatus((String) extendedDataMap.get("PAResStatus"));
        order.setSignatureVerification((String) extendedDataMap.get("SignatureVerification"));
        order.setAuthEci(eciFlag);
        order.setEciFlag(eciFlag);
        order.setAuthCavv((String) extendedDataMap.get("CAVV"));
        order.setAuthXid((String) extendedDataMap.get("XID"));
        order.setEnrolled((String) extendedDataMap.get("Enrolled"));

        String ipAddress = httpServletRequest.getHeader("x-forwarded-for");
        if (ipAddress == null) {
            ipAddress = httpServletRequest.getRemoteAddr();
        }
        order.setCustomerIpAddress(ipAddress);

        AuthorizeResult authorizeResult = new AuthorizeResult();
        authorizeResult.setCorrelationId(order.getCorrelationId());

        if ("02".equals(eciFlag)) {
            order.setCardBrand("MASTERCARD");
            orderDB.setCardBrand("MASTERCARD");
        } else if ("05".equals(eciFlag)) {
            order.setCardBrand("VISA");
            orderDB.setCardBrand("VISA");
        }

        Gson gson = new Gson();
        String payload_response = gson.toJson(payload);
        String orbital_payment_response = "";
        String exception_message = "";

        if ("00".equals(eciFlag) || "07".equals(eciFlag)) {
            logger.error("ECI Flag is: " + eciFlag);
            String errMsg = "Authentication is unsuccessful or not attempted. The credit card is either a non-3D card or card issuing bank does not handle it as a 3D transaction.";
            exception_message = errMsg;

            authorizeResult.setSuccess(false);
            authorizeResult.setMessage(errMsg);

            order.setStatus(OrderRecord.STATUS_FAILED);
            order.setNotes(authorizeResult.getMessage());

            orderLockRepository.delete(orderLock);  // allow retry if failed
        } else {
            try {
                CreatePaymentRequest buildPaymentRequest = createPaymentService.buildEinPaymentRequest(order);
                orbital_payment_response = createPaymentService.createPayment4Logging(buildPaymentRequest);

                CreatePaymentResponse createPaymentResponse = gson.fromJson(orbital_payment_response, CreatePaymentResponse.class);

                OrderResponse orderResponse = createPaymentResponse.getOrder();
                StatusResponse statusResponse = orderResponse.getStatus();
                String approvalStatus = statusResponse.getApprovalStatus();
                String respCodeMessage = statusResponse.getRespCodeMessage();
                String respCode = statusResponse.getRespCode();

                order.setOrderId(createPaymentResponse.getOrder().getOrderID());
                order.setOrbitalTransactionId(createPaymentResponse.getOrder().getTxRefIdx());
                order.setOrbitalTransactionNumber(createPaymentResponse.getOrder().getTxRefNum());
                order.setAuthorizeTransactionId(createPaymentResponse.getOrder().getTxRefNum());
                order.setTransactionType(createPaymentResponse.getTransType());

                order.setInquiryRetryNumber(createPaymentResponse.getOrder().getInquiryRetryNumber());
                String retryAttempCount = createPaymentResponse.getOrder().getRetryAttempCount();
                if (retryAttempCount != null) {
                    order.setRetryAttempCount(Integer.valueOf(retryAttempCount));
                }
                order.setAuthorizeAuthCode(createPaymentResponse.getOrder().getStatus().getAuthorizationCode());
                order.setAuthorizeErrorMessage(respCodeMessage);
                order.setAuthorizeErrorCode(respCode);

                String cardNumber = order.getCard_number();
                String last4CardNumber = cardNumber.substring(cardNumber.length() - 4);
                order.setLast4DigitsCard(last4CardNumber);

                if("1".equals(approvalStatus)) {
                    AlertConfig autoFulfillment = alertConfigRepo.findByName(AlertConfigRepo.EIN_FULFILLMENT_AUTO);
                    boolean autoEnabled = autoFulfillment != null && autoFulfillment.isEnabled();

                    if (autoEnabled) {
                        order.setStatus(OrderRecord.STATUS_PROCESSING);
                        mailjetSender.sendEinOrderProcessingEmail(order);
                    } else {
                        order.setStatus(OrderRecord.STATUS_ON_HOLD);
                    }

                    authorizeResult.setSuccess(true);
                    authorizeResult.setMessage("Successfully process payment");
                } else {
                    logger.error("Error in authorize");

                    exception_message = "Error in authorize: approvalStatus is not 1";

                    authorizeResult.setSuccess(false);
                    authorizeResult.setMessage("There was an unexpected error on card transaction.");

                    order.setStatus(OrderRecord.STATUS_FAILED);
                    order.setNotes(authorizeResult.getMessage());

                    orderLockRepository.delete(orderLock);  // allow retry if failed
                }
            } catch (Exception e) {
                logger.error("Error in authorize", e);

                exception_message = "Error in authorize: " + e.getMessage();

                authorizeResult.setSuccess(false);
                authorizeResult.setMessage("Card Transaction Failed!");

                order.setStatus(OrderRecord.STATUS_FAILED);
                order.setNotes(authorizeResult.getMessage());

                orderLockRepository.delete(orderLock);  // allow retry if failed
            }
        }

        if ("".equals(order.getSuffix())) {
            order.setSuffix(null);
        }

        EinOrder savedOrder = einRecordRepo.save(order);
        logger.debug("SAVED EIN ORDER:" + savedOrder.getId() + " STATUS:" + savedOrder.getStatus());

        TransactionLogs trxLog = new TransactionLogs();
        trxLog.setOrder_id(order.getId());
        trxLog.setCardinal_response(payload_response);
        trxLog.setOrbital_payment_response(orbital_payment_response);
        trxLog.setException_message(exception_message);
        trxLog.setCreatedDate(new Date());

        transactionLogsRepo.save(trxLog);

        if (OrderRecord.STATUS_FAILED.equals(order.getStatus())) {
            mailjetSender.sendEinFailedEmail(order);
        }
        return authorizeResult;
    }

    @GetMapping("/confirmation/{correlationId}")
    public @ResponseBody EinOrder getOrderConfirmation(@PathVariable String correlationId) {
        EinOrder order = einRecordRepo.findByCorrelationId(correlationId);
        if (order == null) {
            logger.error("ERROR: Not Found Order: getOrderConfirmation(): 810");
            throw new NotFoundException();
        }
        order.setUserId(emailToUserIdMapService.getOrCreateRecordByEmail(order.getEmail()).getUserId());
        return order;
    }

    @GetMapping("/order/{id}")
    public @ResponseBody
    EinOrder getEinOrder(HttpSession session, @PathVariable Long id) throws Exception {
        EinOrder order = einRecordRepo.findById(id).orElse(null);

        String envelopeId = order.getDsEnvelopeId();
        if (OrderRecord.STATUS_AWAITING_SIGNATURE_SERVICE.equals(order.getStatus())
                && StringUtils.isNotBlank(envelopeId)) {
            boolean signed = checkSignedAndFetchFSS4For(order, true);

            if (signed) {
                order.setStatus(OrderRecord.STATUS_SIGNED);

                Long theId = order.getId();
                Timer timer = new Timer();
                TimerTask delayedTask = new TimerTask() {
                    @Override
                    public void run() {
                        EinOrder theOrder = einRecordRepo.findById(theId).orElse(null);
                        // if incomplete after 1 hour - send email
                        if (theOrder != null && OrderRecord.STATUS_SIGNED.equals(theOrder.getStatus())) {
                            mailjetSender.sendEinPaymentMissingEmail(theOrder);
                        }
                    }
                };
                timer.schedule(delayedTask, 30 * 60 * 1000); // start after 30 minutes delay

                einRecordRepo.save(order);
            }
        }

        return order;
    }

    private void fetchFSS4For(EinOrder order) {
        Long id = order.getId();
        String envelopeId = order.getDsEnvelopeId();
        try {
            // After signing it takes about 8 to 45 seconds to generated the signed document.
            // We also don't want to wait forever when the 3rd party service failed to update the status.
            // So to be safe we wait for at most 120 seconds.
            byte[] fSS4Content = signatureAPIService.getSignedDocument(envelopeId, 120);

            AmazonS3 s3client = initS3Client();
            if (!s3client.doesBucketExist(this.awsBucketName)) {
                s3client.createBucket(this.awsBucketName);
            }
            // We do left the path f8821-based for backward compatibility, it will contain SS4 form from now
            String thePath = "f8821/f8821-" + id + TaxConstants.PDF_EXTENSION;
            if (s3client.doesBucketExist(this.awsBucketName)) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("application/pdf");
                s3client.putObject(
                        this.awsBucketName,
                        thePath,
                        new ByteArrayInputStream(fSS4Content),
                        metadata);
            }

            order.setF8821S3Path(thePath);
            einRecordRepo.save(order);
        } catch (Exception ex) {
            logger.warn("Failed to fetch and store the fss4 pdf file from DocuSign to AWS S3 for order ", id, ex);
            // do nothing, we will have other tries
        }
    }

    private boolean checkSignedAndFetchFSS4For(EinOrder order, boolean async) throws Exception {
        String s3Path = order.getF8821S3Path();
        if (StringUtils.isNotBlank(s3Path)) { // Form SS4 signed and in the S3 bucket
            return true;
        }

        String envelopeId = order.getDsEnvelopeId();
        if (StringUtils.isBlank(envelopeId)) {
            return false;
        }

        boolean signed = signatureAPIService.isSigned(envelopeId);

        if (signed) { // it means that Form SS4 is signed, but not fetched (first if statement) - let's fetch it to S3 bucket
            if (async) {
                Timer timer = new Timer();
                TimerTask delayedTask = new TimerTask() {
                    @Override
                    public void run() {
                        fetchFSS4For(order);
                    }
                };
                timer.schedule(delayedTask, 1 * 1000); // start after 1 second delay
            } else {
                fetchFSS4For(order);
            }
        }

        return signed;
    }

    @GetMapping("/fss4Download/id/{id}")
    public @ResponseBody ResponseEntity<Resource> downloadSignedFSS4Pdf(HttpSession session, @PathVariable Long id) throws Exception {
        try {
            EinOrder order = einRecordRepo.findById(id).orElse(null);

            String envelopeId = order.getDsEnvelopeId();
            List<String> notReady = Arrays.asList(OrderRecord.STATUS_AWAITING_SIGNATURE_SERVICE);

            if (!notReady.contains(order.getStatus()) && StringUtils.isNotBlank(envelopeId)) { // looks to be signed already
                boolean signed = checkSignedAndFetchFSS4For(order, false);

                if (signed) {
                    AmazonS3 s3client = initS3Client();
                    String s3Path = order.getF8821S3Path();
                    S3Object fss4Object = s3client.getObject(this.awsBucketName, s3Path);
                    S3ObjectInputStream fss4Content = fss4Object.getObjectContent();

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType("application/octet-stream"))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=fss4-" + id + TaxConstants.PDF_EXTENSION)
                            .body(new InputStreamResource(fss4Content));
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to download the fss4 pdf file for order ", id, e);
            throw e;
        }
    }

    private AmazonS3 initS3Client() {
        AWSCredentials awsCred = new BasicAWSCredentials(
                this.awsAccessKey,
                this.awsSecretKey);
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                .withRegion(Regions.US_EAST_2)
                .build();
        return s3client;
    }

    @GetMapping("/get-waiting-order")
    public @ResponseBody
    EinOrder getWaitingEinOrder(HttpSession session, @RequestParam String bot_id) {
        AlertConfig autoFulfillment = alertConfigRepo.findByName(AlertConfigRepo.EIN_FULFILLMENT_AUTO);
        boolean autoEnabled = autoFulfillment != null && autoFulfillment.isEnabled();

        EinOrder order = autoEnabled
                ? einRecordRepo.findWaitingEinOrder(bot_id)
                : einRecordRepo.findWaitingEinOrderManual(bot_id);
        return order;
    }

    @PostMapping("/set-ein")
    @ResponseBody
    public String setEin(@RequestParam String order_id, @RequestParam String ein, @RequestParam String ein_letter_pdf) {
        Long id = Long.valueOf(order_id);
        EinOrder order = einRecordRepo.findById(id).orElse(null);

        if (order != null) {
            order.setEin(ein);
            byte[] letterContent = null;
            if (StringUtils.isNotBlank(ein_letter_pdf)) {
                String letterPDF = ein_letter_pdf.startsWith("http")
                        ? ein_letter_pdf
                        : "https://sa.www4.irs.gov" + ein_letter_pdf;
                order.setLetterPdf(letterPDF);
                letterContent = saveEINLetterPDF(id, letterPDF);
            }

            order.setStatus(OrderRecord.STATUS_COMPLETE);
            order.setErrorCode(null);
            order.setRestartsCount(0);
            order.setStartAfterMillis(null);
            order.setManuallyRun(false);
            sendEINCompletedEmail(order, letterContent);
            einRecordRepo.save(order);
        }

        return "Ok";
    }

    private void sendEINCompletedEmail(EinOrder einOrder, byte[] letterContent) {
        HashMap<String, String> variables = new HashMap<>();

        String theName = einOrder.getFirst_name() + " " + einOrder.getLast_name();
        variables.put("name", theName);
        variables.put("einNumber", einOrder.getEin());
        variables.put("orderNumber", einOrder.getId().toString());
        variables.put("dateFormatted", DateUtil.formatMMDDYYYY(einOrder.getCreatedDate()));
        variables.put("paymentPlan", "EIN Application");
        variables.put("phone", einOrder.getPhone_number());
        variables.put("mailAddress", einOrder.getEmail());
        variables.put("reasonForEIN", einOrder.getReason());
        variables.put("employeesEIN", einOrder.getIs_w2_employees() == 1 ? "true" : "false");

        String subject = "EIN Order " + einOrder.getId() + " Completed";

        try {
            if (letterContent != null) {
                byte[] encoded = Base64.getEncoder().encode(letterContent);
                pdfEmail.sendTemplateEmail(new String(encoded), einOrder.getEmail(), theName,
                        "ein-letter", variables, einCompleteTemplateId);
            } else {
                // TODO: good to encapsulate email sending logic from PdfEmail and MailjetSender within the one class
                mailjetSender.sendTemplateEmail(einOrder.getEmail(), subject, einCompleteTemplateId, variables);
            }
        } catch (Exception e) {
            logger.error("Failed to save EIN completed email", e);
        }
    }

    private byte[] saveEINLetterPDF(Long id, String letterPDF) {
        try {
            InputStream input = new URL(letterPDF).openStream();
            byte[] fileContent = IOUtils.toByteArray(input);

            PdfOrderVersions orderPdf = new PdfOrderVersions();

            orderPdf.setOrderId(id.toString());
            orderPdf.setPdfFile(fileContent);

            ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findByOrderid(id.toString());
            int version = optRecord != null ? optRecord.size() : 0;
            orderPdf.setVersionId(id + "-v" + version);

            pdfOrderVersionRepo.save(orderPdf);
            return fileContent;
        } catch (Exception e) {
            logger.error("Failed to save the file", e);
            return null;
        }
    }

    @PostMapping("/set-ein-bot-log")
    @ResponseBody
    @CrossOrigin(maxAge = 5)
    public String setEinLog(@RequestBody EinBotLog log) {
        Long orderId = log.getEin_order_id();
        Integer is_error = log.getIs_error();
        Integer error_code = log.getError_code();

        EinBotLog err = new EinBotLog();
        err.setEin_order_id(orderId);
        err.setUrl(log.getUrl());
        err.setIs_error(log.getIs_error());
        err.setError_code(error_code);
        err.setCreated_date(new Date());
        err.setScreenshot_base64(log.getScreenshot_base64());
        einBotLogRepo.save(err);

        // Set up configuration to force extension redeploy before handling next waiting order
        boolean accessDenied = (error_code != null) && 1001 == error_code.intValue();
        AlertConfig accessDeniedConfig = alertConfigRepo.findByName(AlertConfigRepo.EIN_EXTENSION_REDEPLOY);
        if (accessDenied != accessDeniedConfig.isEnabled()) {
            accessDeniedConfig.setEnabled(accessDenied);
            alertConfigRepo.save(accessDeniedConfig);
        }

        if (is_error == 1) {
            EinOrder order = einRecordRepo.findById(orderId).orElse(null);
            // We do check if EIN number was set already here, since we left the IRS page open after order was
            // processed by the EIN Bot; and at some point we might be redirected to some unknown page, which
            // will be handled as an error.
            // E.g. for order 548209 we were redirected to https://sa.www4.irs.gov/modiein/common/session-cookies-disabled.jsp
            if (order != null && StringUtils.isBlank(order.getEin())) {
                boolean systemTemporaryUnavailable = (error_code != null)
                        && Arrays.asList(109, 110, 112, 113).contains(error_code.intValue());
                boolean perDayLimitation = (error_code != null) && 114 == error_code.intValue();

                if (systemTemporaryUnavailable) {
                    int restartCount = order.getRestartsCount() == null ? 1 : order.getRestartsCount() + 1;
                    Long startAfterMillis = System.currentTimeMillis() + 1L * 3600L * 1000L;
                    String status = restartCount <= 3 ? OrderRecord.STATUS_PROCESSING : OrderRecord.STATUS_BOT_ERROR;

                    order.setStatus(status);
                    order.setRestartsCount(restartCount);
                    order.setStartAfterMillis(startAfterMillis);
                    order.setManuallyRun(restartCount <= 3);
                } else if (perDayLimitation) {
                    Long startAfterMillis = System.currentTimeMillis() + 15L * 3600L * 1000L;

                    order.setStatus(OrderRecord.STATUS_PROCESSING); // potentially might be processed infinite times
                    order.setRestartsCount(0);
                    order.setStartAfterMillis(startAfterMillis);
                    order.setManuallyRun(true);
                } else if (accessDenied) {
                    int accessDeniedCount = order.getAccessDeniedCount() == null ? 1 : order.getAccessDeniedCount() + 1;
                    String status = accessDeniedCount <= 5 ? OrderRecord.STATUS_PROCESSING : OrderRecord.STATUS_BOT_ERROR;

                    order.setStatus(status);
                    order.setAccessDeniedCount(accessDeniedCount);
                    order.setStartAfterMillis(null);
                    order.setManuallyRun(accessDeniedCount <= 5);
                } else {
                    order.setStatus(OrderRecord.STATUS_BOT_ERROR);
                    order.setRestartsCount(0);
                    order.setStartAfterMillis(null);
                    order.setManuallyRun(false);
                }

                order.setErrorCode(error_code);
                einRecordRepo.save(order);
            }
        }

        return "Ok";
    }

    @PostMapping("/set-ein-maintenance")
    @ResponseBody
    @CrossOrigin(maxAge = 5)
    public String setEinMaintenance() {
        // Set up service maintenance timestamp to initiate bot's 6 hours delay
        AlertConfig maintenanceConfig = alertConfigRepo.findByName(AlertConfigRepo.EIN_SERVICE_MAINTENANCE);
        maintenanceConfig.setEmail(String.valueOf(System.currentTimeMillis()));
        alertConfigRepo.save(maintenanceConfig);
        return "Ok";
    }

    @GetMapping("/bot/logs/{id}")
    @ResponseBody
    public PaginationResponse<EinBotLog> getLogs(HttpSession session, @PathVariable Long id,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "25") int size) {
        List<AuthRole> roles = Arrays.asList(AuthRole.ADMIN, AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        long count = einBotLogRepo.getCountByOrderId(id);
        List<EinBotLog> logs = einBotLogRepo.findByOrderId(id, page * size, size);
        PaginationResponse<EinBotLog> result = PaginationResponse.<EinBotLog>builder()
                .pageNumber(page).pageSize(size)
                .numResults(count)
                .rows(logs).resultsInPage(logs.size())
                .build();
        return result;
    }

    @GetMapping("/bot/logs/{id}/check")
    @ResponseBody
    public EinLogMeta checkLogs(HttpSession session, @PathVariable Long id) {
        // Technically we don't know if there are bot logs available for the specific order just based on the status
        List<AuthRole> roles = Arrays.asList(AuthRole.ADMIN, AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        long count = einBotLogRepo.getCountByOrderId(id);
        return EinLogMeta.builder().count(count).build();
    }

}
