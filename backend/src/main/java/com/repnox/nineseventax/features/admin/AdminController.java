package com.repnox.nineseventax.features.admin;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.docusign.esign.client.ApiException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVWriter;
import com.repnox.nineseventax.common.PaginationRequest;
import com.repnox.nineseventax.exceptions.BadRequestException;
import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.admin.repo.OrderRepo;
import com.repnox.nineseventax.features.auth.AuthRole;
import com.repnox.nineseventax.features.auth.AuthService;
import com.repnox.nineseventax.features.auth.UserRecord;
import com.repnox.nineseventax.features.auth.UserRecordRepo;
import com.repnox.nineseventax.features.couponcode.model.CouponCode;
import com.repnox.nineseventax.features.couponcode.model.CouponCodeRepo;
import com.repnox.nineseventax.features.docusign.DocuSignService;
import com.repnox.nineseventax.features.ecommerce.*;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.ein.EinRecordRepo;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.excel.OfferInCompromiseExport;
import com.repnox.nineseventax.features.excel.PaymentPlanExport;
import com.repnox.nineseventax.features.excel.TaxLienExport;
import com.repnox.nineseventax.features.mailroom.model.Batch;
import com.repnox.nineseventax.features.mailroom.model.BatchRepo;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUser;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUserRepo;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUserRequest;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUserSpecs;
import com.repnox.nineseventax.features.models.*;
import com.repnox.nineseventax.features.oic.OicRepository;
import com.repnox.nineseventax.features.oic.model.OicModel;
import com.repnox.nineseventax.features.order.repo.FulfillmentOrder;
import com.repnox.nineseventax.features.order.repo.FulfillmentRepo;
import com.repnox.nineseventax.features.partnercode.model.PCUsageReport;
import com.repnox.nineseventax.features.partnercode.model.PartnerCode;
import com.repnox.nineseventax.features.partnercode.model.PartnerCodeRepo;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanRepo;
import com.repnox.nineseventax.features.pdforderversions.PdfOrderVersions;
import com.repnox.nineseventax.features.pdforderversions.PdfOrderVersionsRepo;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyOrderRepo;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import com.repnox.nineseventax.features.processedorders.ProcessedOrders;
import com.repnox.nineseventax.features.processedorders.ProcessedOrdersRepo;
import com.repnox.nineseventax.features.processordersschedule.ProcessOrdersSchedule;
import com.repnox.nineseventax.features.processordersschedule.ProcessOrdersScheduleRepo;
import com.repnox.nineseventax.features.productprices.ProductPriceRepo;
import com.repnox.nineseventax.features.productprices.SalesListRepo;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabels;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabelsRepo;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalDetails;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalRepo;
import com.repnox.nineseventax.features.utils.AddressUtil;
import com.repnox.nineseventax.features.utils.AdminUtil;
import com.repnox.nineseventax.features.utils.TaxConstants;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.GetTransactionDetailsResponse;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api/admin")
public class AdminController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    @Value("${mailroom.upload.path}")
    private String mailroomFilePath;

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private PenaltyOrderRepo penaltyOrderRepo;

    @Autowired
    private PenaltyRecordRepo penaltyRecordRepo;

    @Autowired
    private EinRecordRepo einRecordRepo;

    @Autowired
    private FulfillmentRepo fulfillmentRepo;

    @Autowired
    private ProcessedOrdersRepo processedOrdersRepo;

    @Autowired
    private PdfOrderVersionsRepo pdfVersionRepo;

    @Autowired
    private UserRecordRepo userRecordRepo;

    @Autowired
    private TransactionRecordRepo transactionRecordRepo;

    @Autowired
    private AuthorizeNetFacade authorizeNetFacade;

    @Autowired
    private PaymentPlanRepo paymentPlanRepo;

    @Autowired
    private OicRepository oicRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private TaxLienRemovalRepo taxLienRemovalRepo;

    @Autowired
    private ProcessOrdersScheduleRepo processOrdersScheduleRepo;

    @Autowired
    private PaymentPlanExport paymentPlanExport;

    @Autowired
    private CsvWriter csvr;

    @Autowired
    private AdminUtil adminUtil;

    @Autowired
    private OfferInCompromiseExport offerInCompromiseExport;

    @Autowired
    private TaxLienExport taxLienExport;

    @Autowired
    private MailjetSender mailjetSender;

    @Autowired
    private MailRoomUserRepo mailRoomUserRepo;

    @Autowired
    private PartnerCodeRepo partnerCodeRepo;

    @Autowired
    private CouponCodeRepo couponCodeRepo;

    @Autowired
    private BatchRepo batchRepo;

    @Autowired
    private ProductPriceRepo productPriceRepo;

    @Autowired
    private SalesListRepo salesListRepo;

    @Value("${mailroom.upload.path}")
    private String uploadPath;

    private Timer timer;

    @Autowired
    private AdminSharedService adminSharedService;

    @Autowired
    private ShippingLabelsRepo shippingLabelsRepo;

    @Autowired
    private DocuSignService docuSignService;

    @Value("${aws.bucket.name}")
    private String awsBucketName;

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    private List<FulfillmentOrder> getProcessingOrders() {
        return fulfillmentRepo.findOrderInProcessingAndNotShipengine();
    }

    private List<FulfillmentOrder> getStaleOnHoldOrders() {
        return fulfillmentRepo.findStaleOnHoldOrders();
    }

    public static class OrderSearchPaginationRequest extends PaginationRequest<OrderSearchQuery> {
    }

    public OrderRecord[] convert(List<OrderRecord> list) {
        return list.toArray(new OrderRecord[list.size()]);
    }

    /**
     * @param session
     * @param versionIds
     */
    @PostMapping("/uploadingmailroom/{versionIds}")
    public void uploadBatchFilesInMailRoom(HttpSession session, @PathVariable String versionIds) {

        try {
            Boolean isFoundOrder = false;
            List<String> versionsList = Arrays.asList(versionIds.split(","));
            for (String versionId : versionsList) {
                ProcessedOrders processedOrder = processedOrdersRepo.findByVersionId(versionId);
                if (processedOrder != null) {
                    Batch batch = new Batch();
                    batch.setPdfFile(processedOrder.getPdfFile());
                    batch.setShippingLabels(processedOrder.getShippingLabels());
                    batch.setStatus("Ready");
                    batch.setStartOrderNumber(processedOrder.getStartOrderNumber());
                    batch.setEndOrderNumber(processedOrder.getEndOrderNumber());
                    batchRepo.save(batch);

                    processedOrder.setSentToMailroom(true);
                    processedOrdersRepo.save(processedOrder);
                    shippingLabelsRepo.updateMailRoomBatchId(batch.getId(), processedOrder.getId());
                    isFoundOrder = true;
                }
            }
            if (isFoundOrder) {
                List<MailRoomUser> roomUsers = (List<MailRoomUser>) mailRoomUserRepo.findAll();
                for (MailRoomUser mailRoomUser : roomUsers) {
                    mailjetSender.sendEmailOfMailRoom(mailRoomUser);
                }
            }

        } catch (Exception e) {
            LOG.error("ERROR: From Uploading BatchFiles in Mail Room:", e);
        }
    }

    private void updateNote(long orderNum, String type, String content, String user) {

        String note_detail = "";

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String strDate = formatter.format(date);
        note_detail = type + "=*=" + content + "=*=" + user + "=*=" + strDate;

        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        OrderRecord orderRecord = optRecord.get();
        if (optRecord.isPresent()) {
            OrderRecord existing = optRecord.get();
            String details = "";
            if (orderRecord.getNotes() == null) {
                details = note_detail;
            } else {
                details = orderRecord.getNotes() + "###" + note_detail;
            }
            if (content.equals("CANCEL AND REFUND") && !existing.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
                existing.setStatus(OrderRecord.STATUS_CANCELLED);
            }
            existing.setNotes(details);
            orderRecordRepo.save(existing);
        } else {
            LOG.error("ERROR: Errors updating Notes Order Number => " + orderNum);
            throw new NotFoundException();
        }

    }

    private void updatePenaltyNote(long orderNum, String type, String content, String user) {

        String note_detail = "";

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String strDate = formatter.format(date);
        note_detail = type + "=*=" + content + "=*=" + user + "=*=" + strDate;

        Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            PenaltyOrder existing = optRecord.get();
            String details = "";
            if (existing.getNotes() == null) {
                details = note_detail;
            } else {
                details = existing.getNotes() + "###" + note_detail;
            }
            if (content.equals("CANCEL AND REFUND") && !existing.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
                existing.setStatus(OrderRecord.STATUS_CANCELLED);
            }
            existing.setNotes(details);
            penaltyRecordRepo.save(existing);
        } else {
            LOG.error("ERROR: Errors updating Notes Order Number => " + orderNum);
            throw new NotFoundException();
        }

    }

    private void updateEinNote(long orderNum, String type, String content, String user) {

        String note_detail = "";

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String strDate = formatter.format(date);
        note_detail = type + "=*=" + content + "=*=" + user + "=*=" + strDate;

        Optional<EinOrder> optRecord = einRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            EinOrder existing = optRecord.get();
            String details = "";
            if (existing.getNotes() == null) {
                details = note_detail;
            } else {
                details = existing.getNotes() + "###" + note_detail;
            }
            if (content.equals("CANCEL AND REFUND") && !existing.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
                existing.setStatus(OrderRecord.STATUS_CANCELLED);
            }
            existing.setNotes(details);
            einRecordRepo.save(existing);
        } else {
            LOG.error("ERROR: Errors updating Notes Order Number => " + orderNum);
            throw new NotFoundException();
        }

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private HashMap<String, Object> _processOrders2() throws Exception {

        try {
            List<ProcessedOrders> processedOrders = processedOrdersRepo.findUnsentProcessedOrders(false);
            for (ProcessedOrders pOrder : processedOrders) {
                ArrayList<ShippingLabels> shippingLabels = shippingLabelsRepo.findByProcessBatchId(pOrder.getId());
                for (ShippingLabels label : shippingLabels) {
                    adminSharedService.setShipingLabelAsVoid(label);
                }
            }
            processedOrdersRepo.deleteProcessedOrders(false);
        } catch (Exception e) {
            LOG.error("ERROR: Occured while process the unsent Processred Orders in processOrders2() function", e);
        }

        // Get the list of stale orders
        List<FulfillmentOrder> staleOnHoldOrders = getStaleOnHoldOrders();
        if (staleOnHoldOrders != null && staleOnHoldOrders.size() > 0) {
            for (int i = 0; i < staleOnHoldOrders.size(); i++) {
                Long currentOrderNum = Long.valueOf(staleOnHoldOrders.get(i).getId());
                String authTransId = staleOnHoldOrders.get(i).getAuthorizeTransactionId();
                String orderType = staleOnHoldOrders.get(i).getOrderType();
                Boolean isSucceed = false;
                if (authTransId != null && !authTransId.isEmpty()) {
                    GetTransactionDetailsResponse transDetailsResponse = authorizeNetFacade
                            .getTransactionDetails(authTransId);

                    if (transDetailsResponse != null && transDetailsResponse.getTransaction() != null) {
                        CreateTransactionResponse response = authorizeNetFacade.refundTransaction(
                                authTransId,
                                transDetailsResponse.getTransaction().getPayment().getCreditCard()
                                        .getCardNumber(),
                                transDetailsResponse.getTransaction().getPayment().getCreditCard()
                                        .getExpirationDate(),
                                transDetailsResponse.getTransaction().getAuthAmount(),
                                currentOrderNum.toString());
                        if (response == null) {
                            LOG.error("ERROR: Unable to refund transaction AdminController.java: 279");
                            isSucceed = false;
                        } else {
                            String refundTransId = response.getTransactionResponse().getTransId();
                            isSucceed = true;

                            if (FulfillmentOrder.ORDER_TYPE_PAYMENT_PLAN.equals(orderType)) {
                                Optional<OrderRecord> optRecord = orderRecordRepo.findById(currentOrderNum);
                                if (optRecord.isPresent()) {
                                    OrderRecord existing = optRecord.get();
                                    existing.setRefundAuthTransId(refundTransId);
                                    existing.setStatus(OrderRecord.STATUS_CANCELLED);
                                    orderRecordRepo.save(existing);

                                    try {
                                        mailjetSender.sendCancelledEmail(existing);
                                    } catch (IOException e) {
                                        LOG.error("ERROR: Unable to send email", e);
                                    }
                                }
                            } else {
                                Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(currentOrderNum);
                                if (optRecord.isPresent()) {
                                    PenaltyOrder existing = optRecord.get();
                                    existing.setRefundAuthTransId(refundTransId);
                                    existing.setStatus(OrderRecord.STATUS_CANCELLED);
                                    penaltyRecordRepo.save(existing);

                                    mailjetSender.sendPenaltyOrderCancelledEmail(existing);
                                }
                            }
                        }
                    }
                }

                /*
                Optional<OrderRecord> optRecord = orderRecordRepo.findById(currentOrderNum);
                if (optRecord.isPresent()) {
                    OrderRecord existing = optRecord.get();
                    Boolean isSucceed = false;
                    // If the order has an authorize.net transaction ID, refund the order
                    if (existing != null) {
                        if (existing.getAuthorizeTransactionId() != null
                                && !existing.getAuthorizeTransactionId().isEmpty()
                                && (existing.getRefundAuthTransId() == null
                                || existing.getRefundAuthTransId().isEmpty())) {
                            GetTransactionDetailsResponse transDetailsResponse = authorizeNetFacade
                                    .getTransactionDetails(existing.getAuthorizeTransactionId());

                            if (transDetailsResponse != null && transDetailsResponse.getTransaction() != null) {
                                CreateTransactionResponse response = authorizeNetFacade.refundTransaction(
                                        existing.getAuthorizeTransactionId(),
                                        transDetailsResponse.getTransaction().getPayment().getCreditCard()
                                                .getCardNumber(),
                                        transDetailsResponse.getTransaction().getPayment().getCreditCard()
                                                .getExpirationDate(),
                                        transDetailsResponse.getTransaction().getAuthAmount(),
                                        existing.getOrderNum().toString());
                                if (response == null) {
                                    LOG.error("ERROR: Unable to refund transaction AdminController.java: 279");
                                    isSucceed = false;
                                } else {
                                    existing.setRefundAuthTransId(response.getTransactionResponse().getTransId());
                                    isSucceed = true;
                                }
                            }
                        }

                        if (isSucceed) {
                            // Cancel the order
                            existing.setStatus(OrderRecord.STATUS_CANCELLED);
                            orderRecordRepo.save(existing);

                            try {
                                mailjetSender.sendCancelledEmail(existing);
                            } catch (IOException e) {
                                LOG.error("ERROR: Unable to send email", e);
                            }
                        }
                    }
                }
                */
            }
        }

        List<FulfillmentOrder> allOrders = getProcessingOrders();
        List<FulfillmentOrder>[] orderChunks = this.adminUtil.chunk(allOrders, 20);
        try {
            if (allOrders != null && allOrders.size() > 0) {
                AddressValidateResponse[] responseAddressValidate;
                for (int i = 0; i < orderChunks.length; i++) {
                    responseAddressValidate = adminSharedService.postAddressValidateRequest(orderChunks[i]);
                    for (int j = 0; j < responseAddressValidate.length; j++) {
                        Long currentOrderId = Long.parseLong(responseAddressValidate[j].getOriginal_address().getName());
                        FulfillmentOrder order = allOrders.stream()
                                .filter(o -> Long.valueOf(o.getId()).equals(currentOrderId))
                                .findAny()
                                .orElse(null);

                        if (order != null) {

                            String shipAddr = order.getShippingAddress1();
                            String respStatus = responseAddressValidate[j].getStatus();
                            String orderType = order.getOrderType();

                            if (FulfillmentOrder.ORDER_TYPE_PAYMENT_PLAN.equals(orderType)) {
                                Optional<OrderRecord> optRecord = orderRecordRepo.findById(currentOrderId);
                                if (optRecord.isPresent()) {
                                    OrderRecord rec = optRecord.get();

                                    if (shipAddr != null && shipAddr.startsWith("PO BOX")) {
                                        rec.setAddressStatus("verified");
                                    } else {
                                        if ("unverified".equals(respStatus) || "error".equals(respStatus)) {
                                            rec.setAddressStatus(respStatus);
                                            LOG.error("Address Validation Response: 321 => ", responseAddressValidate[j]);
                                            mailjetSender.sendOnHoldOrderEmail(rec);
                                            allOrders.removeIf(e -> Long.valueOf(e.getId()).equals(currentOrderId));
                                            updateNote(currentOrderId, "SYSTEM", "ON HOLD DUE TO FULFILLMENT ERROR", "SYSTEM");
                                        }
                                    }

                                    orderRecordRepo.save(rec);
                                }
                            } else {
                                Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(currentOrderId);
                                if (optRecord.isPresent()) {
                                    PenaltyOrder rec = optRecord.get();

                                    if (shipAddr != null && shipAddr.startsWith("PO BOX")) {
                                        rec.setAddressStatus("verified");
                                    } else {
                                        if ("unverified".equals(respStatus) || "error".equals(respStatus)) {
                                            rec.setAddressStatus(respStatus);
                                            LOG.error("Address Validation Response: 321 => ", responseAddressValidate[j]);
                                            mailjetSender.sendPenaltyOrderOnholdEmail(rec);
                                            allOrders.removeIf(e -> Long.valueOf(e.getId()).equals(currentOrderId));
                                            updateNote(currentOrderId, "SYSTEM", "ON HOLD DUE TO FULFILLMENT ERROR", "SYSTEM");
                                        }
                                    }

                                    penaltyRecordRepo.save(rec);
                                }
                            }

                            /*
                            order.setAddressStatus(responseAddressValidate[j].getStatus());
                            if (responseAddressValidate[j].getStatus().equals("unverified")
                                    || responseAddressValidate[j].getStatus().equals("error")) {
                                order.setStatus(OrderRecord.STATUS_ON_HOLD);
                                LOG.error("Address Validation Response: 321 => ", responseAddressValidate[j]);
                                mailjetSender.sendOnHoldOrderEmail(order);
                                allOrders.removeIf(e -> e.getOrderNum().equals(currentOrderId));
                                updateNote(currentOrderId, "SYSTEM", "ON HOLD DUE TO FULFILLMENT ERROR", "SYSTEM");
                            }
                            orderRecordRepo.save(order);
                             */
                        }
                    }
                    Thread.sleep(70000);
                }
            }
        } catch (Exception e) {
            LOG.error("ERROR: in On HOLDING: 331", e);
        }

        orderChunks = this.adminUtil.chunk(allOrders, 195);
        Integer processedOrderCounter = 0;
        Integer allProcessedOrderCounter = 0;
        if (allOrders != null && allOrders.size() > 0) {
            HashMap<String, Object> finalResult = new HashMap<>();
            List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
            Integer forIndex = 0;
            for (List<FulfillmentOrder> orders : orderChunks) {
                // if (forIndex < 3) {

                String uniqueFilename = UUID.randomUUID().toString();
                String uniqueShipmentFileName = UUID.randomUUID().toString();
                PDFMergerUtility pdfMerger = new PDFMergerUtility();
                PDFMergerUtility pdfMergerShipment = new PDFMergerUtility();

                pdfMerger.setDestinationFileName(
                        this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION);
                pdfMergerShipment.setDestinationFileName(
                        this.mailroomFilePath + uniqueShipmentFileName + TaxConstants.PDF_EXTENSION);
                List<FulfillmentOrder> ordersToUpdate = new ArrayList<FulfillmentOrder>();
                Boolean IfPdfFound = false;
                for (FulfillmentOrder order : orders) {
                    List<PdfOrderVersions> pdfOrders = pdfVersionRepo.findByOrderid(order.getId());
                    if (pdfOrders.size() > 0) {
                        if (pdfOrders.get(pdfOrders.size() - 1).getPdfFile() != null) {
                            InputStream targetStream = new ByteArrayInputStream(
                                    pdfOrders.get(pdfOrders.size() - 1).getPdfFile());
                            pdfMerger.addSource(targetStream);
                            IfPdfFound = true;
                            ordersToUpdate.add(order);
                        }
                    }
                }

                ProcessedOrders processedOrder = new ProcessedOrders();
                if (IfPdfFound) {
                    try {
                        Long startOrderNumber = Long.valueOf(ordersToUpdate.get(0).getId());
                        Long endOrderNumber = Long.valueOf(ordersToUpdate.get(ordersToUpdate.size() - 1).getId());
                        processedOrder.setVersionId(UUID.randomUUID().toString());
                        processedOrder.setStartOrderNumber(startOrderNumber);
                        processedOrder.setEndOrderNumber(endOrderNumber);
                        processedOrder.setOrdersCounter(ordersToUpdate.size());

                        List<ShippingLabels> shippingLabelsEnvelops = new ArrayList<ShippingLabels>();
                        List<ShippingLabels> shippingLabelsPackages = new ArrayList<ShippingLabels>();
                        List<Long> erroredShippingEnvelopOrderNumbers = new ArrayList<Long>();
                        List<Long> erroredShippingPackageOrderNumbers = new ArrayList<Long>();
                        int count = 0;
                        for (FulfillmentOrder updateOrder : ordersToUpdate) {
                            count++;
                            if (count == 10) {
                                count = 0;
                                Thread.sleep(70000);
                            }

                            Long currentOrderid = Long.valueOf(updateOrder.getId());
                            ShippingLabels shippingLabelsPackage = new ShippingLabels();
                            shippingLabelsPackage.setOrderId(currentOrderid);
                            try {
                                LabelPackageEnvelopeResponse labelPackageResponse = adminSharedService
                                        .postLabelPackageRequest(updateOrder);
                                String labelPackageDownload = labelPackageResponse.getLabel_download().getHref();
                                InputStream labelPackageStream = this.adminUtil
                                        .Base64ToStream(labelPackageDownload);
                                pdfMergerShipment.addSource(labelPackageStream);
                                adminSharedService.mapLabelResponseToShippinLabel(currentOrderid,
                                        labelPackageResponse, shippingLabelsPackage);
                                shippingLabelsPackages.add(shippingLabelsPackage);
                            } catch (HttpStatusCodeException exception) {
                                if (exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                    LOG.error("ERROR: ShipEngine Error Too Many Request : 413 "
                                            + currentOrderid, exception);
                                    Thread.sleep(60000);
                                    try {
                                        LabelPackageEnvelopeResponse labelPackageResponse = adminSharedService
                                                .postLabelPackageRequest(updateOrder);
                                        String labelPackageDownload = labelPackageResponse.getLabel_download()
                                                .getHref();
                                        InputStream labelPackageStream = this.adminUtil
                                                .Base64ToStream(labelPackageDownload);
                                        pdfMergerShipment.addSource(labelPackageStream);
                                        adminSharedService.mapLabelResponseToShippinLabel(currentOrderid,
                                                labelPackageResponse, shippingLabelsPackage);
                                        shippingLabelsPackages.add(shippingLabelsPackage);
                                    } catch (Exception e) {
                                        LOG.error("ERROR: ShipEngine Error Too Many Request : 423 "
                                                + currentOrderid, exception);
                                    }
                                } else {
                                    LOG.error("ERROR: ShipEngine Error when the pacakge is created: 426"
                                            + currentOrderid, exception);
                                    System.out
                                            .println(adminSharedService.createLabelPackageRequestData(updateOrder));
                                    erroredShippingPackageOrderNumbers.add(currentOrderid);
                                    shippingLabelsPackage.setErrorMessage(exception.getResponseBodyAsString());
                                    shippingLabelsRepo.save(shippingLabelsPackage);
                                }
                            } catch (Exception e) {
                                LOG.error("ERROR: While create the Batch: 433 : OrderNum"
                                        + currentOrderid + " ", e);
                            }

                            ShippingLabels shippingLabelsEnvelop = new ShippingLabels();
                            shippingLabelsEnvelop.setOrderId(currentOrderid);
                            try {
                                LabelPackageEnvelopeResponse labelEnvelopeResponse = adminSharedService
                                        .postLabelEnvelopeRequest(updateOrder);
                                String labelPackageDownload = labelEnvelopeResponse.getLabel_download().getHref();
                                InputStream labelEnvelopStream = this.adminUtil
                                        .Base64ToStream(labelPackageDownload);
                                pdfMergerShipment.addSource(labelEnvelopStream);
                                adminSharedService.mapLabelResponseToShippinLabel(currentOrderid,
                                        labelEnvelopeResponse, shippingLabelsEnvelop);
                                shippingLabelsEnvelops.add(shippingLabelsEnvelop);
                            } catch (HttpStatusCodeException exception) {
                                if (exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                    LOG.error("ERROR: ShipEngine Error Too Many Request: 447 : "
                                            + currentOrderid, exception);
                                    Thread.sleep(180000);
                                    try {
                                        LabelPackageEnvelopeResponse labelEnvelopeResponse = adminSharedService
                                                .postLabelEnvelopeRequest(updateOrder);
                                        String labelPackageDownload = labelEnvelopeResponse.getLabel_download()
                                                .getHref();
                                        InputStream labelEnvelopStream = this.adminUtil
                                                .Base64ToStream(labelPackageDownload);
                                        pdfMergerShipment.addSource(labelEnvelopStream);
                                        adminSharedService.mapLabelResponseToShippinLabel(currentOrderid,
                                                labelEnvelopeResponse, shippingLabelsEnvelop);
                                        shippingLabelsEnvelops.add(shippingLabelsEnvelop);
                                    } catch (Exception e) {
                                        LOG.error("ERROR: ShipEngine Error Too Many Request : 440 "
                                                + currentOrderid, exception);
                                    }
                                } else {
                                    LOG.error("ERROR: ShipEngine Error when the Envelop is created: 405"
                                            + currentOrderid, exception);
                                    System.out.println(
                                            adminSharedService.createLabelEnvelopeRequestData(updateOrder));
                                    erroredShippingEnvelopOrderNumbers.add(currentOrderid);
                                    shippingLabelsEnvelop.setErrorMessage(exception.getResponseBodyAsString());
                                    shippingLabelsRepo.save(shippingLabelsEnvelop);
                                }
                            } catch (Exception e) {
                                LOG.error("ERROR: While create the Batch: 467 : OrderNum"
                                        + currentOrderid + " ", e);
                            }
                        }

                        Boolean isNotEqual = ordersToUpdate.size() != shippingLabelsPackages.size() ? true
                                : ordersToUpdate.size() != shippingLabelsEnvelops.size() ? true : false;

                        HashMap<String, String> temp = new HashMap<String, String>();
                        temp.put("startOrderNumber", startOrderNumber.toString());
                        temp.put("endOrderNumber", endOrderNumber.toString());
                        temp.put("versionId", processedOrder.getVersionId());
                        result.add(temp);
                        pdfMerger.mergeDocuments(null);
                        String pdfFileName = uniqueFilename + TaxConstants.PDF_EXTENSION;
                        String uploadedPdfPath = this.uploadFileToAwsS3Bucket(
                                new File(this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION),
                                pdfFileName);
                        processedOrder.setPdfFile(uploadedPdfPath);

                        // processedOrder.setPdfFile(this.adminUtil.fileToByte(this.mailroomFilePath +
                        // uniqueFilename + TaxConstants.PDF_EXTENSION));
                        pdfMergerShipment.mergeDocuments(null);
                        String shipmentFileName = uniqueShipmentFileName + TaxConstants.PDF_EXTENSION;
                        String uploadedShippingLabelPath = this.uploadFileToAwsS3Bucket(new File(
                                        this.mailroomFilePath + uniqueShipmentFileName + TaxConstants.PDF_EXTENSION),
                                shipmentFileName);
                        processedOrder.setShippingLabels(uploadedShippingLabelPath);

                        if (isNotEqual.equals(false)) {
                            processedOrdersRepo.save(processedOrder);
                        } else {
                            processedOrderCounter = shippingLabelsEnvelops.size() < shippingLabelsPackages.size()
                                    ? shippingLabelsEnvelops.size()
                                    : shippingLabelsPackages.size();
                            processedOrder.setOrdersCounter(processedOrderCounter);
                            processedOrdersRepo.save(processedOrder);
                        }

                        for (ShippingLabels label : shippingLabelsEnvelops) {
                            if (isNotEqual.equals(true)) {
                                if (erroredShippingEnvelopOrderNumbers.indexOf(label.getOrderId()) > -1) {
                                    adminSharedService.setShipingLabelAsVoid(label);
                                } else {
                                    label.setProcessBatchId(processedOrder.getId());
                                }
                            } else {
                                label.setProcessBatchId(processedOrder.getId());
                            }
                        }

                        for (ShippingLabels label : shippingLabelsPackages) {
                            if (isNotEqual.equals(true)) {
                                if (erroredShippingPackageOrderNumbers.indexOf(label.getOrderId()) > -1) {
                                    adminSharedService.setShipingLabelAsVoid(label);
                                } else {
                                    label.setProcessBatchId(processedOrder.getId());
                                }
                            } else {
                                label.setProcessBatchId(processedOrder.getId());
                            }
                        }
                        shippingLabelsRepo.saveAll(shippingLabelsEnvelops);
                        shippingLabelsRepo.saveAll(shippingLabelsPackages);
                        this.adminUtil.deleteFiles(uniqueFilename);
                        this.adminUtil.deleteFiles(uniqueShipmentFileName);
                        allProcessedOrderCounter += (isNotEqual ? processedOrderCounter : ordersToUpdate.size());
                    } catch (Exception e) {
                        LOG.error("ERROR: While create the Batch: 462", e);
                    }
                }
                // }

                Thread.sleep(10000);
                forIndex++;
            }
            String warningMessage = "";
            if (!allProcessedOrderCounter.equals(allOrders.size())) {
                warningMessage = "WARNING: " + Integer.toString(allProcessedOrderCounter) + " orders in batch and "
                        + Integer.toString(allOrders.size()) + " orders processing status.";
            }
            finalResult.put("processedOrders", result);
            finalResult.put("warningMessage", warningMessage);
            return finalResult;
        } else {
            LOG.error("ERRROR: Not found processed Orders: 476");
            throw new NotFoundException();
        }
    }

    @GetMapping("/processordersschedule/current")
    public ProcessOrdersSchedule getProcessOrdersSchedule() throws Exception {
        return processOrdersScheduleRepo.findById(1);
    }

    @PutMapping("/processordersschedule/current")
    public @ResponseBody ProcessOrdersSchedule updateProcessOrdersSchedule(
            @RequestBody ProcessOrdersSchedule scheduleInfo) throws Exception {
        Boolean isAutomatic = scheduleInfo.getIsAutomatic();
        Date automatedTime = scheduleInfo.getAutomatedTime();
        List<String> excludedHolidays = Arrays.asList(scheduleInfo.getExcludedHolidays().split(","));
        processOrdersScheduleRepo.save(scheduleInfo);

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

        this.timer = new Timer();
        TimerTask scheduledTask = new TimerTask() {
            @Override
            public void run() {
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

                try {
                    mailroomProcessOrders();
                } catch (Exception e) {
                    LOG.error("ERROR: occurred while running automated fulfillment scheduler: " + e.toString());
                }
            }
        };
        this.timer.schedule(scheduledTask, automatedTime, 24 * 60 * 60 * 1000); // every day interval
        return scheduleInfo;
    }

    @GetMapping("/processorders")
    public HashMap<String, Object> processOrders(HttpSession session, HttpServletResponse response) throws Exception {
        return _processOrders2();
    }

    @GetMapping("/processingorders/orders.xlsx")
    public void exportProcessingExcel(HttpSession session, HttpServletResponse response, OrderSearchQuery query)
            throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        try {
            List<OrderRecord> orders = getProcessingOrders(query);
            paymentPlanExport.exportProcessingOrdersExcel(response.getOutputStream(), orders);
        } catch (Exception ee) {
            LOG.error("ERROR: Exception Feting Order", ee);
        }
    }

    @GetMapping("/processorders/orders.csv")
    public void processOrders(HttpSession session, HttpServletResponse response, OrderSearchQuery query)
            throws IOException, ParseException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        response.setContentType("text/csv");
        paymentPlanExport.exportProcessingOrdersCsv(response.getOutputStream(), getProcessingOrders(query));
    }

    @GetMapping("/processorders/ordersByDate.csv")
    public void processOrdersByDate(HttpSession session, HttpServletResponse response, OrderSearchQuery query)
            throws IOException, ParseException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        response.setContentType("text/csv");
        paymentPlanExport.exportProcessingOrdersCsv(response.getOutputStream(), getProcessingOrdersByDate(query));
    }


    @GetMapping("/orders/oic_export.xlsx")
    public void exportOicExcel(HttpSession session, HttpServletResponse response, OrderSearchPaginationRequest request)
            throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        offerInCompromiseExport.exortOicExcel(response.getOutputStream());
    }

    @GetMapping("/orders/oic_export.csv")
    public void exportOicCsv(HttpSession session, HttpServletResponse response) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        response.setContentType("text/csv");
        offerInCompromiseExport.exortOicCsv(response.getOutputStream());
    }

    private List<OrderRecord> getProcessingOrders(OrderSearchQuery query) {
        return new ArrayList<>(orderRecordRepo.findAll(OrderSpecs.orderSearch(query)));
    }

    private List<OrderRecord> getProcessingOrdersByDate(OrderSearchQuery query) throws ParseException {
        Date date1 = new Date();
        Date date2 = new Date();
        try{
        if (query.getCreatedAt() != "") {

            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();

            Date start = null;
            Date end = null;
            String created = query.getCreatedAt();

            CreatedAtFormat fmt = new Gson().fromJson(created, CreatedAtFormat.class);

            if (fmt != null) {
                String st = fmt.getStartDate() ;
                String en = fmt.getEndDate() ;

                if (!st.isEmpty() && !en.isEmpty()) {
                    SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");

                    SimpleDateFormat entityFormatter = new SimpleDateFormat("M/d/yyyy hh:mm:ss a");
                    entityFormatter.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

                    Date startDate = inputFormatter.parse(st);
                    Date endDate = inputFormatter.parse(en);
                    date1 = entityFormatter.parse(entityFormatter.format(startDate));
                    date2 = entityFormatter.parse(entityFormatter.format(endDate));

                }
            }
        }
        List<OrderRecord> dataa = orderRecordRepo.findByOrderDateBetween(date1, date2);
        return dataa;
    }catch (ParseException e) {
            // Tangkap dan log kesalahan ParseException
            e.printStackTrace();
            // Bisa juga menambahkan logic untuk menangani error (misalnya, mengembalikan data kosong atau mengirimkan pesan error)
            return new ArrayList<>();
        }
    }

    @PostMapping("/orders/tracking_upload")
    public @ResponseBody List<UploadResult> uploadTrackingNumbers(HttpSession session,
                                                                  @RequestParam("file") MultipartFile file) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new InputStreamReader(file.getInputStream()))) {
            List<UploadResult> results = new ArrayList<>();
            List<String[]> records = reader.readAll();
            for (String[] next : records) {
                UploadResult result = new UploadResult();
                try {
                    Long orderNum;
                    try {
                        // Printed message is order id
                        orderNum = Long.parseLong(next[18].replaceAll("\\D+", ""));
                        System.out.println(orderNum);
                    } catch (Exception e) {
                        LOG.error("ERROR: Printed Messainge is Order ID:", e);
                        continue;
                    }

                    results.add(result);
                    result.setOrderNum(orderNum);
                    Optional<OrderRecord> optionalRecord = orderRecordRepo.findById(orderNum);
                    Optional<PenaltyOrder> optionalPenaltyRecord = penaltyRecordRepo.findById(orderNum);

                    if (optionalRecord.isPresent()) {
                        OrderRecord record = optionalRecord.get();
                        String oldStatus = record.getStatus();

                        if (OrderRecord.STATUS_PROCESSING.equals(oldStatus)) {
                            // Tracking number
                            record.setTrackingNumber(next[7].replaceAll("\\D+", ""));
                            record.setTrackingNumberEntry(new Date());
                            record.setStatus(OrderRecord.STATUS_COMPLETE);
                            orderRecordRepo.save(record);
                            mailjetSender.sendCompleteEmail(record);
                            result.setMessage("Success");
                        } else if (OrderRecord.STATUS_CANCELLED.equals(oldStatus)) {
                            result.setMessage("Error - Order was cancelled");
                        } else {
                            result.setMessage(
                                    "Warning - Order was not in Pending status, it was: " + record.getStatus());
                        }
                    } else if (optionalPenaltyRecord.isPresent()) {

                        PenaltyOrder recordPenalty = optionalPenaltyRecord.get();
                        String oldStatus = recordPenalty.getStatus();

                        if (OrderRecord.STATUS_PROCESSING.equals(oldStatus)) {
                            // Tracking number
                            recordPenalty.setTrackingNumber(next[7].replaceAll("\\D+", ""));
                            recordPenalty.setTrackingNumberEntry(new Date());
                            recordPenalty.setStatus(OrderRecord.STATUS_COMPLETE);
                            penaltyRecordRepo.save(recordPenalty);
                            mailjetSender.sendPenaltyOrderCompleteEmail(recordPenalty);
                            result.setMessage("Success");
                        } else if (OrderRecord.STATUS_CANCELLED.equals(oldStatus)) {
                            result.setMessage("Error - Penalty Order was cancelled");
                        } else {
                            result.setMessage(
                                    "Warning - Penalty Order was not in Pending status, it was: " + recordPenalty.getStatus());
                        }
                    } else {
                        result.setMessage("Error - Order number did not exist in database");
                    }
                } catch (Exception e) {
                    LOG.error("ERRPR: Row parse error", e);
                    result.setMessage(
                            "Error - unable to process row. Please contact the administrator and check the logs.");
                }
            }
            return results;
        } catch (IOException e) {
            LOG.error("ERROR: File Reading Error", e);
            throw new BadRequestException();
        }
    }

    @GetMapping("/orders/download")
    public void downloadOrders(HttpSession session, HttpServletResponse response,
                               @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                               @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        List<OrderRecord> orders = orderRecordRepo.findByOrderDateBetween(startDate, endDate);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=orders.csv");

        try (PrintWriter writer = response.getWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] header = {"Order Date", "Product", "Sales Amount", "Order Status", "Orbital Failure/Success Code",
                    "ECI Code", "Change of Address Selected", "Express Delivery", "Payroll Selected", "Chase or CDRN Chargeback"};
            csvWriter.writeNext(header);

            for (OrderRecord order : orders) {
                String[] data = {
                        order.getCreatedDate().toString(),
                        order.getProduct(),
                        String.valueOf(order.getAmount()),
                        order.getStatus(),
                        order.getOrbitalTransactionId(),
                        order.getEciFlag(),
                        "No", "No", "No", "No"
//                        order.isChangeOfAddressSelected() ? "Yes" : "No",
//                        order.isExpressDelivery() ? "Yes" : "No",
//                        order.getPayrollDeduction() ? "Yes" : "No",
//                        order.getChargebackStatus()
                };
                csvWriter.writeNext(data);
            }
        } catch (IOException e) {
            LOG.error("ERROR: File Writing Error", e);
            throw new RuntimeException("Error generating CSV file", e);
        }
    }

    @GetMapping("/orders")
    public @ResponseBody String getOrderRecords(HttpSession session,
                                                OrderSearchPaginationRequest request) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        /*
        Page<OrderRecord> page = orderRecordRepo.findAll(OrderSpecs.orderSearch(request.getQuery()),
                request.toPageable());
        return PaginationResponse.fromPage(page);
        */

        int total = orderRepo.getOrdersCount(request);
        List<Map<String, Object>> orders = orderRepo.getOrdersList(request);
        Gson gson = new Gson();

        Map<String, Object> result = new HashMap<>();
        result.put("numResults", total);
        result.put("pageNumber", request.getPageNumber());
        result.put("pageSize", request.getPageSize());
        result.put("resultsInPage", orders.size());
        result.put("rows", orders);

        String response = gson.toJson(result);
        return response;
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    @PostMapping("/orders/delete")
    public void deleteOrderRecords(HttpSession session, @RequestBody DeleteListRequest request) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        orderRepo.markOrdersForDeletion(request.getIds());
    }

    @PutMapping("/order/id/{orderNum}/trackingNumber")
    public @ResponseBody UpdateTrackingNumberResponse updateTrackingNumber(HttpSession session,
                                                                           @PathVariable Long orderNum, @RequestBody UpdateTrackingNumberRequest request) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        UpdateTrackingNumberResponse response = new UpdateTrackingNumberResponse();
        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        Optional<PenaltyOrder> optPenaltyRecord = penaltyRecordRepo.findById(orderNum);

        if (optRecord.isPresent()) {
            OrderRecord orderRecord = optRecord.get();
            orderRecord.setStatus(OrderRecord.STATUS_COMPLETE);
            orderRecord.setTrackingNumber(request.getTrackingNumber());
            orderRecord.setTrackingNumberEntry(new Date());
            orderRecordRepo.save(orderRecord);
            response.setSaved(true);
            if (request.isSendNotification()) {
                mailjetSender.sendCompleteEmail(orderRecord);
                response.setNotificationSent(true);
            }
            return response;
        } else if (optPenaltyRecord.isPresent()) {
            PenaltyOrder penaltyOrder = optPenaltyRecord.get();
            penaltyOrder.setStatus(OrderRecord.STATUS_COMPLETE);
            penaltyOrder.setTrackingNumber(request.getTrackingNumber());
            penaltyOrder.setTrackingNumberEntry(new Date());
            penaltyRecordRepo.save(penaltyOrder);
            response.setSaved(true);
            if (request.isSendNotification()) {
                mailjetSender.sendPenaltyOrderCompleteEmail(penaltyOrder);
                response.setNotificationSent(true);
            }
            return response;
        } else {
            LOG.error("ERROR: Not Found Order: updateTrackingNumber(): 702");
            throw new NotFoundException();
        }
    }

    @GetMapping("/order/id/{orderNum}")
    public @ResponseBody OrderRecord getOrderRecord(HttpSession session, @PathVariable Long orderNum) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            return optRecord.get();
        } else {
            LOG.error("ERROR: Not Found Order: getOrderRecord(): 717");
            throw new NotFoundException();
        }
    }

    @PutMapping("/order/id/{orderNum}")
    public void putOrderRecord(HttpSession session, @PathVariable Long orderNum, @RequestBody OrderRecord orderRecord) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            OrderRecord existing = optRecord.get();
            existing.setNotes(orderRecord.getNotes());
            existing.setNotesEntry(new Date());
            orderRecordRepo.save(existing);
        } else {
            LOG.error("ERROR: Not Found Order: putOrderRecord(): 717");
            throw new NotFoundException();
        }
    }

    @PutMapping("/registerUser")
    public void registerUser(HttpSession session, @RequestBody UserRecord user) {
        authService.requireRole(session, AuthRole.ADMIN);
        try {
            user.setUserType("StandardUser");
            userRecordRepo.save(user);
            mailjetSender.sendNewUserEmail(user);
        } catch (Exception ex) {
            LOG.error("ERROR: Register User: registerUser()", ex);
            System.out.println("User is" + ex.getMessage());
        }
    }

    @PutMapping("/updateUser")
    public void updateUser(HttpSession session, @RequestBody UserRecord user) {
        authService.requireRole(session, AuthRole.ADMIN);
        try {
            Optional<UserRecord> userToUpdate = userRecordRepo.findById(user.getUsername());
            if (userToUpdate.isPresent()) {
                UserRecord updatedUser = userToUpdate.get();
                updatedUser.setUserData(user);
                userRecordRepo.save(updatedUser);
            }
        } catch (Exception ex) {
            LOG.error("ERROR: Update User: updateUser()", ex);
            System.out.println("User is" + ex.getMessage());
        }
    }

    @GetMapping("/usersList")
    public @ResponseBody Iterable<UserRecord> usersList(HttpSession session) {
        authService.requireRole(session, AuthRole.ADMIN);
        try {
            return userRecordRepo.findAll();
        } catch (Exception ex) {
            LOG.error("ERROR: Get UserList: usersList()", ex);
            System.out.println("User is" + ex.getMessage());
            throw new NotFoundException();
        }
    }

    @PostMapping("/users/delete")
    public @ResponseBody Iterable<UserRecord> deleteUser(HttpSession session, @RequestBody UserRecord userRecord) {
        authService.requireRole(session, AuthRole.ADMIN);
        try {
            userRecordRepo.delete(userRecord);
            return userRecordRepo.findAll();
        } catch (Exception ex) {
            LOG.error("ERROR: Delete User: deleteUser()", ex);
            System.out.println("User is" + ex.getMessage());
            throw new NotFoundException();
        }
    }

    @PutMapping("/order/id/{orderNum}/cancel")
    public void cancelOrderRecord(HttpSession session, @PathVariable Long orderNum,
                                  @RequestBody OnHoldRequest request) {
        LOG.info("Cancel order called");

        try {
            List<AuthRole> roles = new ArrayList<>();
            roles.add(AuthRole.ADMIN);
            roles.add(AuthRole.STANDARDUSER);
            authService.requireRoles(session, roles);
            Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
            if (optRecord.isPresent()) {
                try {
                    OrderRecord existing = optRecord.get();
                    existing.setStatus(OrderRecord.STATUS_CANCELLED);
                    orderRecordRepo.save(existing);
                } catch (Exception e) {
                    LOG.error("ERROR: Saving Cancelled Order " + orderNum + ": Line 817");
                }
                updateNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
            } else {
                LOG.error("ERROR: Not Found Order:" + orderNum + " cancelOrderRecord(): 810");
                throw new NotFoundException();
            }
        } catch (Exception e) {
            LOG.error("Error occurred while canceling local record", e);
        }

    }

    @PutMapping("/order/id/{orderNum}/onhold")
    public void holdByOrderNum(HttpSession session, @PathVariable Long orderNum, @RequestBody OnHoldRequest request)
            throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            OrderRecord existing = optRecord.get();

            if (OrderRecord.STATUS_ON_HOLD.equals(existing.getStatus())) {
                existing.setStatus(OrderRecord.STATUS_PROCESSING);

                if (request.isSendNotification()) {
                    mailjetSender.sendProcessingEmail(existing);
                }
            } else {
                existing.setStatus(OrderRecord.STATUS_ON_HOLD);

                if (request.isSendNotification()) {
                    mailjetSender.sendOnHoldOrderEmail(existing);
                }
            }

            orderRecordRepo.save(existing);

            updateNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found Order: holdByOrderNum(): 810");
            throw new NotFoundException();
        }
    }

    @PutMapping("/order/id/{orderNum}/chargeback")
    public void chargeBackOrderRecord(HttpSession session, @PathVariable Long orderNum,
                                      @RequestBody OnHoldRequest request) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            OrderRecord existing = optRecord.get();
            existing.setStatus(OrderRecord.STATUS_CHARGE_BACK);
            existing.setCbtype(request.getCbtype());
            orderRecordRepo.save(existing);
            updateNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found Order: chargeBackOrderRecord(): 810");
            throw new NotFoundException();
        }
    }

    @PutMapping("/order/id/{orderNum}/updatenotes")
    public void updateOrderNotes(HttpSession session, @PathVariable Long orderNum,
                                 @RequestBody OnHoldRequest request) {
        updateNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
    }

    @PutMapping("/penalty-order/id/{orderNum}/updatenotes")
    public void updatePenaltyOrderNotes(HttpSession session, @PathVariable Long orderNum,
                                        @RequestBody OnHoldRequest request) {
        updatePenaltyNote(orderNum, request.getType(), request.getContent(), request.getUser());
    }

    @PutMapping("/ein/id/{orderNum}/updatenotes")
    public void updateEinOrderNotes(HttpSession session, @PathVariable Long orderNum,
                                    @RequestBody OnHoldRequest request) {
        updateEinNote(orderNum, request.getType(), request.getContent(), request.getUser());
    }

    @PutMapping("/order/id/{orderNum}/update")
    public OrderRecord saveOrder(HttpSession session, @PathVariable Long orderNum,
                                 @RequestBody OnSaveRequest request) {
        OrderRecord orderRecord = request.getOrderRecord();
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            OrderRecord existing = optRecord.get();

            existing.setFirstName(orderRecord.getFirstName());
            existing.setLastName(orderRecord.getLastName());
            existing.setPhone(orderRecord.getPhone());
            existing.setEmail(orderRecord.getEmail());
            existing.setSsn(orderRecord.getSsn());

            existing.setBillingAddress1(orderRecord.getBillingAddress1());
            existing.setBillingAddress2(orderRecord.getBillingAddress2());
            existing.setBillingCity(orderRecord.getBillingCity());
            existing.setBillingState(orderRecord.getBillingState());
            existing.setBillingZip(orderRecord.getBillingZip());

            existing.setShippingAddress1(orderRecord.getShippingAddress1());
            existing.setShippingAddress2(orderRecord.getShippingAddress2());
            existing.setShippingCity(orderRecord.getShippingCity());
            existing.setShippingState(orderRecord.getShippingState());
            existing.setShippingZip(orderRecord.getShippingZip());

            existing.setHasOldAddress(orderRecord.getHasOldAddress());
            existing.setOldAddress1(orderRecord.getOldAddress1());
            existing.setOldAddress2(orderRecord.getOldAddress2());
            existing.setOldCity(orderRecord.getOldCity());
            existing.setOldState(orderRecord.getOldState());
            existing.setOldZip(orderRecord.getOldZip());

            existing.setBusinessAddress1(orderRecord.getBusinessAddress1());
            existing.setBusinessAddress2(orderRecord.getBusinessAddress2());
            existing.setBusinessCity(orderRecord.getBusinessCity());
            existing.setBusinessState(orderRecord.getBusinessState());
            existing.setBusinessZip(orderRecord.getBusinessZip());

            existing.setSecondaryPhone(orderRecord.getSecondaryPhone());
            existing.setEmail(orderRecord.getEmail());
            existing.setIsOwedFromBusiness(orderRecord.getIsOwedFromBusiness());
            existing.setEin(orderRecord.getEin());
            existing.setBusinessName(orderRecord.getBusinessName());
            existing.setDba(orderRecord.getDba());
            existing.setIllinoisAccountId(orderRecord.getIllinoisAccountId());
            existing.setMobile(orderRecord.getMobile());
            existing.setGoodFaithPayment(orderRecord.getGoodFaithPayment());
            if (orderRecord.getTrackingNumber() != null && !orderRecord.getTrackingNumber().isEmpty()) {
                existing.setTrackingNumber(orderRecord.getTrackingNumber());
                existing.setTrackingNumberEntry(new Date());
                existing.setStatus(OrderRecord.STATUS_COMPLETE);
            } else if (orderRecord.getTrackingNumber() != null && orderRecord.getTrackingNumber().isEmpty()) {
                existing.setTrackingNumber(orderRecord.getTrackingNumber());
            }
            orderRecordRepo.save(existing);

            updateNote(orderNum, "SYSTEM", "EDITED", request.getUser());

            if (request.getContent().length() > 0) {
                updateNote(orderNum, "MANUAL", request.getContent(), request.getUser());
            }
            if (request.getEmailChangeContent() != null && request.getEmailChangeContent().length() > 0) {
                updateNote(orderNum, "SYSTEM", request.getEmailChangeContent(), request.getUser());
            }
            if (request.getTrackingNumberContent() != null && request.getTrackingNumberContent().length() > 0) {
                updateNote(orderNum, "SYSTEM", request.getTrackingNumberContent(), request.getUser());
            }
            return existing;
        } else {
            LOG.error("ERROR: Not Found Order: saveOrder(): 940");
            throw new NotFoundException();
        }
    }

    @PostMapping("/order/id/{orderNum}/sendEmailNotification")
    public void sendEmailNotificationToUser(HttpSession session, @PathVariable Long orderNum) throws IOException {
        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            OrderRecord orderRecord = optRecord.get();
            if (orderRecord.getStatus().equals(OrderRecord.STATUS_COMPLETE)) {
                mailjetSender.sendCompleteEmail(orderRecord);
            } else if (orderRecord.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
                mailjetSender.sendCancelledEmail(orderRecord);
            } else if (orderRecord.getStatus().equals(OrderRecord.STATUS_PROCESSING)) {
                mailjetSender.sendProcessingEmail(orderRecord);
            } else if (orderRecord.getStatus().equals(OrderRecord.STATUS_INCOMPLETE)) {
                mailjetSender.sendInCompleteEmail(orderRecord);
            }
        } else {
            LOG.error("ERROR: Not Found Order: sendEmailNotificationToUser(): 964");
            throw new NotFoundException();
        }
    }

    @PostMapping("/penalty-order/id/{orderNum}/sendEmailNotification")
    public void sendEmailNotificationToUser4Penalty(HttpSession session, @PathVariable Long orderNum) throws IOException {
        Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            PenaltyOrder orderRecord = optRecord.get();
            if (orderRecord.getStatus().equals(OrderRecord.STATUS_COMPLETE)) {
                mailjetSender.sendPenaltyOrderCompleteEmail(orderRecord);
            } else if (orderRecord.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
                mailjetSender.sendPenaltyOrderCancelledEmail(orderRecord);
            } else if (orderRecord.getStatus().equals(OrderRecord.STATUS_PROCESSING)) {
                mailjetSender.sendPenaltyOrderProcessingEmail(orderRecord);
            } else if (orderRecord.getStatus().equals(OrderRecord.STATUS_INCOMPLETE)) {
                mailjetSender.sendPenaltyOrderIncompleteEmail(orderRecord);
            }
        } else {
            LOG.error("ERROR: Not Found Order: sendEmailNotificationToUser4Penalty(): 964");
            throw new NotFoundException();
        }
    }

    @PostMapping("/ein/id/{orderNum}/sendEmailNotification")
    public void sendEmailNotificationToUser4Ein(HttpSession session, @PathVariable Long orderNum) throws IOException {
        Optional<EinOrder> optRecord = einRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            EinOrder einOrder = optRecord.get();
            if (einOrder.getStatus().equals(OrderRecord.STATUS_COMPLETE)) {
                mailjetSender.sendEinOrderCompleteEmail(einOrder);
            } else if (einOrder.getStatus().equals(OrderRecord.STATUS_CANCELLED)) {
                mailjetSender.sendEinOrderCancelledEmail(einOrder);
            } else if (einOrder.getStatus().equals(OrderRecord.STATUS_FAILED)) {
                mailjetSender.sendEinFailedEmail(einOrder);
            } else if (einOrder.getStatus().equals(OrderRecord.STATUS_PROCESSING)) {
                mailjetSender.sendEinOrderProcessingEmail(einOrder);
            } else if (einOrder.getStatus().equals(OrderRecord.STATUS_AWAITING_SIGNATURE_SERVICE)) {
                mailjetSender.sendEinAwaitingSignatureServiceEmail(einOrder);
            } else if (einOrder.getStatus().equals(OrderRecord.STATUS_SIGNED)) {
                mailjetSender.sendEinPaymentMissingEmail(einOrder);
            } else if (einOrder.getStatus().equals(OrderRecord.STATUS_INCOMPLETE)) {
                mailjetSender.sendEinIncompleteEmail(einOrder);
            }
        } else {
            LOG.error("ERROR: Not Found Order: sendEmailNotificationToUser4Ein(): 964");
            throw new NotFoundException();
        }
    }

    @PostMapping("/ein/id/{orderNum}/manually-run")
    public void manuallyRun(HttpSession session, @PathVariable Long orderNum) {
        Optional<EinOrder> optRecord = einRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            EinOrder einOrder = optRecord.get();
            einOrder.setManuallyRun(true);
            einOrder.setAccessDeniedCount(0);
            einOrder.setRestartsCount(0);
            einOrder.setStartAfterMillis(null);
            einOrder.setStatus(OrderRecord.STATUS_PROCESSING);
            einOrder.setErrorCode(null);
            // NOTE: We do not want to send email every time we do manual run (e.g. in case of multiple EIN Bot errors)
            // mailjetSender.sendEinOrderProcessingEmail(einOrder);
            einRecordRepo.save(einOrder);
        } else {
            LOG.error("ERROR: Not Found Order: manuallyRun(): 964");
            throw new NotFoundException();
        }
    }

    @GetMapping("/oic/orderNum/{orderNum}")
    public @ResponseBody OicModel getOic(HttpSession session, @PathVariable Long orderNum) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        OicModel details = oicRepository.getByOrderNum(orderNum);
        if (details != null) {
            return details;
        } else {
            LOG.error("ERROR: Not Found : getOic(): 981");
            throw new NotFoundException();
        }
    }

    @PutMapping("/oic")
    public void putOic(HttpSession session, @RequestBody OicModel oic) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        try {
            authService.requireRoles(session, roles);
            oicRepository.save(oic);
        } catch (Exception e) {
            LOG.error("ERROR: Put OIC: putOic(): ", e);
        }
    }

    @GetMapping("/taxlien/orderNum/{orderNum}")
    public @ResponseBody TaxLienRemovalDetails getTaxLien(HttpSession session, @PathVariable Long orderNum) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        TaxLienRemovalDetails details = taxLienRemovalRepo.getByOrderNum(orderNum);
        if (details != null) {
            return details;
        } else {
            LOG.error("ERROR: Not Found: getTaxLie()");
            throw new NotFoundException();
        }
    }

    @PutMapping("/taxlien")
    public void putTaxLienDetails(HttpSession session, @RequestBody TaxLienRemovalDetails taxLien) {
        try {
            List<AuthRole> roles = new ArrayList<AuthRole>();
            roles.add(AuthRole.ADMIN);
            roles.add(AuthRole.STANDARDUSER);
            authService.requireRoles(session, roles);
            taxLienRemovalRepo.save(taxLien);
        } catch (Exception e) {
            LOG.error("ERROR: Saving TaxLienDetails: putTaxLienDetails()");
        }
    }

    @GetMapping("/paymentPlan/orderNum/{orderNum}")
    public @ResponseBody PaymentPlanDetails getPaymentPlanDetails(HttpSession session, @PathVariable Long orderNum) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        PaymentPlanDetails details = paymentPlanRepo.findByOrderNum(orderNum);

        if (details != null) {
            return details;
        } else {
            LOG.error("ERROR: Not Found: getPaymentPlanDetails()");
            throw new NotFoundException();
        }
    }

    @GetMapping("/envelopeTrackingNumber/orderNum/{orderNum}")
    public @ResponseBody ArrayList<ShippingLabels> getEnvelopeTrackingNumber(HttpSession session,
                                                                             @PathVariable Long orderNum) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        ArrayList<ShippingLabels> shippingLabels = shippingLabelsRepo.findByOrderNumber(orderNum);
        return shippingLabels;
    }

    @PutMapping("/paymentPlan")
    public void putPaymentPlanDetails(HttpSession session, @RequestBody PaymentPlanDetails paymentPlan) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        try {
            roles.add(AuthRole.ADMIN);
            roles.add(AuthRole.STANDARDUSER);
            authService.requireRoles(session, roles);
            paymentPlanRepo.save(paymentPlan);
        } catch (Exception e) {
            LOG.error("ERROR: Saving PaymentPlanDetails", e);
        }
    }

    @PutMapping("/transaction/{authTransactionId}/refund")
    public void refundTransaction(HttpSession session, @RequestBody RefundTransactionRequest request,
                                  @PathVariable String authTransactionId) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        CreateTransactionResponse response = authorizeNetFacade.refundTransaction(authTransactionId,
                request.getCardNumber(),
                request.getExpirationDate(), new BigDecimal(request.getAmount()), request.getInvoiceNumber());
        if (response == null) {
            throw new RuntimeException("ERROR: Unable to refund transaction.");
        } else {
            OrderRecord record = orderRecordRepo.findByAuthorizeTransactionId(authTransactionId);
            record.setStatus(OrderRecord.STATUS_CANCELLED);
            record.setRefundAuthTransId(response.getTransactionResponse().getTransId());
            orderRecordRepo.save(record);
            try {
                mailjetSender.sendCancelledEmail(record);
            } catch (IOException e) {
                LOG.error("ERROR: Unable to send email", e);
            }
        }
    }

    @PutMapping("/transaction/{authTransactionId}/void")
    public void voidTransaction(HttpSession session, @PathVariable String authTransactionId) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        CreateTransactionResponse response = authorizeNetFacade.voidTransaction(authTransactionId);
        if (response == null) {
            throw new RuntimeException("ERRPR: Unable to void transaction.");
        } else {
            OrderRecord record = orderRecordRepo.findByAuthorizeTransactionId(authTransactionId);
            if (record != null) {
                record.setStatus(OrderRecord.STATUS_CANCELLED);
                orderRecordRepo.save(record);
                try {
                    mailjetSender.sendCancelledEmail(record);
                } catch (IOException e) {
                    LOG.error("ERROR: Unable to send email", e);
                }
            }
        }
    }

    @PutMapping("/transaction/{authTransactionId}/refundOnChargeBack")
    public void refundTransactionOnChargeBack(HttpSession session, @RequestBody RefundTransactionRequest request,
                                              @PathVariable String authTransactionId) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        CreateTransactionResponse response = authorizeNetFacade.refundTransaction(authTransactionId,
                request.getCardNumber(),
                request.getExpirationDate(), new BigDecimal(request.getAmount()), request.getInvoiceNumber());
        if (response == null) {
            throw new RuntimeException("ERROR: Unable to refund transaction.");
        } else {
            OrderRecord record = orderRecordRepo.findByAuthorizeTransactionId(authTransactionId);
            record.setStatus(OrderRecord.STATUS_CHARGE_BACK);
            record.setRefundAuthTransId(response.getTransactionResponse().getTransId());
            orderRecordRepo.save(record);
        }
    }

    @PutMapping("/transaction/{authTransactionId}/voidOnChargeBack")
    public void voidTransactionOnChargeBack(HttpSession session, @PathVariable String authTransactionId) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        CreateTransactionResponse response = authorizeNetFacade.voidTransaction(authTransactionId);
        if (response == null) {
            throw new RuntimeException("ERROR: Unable to void transaction.");
        } else {
            OrderRecord record = orderRecordRepo.findByAuthorizeTransactionId(authTransactionId);
            if (record != null) {
                record.setStatus(OrderRecord.STATUS_CHARGE_BACK);
                orderRecordRepo.save(record);
            }
        }
    }

    @PutMapping("/transaction/{authTransactionId}/onHold")
    public void holdTransaction(HttpSession session, @PathVariable String authTransactionId,
                                @RequestBody OnHoldRequest request) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        OrderRecord record = orderRecordRepo.findByAuthorizeTransactionId(authTransactionId);
        if (OrderRecord.STATUS_ON_HOLD.equals(record.getStatus())) {
            record.setStatus(OrderRecord.STATUS_PROCESSING);
        } else {
            record.setStatus(OrderRecord.STATUS_ON_HOLD);
        }
        orderRecordRepo.save(record);
        if (request.isSendNotification()) {
            mailjetSender.sendProcessingEmail(record);
        }
        updateNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
    }

    @GetMapping("/transaction/{authTransactionId}")
    public @ResponseBody TransactionRecord getTransactionRecord(HttpSession session,
                                                                @PathVariable String authTransactionId) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        GetTransactionDetailsResponse response = authorizeNetFacade.getTransactionDetails(authTransactionId);

        if (response != null) {
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setAuthorizeTransactionId(authTransactionId);
            if (response.getTransaction() != null) {
                transactionRecord.setTransactionType(response.getTransaction().getTransactionType());
                transactionRecord.setTransactionStatus(response.getTransaction().getTransactionStatus());
                if (response.getTransaction().getPayment() != null
                        && response.getTransaction().getPayment().getCreditCard() != null) {
                    transactionRecord.setCardNumberMasked(
                            response.getTransaction().getPayment().getCreditCard().getCardNumber());
                    transactionRecord.setExpirationDateMasked(
                            response.getTransaction().getPayment().getCreditCard().getExpirationDate());
                }
                if (response.getTransaction().getAuthAmount() != null) {
                    transactionRecord.setAuthAmount(response.getTransaction().getAuthAmount().toString());
                }
                transactionRecord.setAuthCode(response.getTransaction().getAuthCode());
                transactionRecord.setAvsResponse(response.getTransaction().getAVSResponse());
                transactionRecord.setCardCodeResponse(response.getTransaction().getCardCodeResponse());
                transactionRecord.setCavvResponse(response.getTransaction().getCAVVResponse());
                transactionRecord
                        .setResponseReasonCode(String.valueOf(response.getTransaction().getResponseReasonCode()));
                transactionRecord
                        .setResponseReasonDescription(response.getTransaction().getResponseReasonDescription());
            }
            transactionRecordRepo.save(transactionRecord);
            return transactionRecord;
        } else {
            return null;
        }
    }

    @GetMapping("/partnercodes")
    public @ResponseBody Iterable<PartnerCode> getPartnerCodeList(HttpSession session) {
        authService.requireRole(session, AuthRole.ADMIN);
        return partnerCodeRepo.findAll();
    }

    @PostMapping("/partnercodes")
    public @ResponseBody PartnerCode addPartnerCode(HttpSession session, @RequestBody PartnerCode newPartnerCode) {
        authService.requireRole(session, AuthRole.ADMIN);
        return partnerCodeRepo.save(newPartnerCode);
    }

    @PutMapping("/partnercodes/{codeNum}")
    public @ResponseBody PartnerCode updatePartnerCode(HttpSession session, @PathVariable Long codeNum,
                                                       @RequestBody PartnerCode newPartnerCode) throws NotFoundException {
        authService.requireRole(session, AuthRole.ADMIN);
        Optional<PartnerCode> foundPartnerCode = partnerCodeRepo.findById(codeNum);
        if (foundPartnerCode.isPresent()) {
            PartnerCode partnerCode = foundPartnerCode.get();
            partnerCode.setCode(newPartnerCode.getCode());
            partnerCode.setPercentageOff(newPartnerCode.getPercentageOff());
            partnerCode.setPartnerName(newPartnerCode.getPartnerName());
            partnerCode.setContactName(newPartnerCode.getContactName());
            partnerCode.setCommission(newPartnerCode.getCommission());
            partnerCode.setCommissionTypeId(newPartnerCode.getCommissionTypeId());
            partnerCode.setStreetAddress(newPartnerCode.getStreetAddress());
            partnerCode.setCity(newPartnerCode.getCity());
            partnerCode.setState(newPartnerCode.getState());
            partnerCode.setZip(newPartnerCode.getZip());
            partnerCode.setNotes(newPartnerCode.getNotes());
            return partnerCodeRepo.save(partnerCode);
        } else {
            LOG.error("ERROR: Update Partner Code");
            throw new NotFoundException();
        }
    }

    @DeleteMapping("/partnercodes/{codeNum}")
    public void deletePartnerCode(HttpSession session, @PathVariable Long codeNum) {
        try {
            partnerCodeRepo.deleteById(codeNum);
        } catch (Exception e) {
            LOG.error("ERROR: Delete Partner Code", e);
        }
    }

    @GetMapping("/partnercodes/usagereport")
    public @ResponseBody List<PCUsageReport> getPartnerUsageReport(HttpSession session) {
        authService.requireRole(session, AuthRole.ADMIN);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse("2020-01-01", inputFormatter);
        LocalDate end = LocalDate.parse("2020-04-16", inputFormatter);
        Date startDate = java.sql.Date.valueOf(start);
        Date endDate = java.sql.Date.valueOf(end);

        List<PCUsageReport> orders = (List<PCUsageReport>) orderRecordRepo.findOrdersForUsageReport(startDate, endDate);

        return orders;
    }

    @GetMapping("/partnercodes/usagereport.xlsx")
    public void exportUsagereportExcel(HttpSession session, HttpServletResponse response, OrderSearchQuery query)
            throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(query.getStartAt(), inputFormatter);
        LocalDate end = LocalDate.parse(query.getEndAt(), inputFormatter);
        Date startDate = java.sql.Date.valueOf(start);
        Date endDate = java.sql.Date.valueOf(end);
        String daterangeStr = query.getStartAt() + " - " + query.getEndAt();
        try {
            List<PCUsageReport> orders = (List<PCUsageReport>) orderRecordRepo.findOrdersForUsageReport(startDate,
                    endDate);
            paymentPlanExport.exportUsageReportExcel(response.getOutputStream(), orders, daterangeStr);
        } catch (Exception ee) {
            LOG.error("ERROR: exception fetching order", ee);
            System.out.println("exception fetching order ==>" + ee.toString());
        }
    }

    // CouponCode API
    @GetMapping("/couponcodes")
    public @ResponseBody Iterable<CouponCode> getCouponCodeList(HttpSession session) {
        authService.requireRole(session, AuthRole.ADMIN);
        return couponCodeRepo.findAll();
    }

    @PostMapping("/couponcodes")
    public @ResponseBody CouponCode addCouponCode(HttpSession session, @RequestBody CouponCode newCouponCode) {
        authService.requireRole(session, AuthRole.ADMIN);
        return couponCodeRepo.save(newCouponCode);
    }

    @PutMapping("/couponcodes/{codeNum}")
    public @ResponseBody CouponCode updateCouponCode(HttpSession session, @PathVariable Long codeNum,
                                                     @RequestBody PartnerCode newCouponCode) throws NotFoundException {
        authService.requireRole(session, AuthRole.ADMIN);
        Optional<CouponCode> foundCouponCode = couponCodeRepo.findById(codeNum);
        if (foundCouponCode.isPresent()) {
            CouponCode couponCode = foundCouponCode.get();
            couponCode.setCode(newCouponCode.getCode());
            couponCode.setPercentageOff(newCouponCode.getPercentageOff());
            couponCode.setPartnerName(newCouponCode.getPartnerName());
            couponCode.setContactName(newCouponCode.getContactName());
            couponCode.setCommission(newCouponCode.getCommission());
            couponCode.setCommissionTypeId(newCouponCode.getCommissionTypeId());
            couponCode.setStreetAddress(newCouponCode.getStreetAddress());
            couponCode.setCity(newCouponCode.getCity());
            couponCode.setState(newCouponCode.getState());
            couponCode.setZip(newCouponCode.getZip());
            couponCode.setNotes(newCouponCode.getNotes());
            return couponCodeRepo.save(couponCode);
        } else {
            LOG.error("ERROR: Not Found CouponCode: updateCouponCode()");
            throw new NotFoundException();
        }
    }

    @DeleteMapping("/couponcodes/{codeNum}")
    public void deleteCouponCode(HttpSession session, @PathVariable Long codeNum) {
        try {
            couponCodeRepo.deleteById(codeNum);
        } catch (Exception e) {
            LOG.error("ERROR: Delete CouponCode", e);
        }
    }

    @PostMapping("/addMailRoomUser")
    public @ResponseBody Map<String, Object> addMailRoomUser(HttpSession session,
                                                             @RequestBody MailRoomUserRequest mailRoomUserRequest) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        Map<String, Object> result = new HashMap<String, Object>();

        boolean update = mailRoomUserRequest.getUpdate();
        if (update) {
            Optional<MailRoomUser> optionalUser = mailRoomUserRepo.findById(mailRoomUserRequest.getUserNum());

            if (optionalUser.isPresent()) {

                MailRoomUser user = optionalUser.get();
                Optional<MailRoomUser> checkUser = mailRoomUserRepo
                        .findOne(MailRoomUserSpecs.searchByEmail(mailRoomUserRequest.getEmail()));
                if (checkUser.isPresent() && checkUser.get().getUserNum() != user.getUserNum()) {
                    result.put("msg", "Email is already in use.");
                    result.put("code", 0);
                } else {
                    user.setUserName(mailRoomUserRequest.getUserName());
                    user.setEmail(mailRoomUserRequest.getEmail());
                    user.setPhone(mailRoomUserRequest.getPhone());
                    if (mailRoomUserRequest.getPassword() != null && mailRoomUserRequest.getPassword().length() > 0) {
                        user.setPassword(mailRoomUserRequest.getPassword());
                    }

                    user = mailRoomUserRepo.save(user);
                    result.put("user", user);
                    result.put("code", 1);
                }
            } else {
                result.put("msg", "Could not find user information.");
                result.put("code", 0);
            }

        } else {
            Optional<MailRoomUser> user = mailRoomUserRepo
                    .findOne(MailRoomUserSpecs.searchByEmail(mailRoomUserRequest.getEmail()));
            if (user.isPresent()) {
                result.put("msg", "Email is already in use.");
                result.put("code", 0);
            } else {
                MailRoomUser mailRoomUser = new MailRoomUser();
                mailRoomUser.setUserName(mailRoomUserRequest.getUserName());
                mailRoomUser.setEmail(mailRoomUserRequest.getEmail());
                mailRoomUser.setPhone(mailRoomUserRequest.getPhone());
                mailRoomUser.setPassword(mailRoomUserRequest.getPassword());
                mailRoomUser = mailRoomUserRepo.save(mailRoomUser);

                result.put("user", mailRoomUser);
                result.put("code", 1);
            }
        }

        return result;
    }

    @PostMapping("/deleteMailRoomUser/{userId}")
    public @ResponseBody Map<String, Object> deleteMailRoomUser(HttpSession session, @PathVariable Long userId)
            throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 1);
        mailRoomUserRepo.deleteById(userId);
        return result;
    }

    @GetMapping("/mailRoomUsers")
    public @ResponseBody ArrayList<MailRoomUser> getMailRoomUsers(HttpSession session) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        ArrayList<MailRoomUser> users = (ArrayList<MailRoomUser>) mailRoomUserRepo.findAll();
        return users;
    }

    @GetMapping("/mailRoomBatches")
    public @ResponseBody ArrayList<Batch> getMailRoomBatches(HttpSession session) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        List<Object[]> batches = batchRepo.findAllBatch();
        return AdminUtil.convertBatch(batches);
    }

    @PostMapping("/deleteBatch/{batchId}")
    public @ResponseBody Map<String, Object> deleteBatch(HttpSession session, @PathVariable Long batchId)
            throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        List<OrderRecord> orderRecords = orderRecordRepo.findAll(OrderSpecs.orderSearchByBatchDocument(batchId));
        for (OrderRecord orderRecord : orderRecords) {
            orderRecord.setBatch(null);
            orderRecordRepo.save(orderRecord);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 1);

        // Please check comments from TD-306 for details before removing s3PdfPath and s3LabelPath from S3 bucket:
        // Optional<Batch> batch = batchRepo.findById(batchId);
        // String s3PdfPath = batch.get().getPdfFile();
        // String s3LabelPath = batch.get().getShippingLabels();

        batchRepo.deleteById(batchId);
        return result;
    }

    @PostMapping("/voidBatch/{batchId}")
    public void voidBatch(HttpSession session, @PathVariable Long batchId) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        try {
            authService.requireRoles(session, roles);
            ArrayList<ShippingLabels> shippingLabels = shippingLabelsRepo.findByMailroomBatchId(batchId);
            for (ShippingLabels label : shippingLabels) {
                adminSharedService.setShipingLabelAsVoid(label);
            }
        } catch (Exception e) {
            LOG.error("ERROR: Void Batch", e);
        }
    }

    @PostMapping("/upload_batch")
    public @ResponseBody List<UploadResult> uploadBatch(HttpServletRequest request, HttpSession session,
                                                        @RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new InputStreamReader(file1.getInputStream()))) {

            boolean shouldReject = false;

            List<UploadResult> results = new ArrayList<>();
            ArrayList<Optional<OrderRecord>> records = new ArrayList<>();

            Map<String, String> next;
            while ((next = reader.readMap()) != null) {
                UploadResult result = new UploadResult();
                try {
                    Long orderNum;
                    try {
                        orderNum = Long.parseLong(next.get("Order Number"));
                    } catch (Exception e) {
                        LOG.error("ERROR: Parsing OrderNumber: uploadBatcn()", e);
                        continue;
                    }

                    Optional<OrderRecord> optionalRecord = orderRecordRepo.findById(orderNum);
                    records.add(optionalRecord);

                    if (optionalRecord.isPresent()) {
                        OrderRecord record = optionalRecord.get();
                        if (OrderRecord.STATUS_COMPLETE.equalsIgnoreCase(record.getStatus())) {

                            results.add(result);
                            result.setOrderNum(orderNum);
                            result.setMessage("Error - Order was completed");
                            shouldReject = true;
                            break;
                        }

                        if (record.getBatch() != null) {
                            results.add(result);
                            result.setOrderNum(orderNum);
                            result.setMessage("Error - Duplicate Order");
                            shouldReject = true;
                            break;
                        }

                    } else {
                        results.add(result);
                        result.setOrderNum(orderNum);
                        result.setMessage("Error - Order number did not exist in database");
                        shouldReject = true;
                        break;
                    }
                } catch (Exception e) {
                    LOG.error("ERROR: occured in uploading batch", e);
                    results.add(result);
                    result.setMessage(
                            "Error - unable to process row. Please contact the administrator and check the logs.");
                    shouldReject = true;
                    break;
                }
            }

            if (!shouldReject) {
                Batch batch = new Batch();
                String uniqueFilename = UUID.randomUUID().toString();
                PDFMergerUtility pdfMerger = new PDFMergerUtility();
                pdfMerger.setDestinationFileName(this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION);
                // pdfMerger.addSource()
                InputStream targetStream = file2.getInputStream();
                pdfMerger.addSource(targetStream);
                pdfMerger.mergeDocuments(null);
                File newFile = new File(this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION);
                String pdfFile = this.uploadFileToAwsS3Bucket(newFile, uniqueFilename + TaxConstants.PDF_EXTENSION);
                batch.setPdfFile(pdfFile);
                // batch.setPdfFile(file2.getBytes());
                batch.setStatus("Ready");
                batchRepo.save(batch);

                for (Optional<OrderRecord> optionalRecord : records) {
                    if (optionalRecord.isPresent()) {
                        OrderRecord record = optionalRecord.get();
                        if (OrderRecord.STATUS_COMPLETE.equalsIgnoreCase(record.getStatus()))
                            continue;
                        record.setBatch(batch.getId());
                        orderRecordRepo.save(record);
                    }
                }

                List<MailRoomUser> roomUsers = (List<MailRoomUser>) mailRoomUserRepo.findAll();
                for (MailRoomUser mailRoomUser : roomUsers) {
                    mailjetSender.sendEmailOfMailRoom(mailRoomUser);
                }
            }
            return results;
        } catch (IOException e) {
            LOG.error("ERROR: Upload Batch File", e);
            throw new BadRequestException();
        }
    }

    @GetMapping("/mailroom/downloadPDF")
    public ResponseEntity<InputStreamResource> downloadPDF(HttpServletRequest request, HttpSession session,
                                                           @RequestParam("start") Long startId, @RequestParam("end") Long endId) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        String uniqueFilename = UUID.randomUUID().toString();
        String filePath = this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION;
        PDFMergerUtility pdfMerger = new PDFMergerUtility();

        pdfMerger.setDestinationFileName(filePath);
        Boolean isPDFFound = false;
        AWSCredentials awsCred = new BasicAWSCredentials(
                this.awsAccessKey,
                this.awsSecretKey);
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                .withRegion(Regions.US_EAST_2)
                .build();
        if (startId == endId) {
            Optional<Batch> optionalBatch = batchRepo.findById(startId);
            if (optionalBatch.get().getPdfFile() != null) {
                S3Object object = s3client.getObject(this.awsBucketName, optionalBatch.get().getPdfFile());
                S3ObjectInputStream objectContent = object.getObjectContent();
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + TaxConstants.PORT_POLIO_PDF)
                        .body(new InputStreamResource(objectContent));
            } else {
                LOG.error("ERROR: Pdf Not Found: downloadPdf(): 1604");
                throw new NotFoundException();
            }

        } else {
            for (long batchId = startId; batchId <= endId; batchId++) {
                Optional<Batch> optionalBatch = batchRepo.findById(batchId);
                if (optionalBatch.isPresent()) {
                    S3Object object = s3client.getObject(this.awsBucketName, optionalBatch.get().getPdfFile());
                    S3ObjectInputStream objectContent = object.getObjectContent();
                    pdfMerger.addSource(objectContent);
                    isPDFFound = true;
                }
            }

            if (isPDFFound) {
                System.out.println("START DOWNLOAD");
                pdfMerger.mergeDocuments(null);
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                this.adminUtil.deleteFiles(uniqueFilename);
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + TaxConstants.PORT_POLIO_PDF)
                        .body(new InputStreamResource(new ByteArrayInputStream(fileContent)));
            } else {
                LOG.error("ERROR: Pdf Not Found: downloadPdf(): 1604");
                throw new NotFoundException();
            }
        }
    }

    @GetMapping("/mailroom/downloadLabels")
    public ResponseEntity<InputStreamResource> downloadLabels(HttpServletRequest request, HttpSession session,
                                                              @RequestParam("start") Long startId, @RequestParam("end") Long endId) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        String uniqueFilename = UUID.randomUUID().toString();
        String filePath = this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION;
        PDFMergerUtility pdfMerger = new PDFMergerUtility();

        pdfMerger.setDestinationFileName(filePath);
        Boolean isPDFFound = false;
        AWSCredentials awsCred = new BasicAWSCredentials(
                this.awsAccessKey,
                this.awsSecretKey);
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                .withRegion(Regions.US_EAST_2)
                .build();
        for (long batchId = startId; batchId <= endId; batchId++) {
            Optional<Batch> optionalBatch = batchRepo.findById(batchId);
            if (optionalBatch.isPresent()) {
                S3Object object = s3client.getObject(this.awsBucketName, optionalBatch.get().getShippingLabels());
                S3ObjectInputStream objectContent = object.getObjectContent();
                pdfMerger.addSource(objectContent);
                isPDFFound = true;
            }
        }

        if (isPDFFound) {
            pdfMerger.mergeDocuments(null);
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            this.adminUtil.deleteFiles(uniqueFilename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment;filename=" + startId + "-" + endId + "-"
                                    + TaxConstants.CURRENT_ORDERS_LABELS_PDF)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(new ByteArrayInputStream(fileContent)));
        } else {
            LOG.error("ERROR: Labels Not Found: downloadLabel(): 1642");
            throw new NotFoundException();
        }
    }

    @GetMapping("/mailroom/downloadPDF/{batchId}")
    public ResponseEntity<InputStreamResource> downloadPDF(HttpServletRequest request, HttpSession session,
                                                           @PathVariable Long batchId) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        Optional<Batch> optionalBatch = batchRepo.findById(batchId);
        if (optionalBatch.isPresent()) {
            AWSCredentials awsCred = new BasicAWSCredentials(
                    this.awsAccessKey,
                    this.awsSecretKey);
            AmazonS3 s3client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                    .withRegion(Regions.US_EAST_2)
                    .build();
            S3Object object = s3client.getObject(this.awsBucketName, optionalBatch.get().getPdfFile());
            S3ObjectInputStream objectContent = object.getObjectContent();
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + TaxConstants.PORT_POLIO_PDF)
                    .body(new InputStreamResource(objectContent));
        } else {
            LOG.error("ERROR: Pdf Not Found: downloadPdf(): 1663");
            throw new NotFoundException();
        }
    }

    @PostMapping("/trackShipping")
    public void trackShipping(@RequestBody ShippingTrackWebhookPayload shippingTrackWebhookPayload) {
        try {
            if (shippingTrackWebhookPayload.getResourceType().equals("API_TRACK")) {
                ShippingTrackData shippingTrackData = shippingTrackWebhookPayload.getShippingTrackData();
                ShippingLabels shippingLabel = null;
                if (shippingTrackData.getTrackingNumber() != null) {
                    shippingLabel = shippingLabelsRepo.findByTrackingNumber(shippingTrackData.getTrackingNumber());
                } else if (shippingTrackData.getLabelUrl() != null) {
                    String[] labelStr = shippingTrackData.getLabelUrl().split("/");
                    String label = labelStr[labelStr.length - 1];
                    shippingLabel = shippingLabelsRepo.findByLabelId(label);
                }

                if (shippingLabel != null) {
                    shippingLabel.setLabelUrl(shippingTrackData.getLabelUrl());
                    shippingLabel.setStatusCode(shippingTrackData.getStatusCode());
                    shippingLabel.setStatusDescription(shippingTrackData.getStatusDescription());
                    shippingLabel.setCarrierStatusCode(shippingTrackData.getCarrierStatusCode());
                    shippingLabel.setCarrierStatusDescription(shippingTrackData.getCarrierStatusDescription());
                    shippingLabel.setEstimatedDeliveryDate(shippingTrackData.getEstimatedDeliveryDate());
                    shippingLabel.setActualDeliveryDate(shippingTrackData.getActualDeliveryDate());
                    shippingLabelsRepo.save(shippingLabel);
                    Optional<OrderRecord> order = orderRecordRepo.findById(shippingLabel.getOrderId());
                    PaymentPlanDetails paymentPlanDetails = paymentPlanRepo.findByOrderNum(shippingLabel.getOrderId());

                    if (order != null && order.isPresent() && paymentPlanDetails != null) {
                        OrderRecord orderRecord = order.get();
                        if (shippingTrackData.getStatusCode() != null
                                && shippingTrackData.getStatusCode().equals("DE")) {
                            mailjetSender.sendShippingPackageDeliveredEmail(order.get());
                            orderRecord.setShipengineStatusCode(shippingTrackData.getStatusDescription());
                            orderRecordRepo.save(orderRecord);
                        } else if (shippingTrackData.getStatusCode() != null
                                && shippingTrackData.getStatusCode().equals("EX")) {
                            mailjetSender.sendInvalidAddressShippingEmail(order.get(), paymentPlanDetails);
                            orderRecord.setShipengineStatusCode(shippingTrackData.getStatusDescription());
                            orderRecordRepo.save(orderRecord);
                        }
                    }

                }

            }
        } catch (Exception ex) {
            LOG.error("ERROR: Tracking Shipping Error", ex);
            System.out.println("tracking error is" + ex.getMessage());
            throw new NotFoundException();
        }
    }

    public TaxLienExport getTaxLienExport() {
        return taxLienExport;
    }

    public void setTaxLienExport(TaxLienExport taxLienExport) {
        this.taxLienExport = taxLienExport;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private HashMap<String, Object> mailroomProcessOrders() throws Exception {
        List<String> versionsList = new ArrayList<String>();
        try {
            List<ProcessedOrders> processedOrders = processedOrdersRepo.findUnsentProcessedOrders(false);
            for (ProcessedOrders pOrder : processedOrders) {
                ArrayList<ShippingLabels> shippingLabels = shippingLabelsRepo.findByProcessBatchId(pOrder.getId());
                for (ShippingLabels label : shippingLabels) {
                    adminSharedService.setShipingLabelAsVoid(label);
                }
            }
            processedOrdersRepo.deleteProcessedOrders(false);
        } catch (Exception e) {
            LOG.error("ERROR: finding processed orders: mailroomProcessOrders()" + e.toString());
        }

        // Get the list of stale orders
        List<FulfillmentOrder> staleOnHoldOrders = getStaleOnHoldOrders();
        if (staleOnHoldOrders != null && staleOnHoldOrders.size() > 0) {
            for (int i = 0; i < staleOnHoldOrders.size(); i++) {
                Long currentOrderNum = Long.valueOf(staleOnHoldOrders.get(i).getId());
                String orderType = staleOnHoldOrders.get(i).getOrderType();
                if (FulfillmentOrder.ORDER_TYPE_PAYMENT_PLAN.equals(orderType)) {
                    Optional<OrderRecord> optRecord = orderRecordRepo.findById(currentOrderNum);
                    OrderRecord existing = optRecord.get();
                    Boolean isSucceed = false;
                    // If the order has an authorize.net transaction ID, refund the order
                    if (existing != null) {
                        if (existing.getAuthorizeTransactionId() != null
                                && !existing.getAuthorizeTransactionId().isEmpty()
                                && (existing.getRefundAuthTransId() == null
                                || existing.getRefundAuthTransId().isEmpty())) {
                            GetTransactionDetailsResponse transDetailsResponse = authorizeNetFacade
                                    .getTransactionDetails(existing.getAuthorizeTransactionId());
                            if (transDetailsResponse != null && transDetailsResponse.getTransaction() != null) {
                                CreateTransactionResponse response = authorizeNetFacade.refundTransaction(
                                        existing.getAuthorizeTransactionId(),
                                        transDetailsResponse.getTransaction().getPayment().getCreditCard()
                                                .getCardNumber(),
                                        transDetailsResponse.getTransaction().getPayment().getCreditCard()
                                                .getExpirationDate(),
                                        transDetailsResponse.getTransaction().getAuthAmount(),
                                        existing.getOrderNum().toString());
                                if (response == null) {
                                    LOG.error("ERROR: Unable to refund transaction : mailroomProcessOrders()");
                                } else {
                                    isSucceed = true;
                                    existing.setRefundAuthTransId(response.getTransactionResponse().getTransId());
                                }
                            }
                        }
                    }

                    // Cancel the order
                    if (isSucceed) {
                        existing.setStatus(OrderRecord.STATUS_CANCELLED);
                        orderRecordRepo.save(existing);
                        try {
                            mailjetSender.sendCancelledEmail(existing);
                        } catch (IOException e) {
                            LOG.error("ERROR: Unable to send email : mailroomProcessOrders()", e);
                        }
                    }
                } else {
                    Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(currentOrderNum);
                    PenaltyOrder existing = optRecord.get();
                    Boolean isSucceed = false;
                    // If the order has an authorize.net transaction ID, refund the order
                    if (existing != null) {
                        if (existing.getAuthorizeTransactionId() != null
                                && !existing.getAuthorizeTransactionId().isEmpty()
                                && (existing.getRefundAuthTransId() == null
                                || existing.getRefundAuthTransId().isEmpty())) {
                            GetTransactionDetailsResponse transDetailsResponse = authorizeNetFacade
                                    .getTransactionDetails(existing.getAuthorizeTransactionId());
                            if (transDetailsResponse != null && transDetailsResponse.getTransaction() != null) {
                                CreateTransactionResponse response = authorizeNetFacade.refundTransaction(
                                        existing.getAuthorizeTransactionId(),
                                        transDetailsResponse.getTransaction().getPayment().getCreditCard()
                                                .getCardNumber(),
                                        transDetailsResponse.getTransaction().getPayment().getCreditCard()
                                                .getExpirationDate(),
                                        transDetailsResponse.getTransaction().getAuthAmount(),
                                        existing.getId().toString());
                                if (response == null) {
                                    LOG.error("ERROR: Unable to refund transaction : mailroomProcessOrders()");
                                } else {
                                    isSucceed = true;
                                    existing.setRefundAuthTransId(response.getTransactionResponse().getTransId());
                                }
                            }
                        }
                    }

                    // Cancel the order
                    if (isSucceed) {
                        existing.setStatus(OrderRecord.STATUS_CANCELLED);
                        penaltyRecordRepo.save(existing);
                        mailjetSender.sendPenaltyOrderCancelledEmail(existing);
                    }
                }
            }
        }

        List<FulfillmentOrder> allOrders = getProcessingOrders();
        List<FulfillmentOrder>[] orderChunks = this.adminUtil.chunk(allOrders, 20);
        if (allOrders != null && allOrders.size() > 0) {
            AddressValidateResponse[] responseAddressValidate;
            for (int i = 0; i < orderChunks.length; i++) {
                responseAddressValidate = adminSharedService.postAddressValidateRequest(orderChunks[i]);
                for (int j = 0; j < responseAddressValidate.length; j++) {
                    Long currentOrderId = Long.parseLong(responseAddressValidate[j].getOriginal_address().getName());
                    FulfillmentOrder order = allOrders.stream()
                            .filter(o -> Long.valueOf(o.getId()).equals(currentOrderId))
                            .findAny()
                            .orElse(null);

                    if (order != null) {

                        String shipAddr = order.getShippingAddress1();

                        String respStatus = responseAddressValidate[j].getStatus();
                        String orderType = order.getOrderType();

                        if (FulfillmentOrder.ORDER_TYPE_PAYMENT_PLAN.equals(orderType)) {
                            Optional<OrderRecord> optRecord = orderRecordRepo.findById(currentOrderId);
                            if (optRecord.isPresent()) {
                                OrderRecord rec = optRecord.get();

                                if (shipAddr != null && shipAddr.startsWith("PO BOX")) {
                                    rec.setAddressStatus("verified");
                                } else {
                                    rec.setAddressStatus(respStatus);

                                    if ("unverified".equals(respStatus) || "error".equals(respStatus)) {
                                        rec.setStatus(OrderRecord.STATUS_ON_HOLD);
                                        LOG.error("Address Validation Response => 1868: " + currentOrderId,
                                                responseAddressValidate[j]);
                                        mailjetSender.sendOnHoldOrderEmail(rec);
                                        allOrders.removeIf(e -> e.getId().equals(currentOrderId));
                                        updateNote(currentOrderId, "SYSTEM", "ON HOLD DUE TO FULFILLMENT ERROR", "SYSTEM");
                                    }
                                }

                                orderRecordRepo.save(rec);
                            }
                        } else {
                            Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(currentOrderId);
                            if (optRecord.isPresent()) {
                                PenaltyOrder rec = optRecord.get();

                                if (shipAddr != null && shipAddr.startsWith("PO BOX")) {
                                    rec.setAddressStatus("verified");
                                } else {
                                    rec.setAddressStatus(respStatus);

                                    if ("unverified".equals(respStatus) || "error".equals(respStatus)) {
                                        rec.setStatus(OrderRecord.STATUS_ON_HOLD);
                                        LOG.error("Address Validation Response => 1868: " + currentOrderId,
                                                responseAddressValidate[j]);
                                        mailjetSender.sendPenaltyOrderOnholdEmail(rec);
                                        allOrders.removeIf(e -> e.getId().equals(currentOrderId));
                                        updateNote(currentOrderId, "SYSTEM", "ON HOLD DUE TO FULFILLMENT ERROR", "SYSTEM");
                                    }
                                }

                                penaltyRecordRepo.save(rec);
                            }
                        }

                        /*
                        order.setAddressStatus(responseAddressValidate[j].getStatus());
                        if (responseAddressValidate[j].getStatus().equals("unverified")
                                || responseAddressValidate[j].getStatus().equals("error")) {
                            order.setStatus(OrderRecord.STATUS_ON_HOLD);
                            LOG.error("Address Validation Response => 1868: " + order.getOrderNum(),
                                    responseAddressValidate[j]);
                            mailjetSender.sendOnHoldOrderEmail(order);
                            allOrders.removeIf(e -> e.getOrderNum().equals(currentOrderId));
                            updateNote(currentOrderId, "SYSTEM", "ON HOLD DUE TO FULFILLMENT ERROR", "SYSTEM");
                        }
                        orderRecordRepo.save(order);
                        */
                    }
                }
                Thread.sleep(7000);
            }
        }

        orderChunks = this.adminUtil.chunk(allOrders, 195);
        Integer processedOrderCounter = 0;
        Integer allProcessedOrderCounter = 0;
        if (allOrders != null && allOrders.size() > 0) {
            HashMap<String, Object> finalResult = new HashMap<>();
            List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
            Integer forIndex = 0;
            for (List<FulfillmentOrder> orders : orderChunks) {
                String uniqueFilename = UUID.randomUUID().toString();
                String uniqueShipmentFileName = UUID.randomUUID().toString();
                PDFMergerUtility pdfMerger = new PDFMergerUtility();
                PDFMergerUtility pdfMergerShipment = new PDFMergerUtility();

                pdfMerger.setDestinationFileName(this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION);
                pdfMergerShipment.setDestinationFileName(
                        this.mailroomFilePath + uniqueShipmentFileName + TaxConstants.PDF_EXTENSION);
                List<FulfillmentOrder> ordersToUpdate = new ArrayList<FulfillmentOrder>();
                Boolean IfPdfFound = false;
                for (FulfillmentOrder order : orders) {
                    List<PdfOrderVersions> pdfOrders = pdfVersionRepo.findByOrderid(order.getId());
                    if (pdfOrders.size() > 0) {
                        if (pdfOrders.get(pdfOrders.size() - 1).getPdfFile() != null) {
                            InputStream targetStream = new ByteArrayInputStream(
                                    pdfOrders.get(pdfOrders.size() - 1).getPdfFile());
                            pdfMerger.addSource(targetStream);
                            IfPdfFound = true;
                            ordersToUpdate.add(order);
                        }
                    }
                }

                ProcessedOrders processedOrder = new ProcessedOrders();
                if (IfPdfFound) {
                    try {
                        Long startOrderNumber = Long.valueOf(ordersToUpdate.get(0).getId());
                        Long endOrderNumber = Long.valueOf(ordersToUpdate.get(ordersToUpdate.size() - 1).getId());
                        processedOrder.setVersionId(UUID.randomUUID().toString());
                        processedOrder.setStartOrderNumber(startOrderNumber);
                        processedOrder.setEndOrderNumber(endOrderNumber);
                        processedOrder.setOrdersCounter(ordersToUpdate.size());

                        List<ShippingLabels> shippingLabelsEnvelops = new ArrayList<ShippingLabels>();
                        List<ShippingLabels> shippingLabelsPackages = new ArrayList<ShippingLabels>();
                        List<Long> erroredShippingEnvelopOrderNumbers = new ArrayList<Long>();
                        List<Long> erroredShippingPackageOrderNumbers = new ArrayList<Long>();
                        int count = 0;

                        for (FulfillmentOrder updateOrder : ordersToUpdate) {
                            count++;
                            System.out.println(count);
                            if (count == 10) {
                                count = 0;
                                Thread.sleep(6000);
                            }

                            Long currentOrderid = Long.valueOf(updateOrder.getId());

                            ShippingLabels shippingLabelsPackage = new ShippingLabels();
                            shippingLabelsPackage.setOrderId(currentOrderid);
                            try {
                                LabelPackageEnvelopeResponse labelPackageResponse = adminSharedService
                                        .postLabelPackageRequest(updateOrder);
                                String labelPackageDownload = labelPackageResponse.getLabel_download().getHref();
                                InputStream labelPackageStream = this.adminUtil.Base64ToStream(labelPackageDownload);
                                pdfMergerShipment.addSource(labelPackageStream);
                                adminSharedService.mapLabelResponseToShippinLabel(currentOrderid,
                                        labelPackageResponse, shippingLabelsPackage);
                                shippingLabelsPackages.add(shippingLabelsPackage);
                            } catch (HttpStatusCodeException exception) {
                                if (exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                    LOG.error("ERROR: ShipEngine Error Too Many Request : 1922 "
                                            + currentOrderid, exception);
                                    Thread.sleep(60000);
                                    try {
                                        LabelPackageEnvelopeResponse labelPackageResponse = adminSharedService
                                                .postLabelPackageRequest(updateOrder);
                                        String labelPackageDownload = labelPackageResponse.getLabel_download().getHref();
                                        InputStream labelPackageStream = this.adminUtil
                                                .Base64ToStream(labelPackageDownload);
                                        pdfMergerShipment.addSource(labelPackageStream);
                                        adminSharedService.mapLabelResponseToShippinLabel(currentOrderid,
                                                labelPackageResponse, shippingLabelsPackage);
                                        shippingLabelsPackages.add(shippingLabelsPackage);
                                    } catch (Exception e) {
                                        shippingLabelsPackage.setErrorMessage(exception.getResponseBodyAsString());
                                        shippingLabelsRepo.save(shippingLabelsPackage);
                                        erroredShippingPackageOrderNumbers.add(currentOrderid);
                                        LOG.error("ERROR: ShipEngine Error Too Many Request : 1935 "
                                                + currentOrderid, exception);
                                    }

                                } else {
                                    shippingLabelsPackage.setErrorMessage(exception.getResponseBodyAsString());
                                    shippingLabelsRepo.save(shippingLabelsPackage);
                                    erroredShippingPackageOrderNumbers.add(currentOrderid);
                                    LOG.error("ERROR: Shipping labelPackage error: mailRoomProcess(): 1943 "
                                            + currentOrderid + " " + exception.getResponseBodyAsString());
                                    System.out.println(adminSharedService.createLabelPackageRequestData(updateOrder));
                                }
                            } catch (Exception e) {
                                LOG.error("ERROR: While create the Batch: 1961 : OrderNum" + currentOrderid
                                        + " ", e);
                            }

                            ShippingLabels shippingLabelsEnvelop = new ShippingLabels();
                            shippingLabelsEnvelop.setOrderId(currentOrderid);
                            try {
                                LabelPackageEnvelopeResponse labelEnvelopeResponse = adminSharedService
                                        .postLabelEnvelopeRequest(updateOrder);
                                String labelPackageDownload = labelEnvelopeResponse.getLabel_download().getHref();
                                InputStream labelEnvelopStream = this.adminUtil.Base64ToStream(labelPackageDownload);
                                pdfMergerShipment.addSource(labelEnvelopStream);
                                adminSharedService.mapLabelResponseToShippinLabel(currentOrderid,
                                        labelEnvelopeResponse, shippingLabelsEnvelop);
                                shippingLabelsEnvelops.add(shippingLabelsEnvelop);
                            } catch (HttpStatusCodeException exception) {
                                if (exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                    LOG.error("ERROR: ShipEngine Error Too Many Request: 1957 : "
                                            + currentOrderid, exception);
                                    Thread.sleep(60000);
                                    try {
                                        LabelPackageEnvelopeResponse labelEnvelopeResponse = adminSharedService
                                                .postLabelEnvelopeRequest(updateOrder);
                                        String labelPackageDownload = labelEnvelopeResponse.getLabel_download()
                                                .getHref();
                                        InputStream labelEnvelopStream = this.adminUtil
                                                .Base64ToStream(labelPackageDownload);
                                        pdfMergerShipment.addSource(labelEnvelopStream);
                                        adminSharedService.mapLabelResponseToShippinLabel(currentOrderid,
                                                labelEnvelopeResponse, shippingLabelsEnvelop);
                                        shippingLabelsEnvelops.add(shippingLabelsEnvelop);
                                    } catch (Exception e) {
                                        LOG.error("ERROR: ShipEngine Error Too Many Request: 1968 : "
                                                + currentOrderid, exception);
                                        shippingLabelsEnvelop.setErrorMessage(exception.getResponseBodyAsString());
                                        shippingLabelsRepo.save(shippingLabelsEnvelop);
                                        erroredShippingEnvelopOrderNumbers.add(currentOrderid);
                                    }
                                } else {
                                    shippingLabelsEnvelop.setErrorMessage(exception.getResponseBodyAsString());
                                    shippingLabelsRepo.save(shippingLabelsEnvelop);
                                    erroredShippingEnvelopOrderNumbers.add(currentOrderid);
                                    LOG.error("ERROR: Shipping labelEnvelop error: mailRoomProcess() : 1976 "
                                            + currentOrderid + " " + exception.getResponseBodyAsString());
                                    System.out.println(adminSharedService.createLabelEnvelopeRequestData(updateOrder));
                                }
                            } catch (Exception e) {
                                LOG.error("ERROR: While create the Batch: 1961 : OrderNum" + currentOrderid
                                        + " ", e);
                            }
                        }

                        Boolean isNotEqual = ordersToUpdate.size() != shippingLabelsPackages.size() ? true
                                : ordersToUpdate.size() != shippingLabelsEnvelops.size() ? true : false;

                        HashMap<String, String> temp = new HashMap<String, String>();
                        temp.put("startOrderNumber", startOrderNumber.toString());
                        temp.put("endOrderNumber", endOrderNumber.toString());
                        temp.put("versionId", processedOrder.getVersionId());
                        versionsList.add(processedOrder.getVersionId());
                        result.add(temp);

                        pdfMerger.mergeDocuments(null);
                        String pdfFileName = uniqueFilename + TaxConstants.PDF_EXTENSION;
                        String uploadedPdfPath = this.uploadFileToAwsS3Bucket(
                                new File(this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION),
                                pdfFileName);
                        processedOrder.setPdfFile(uploadedPdfPath);
                        pdfMergerShipment.mergeDocuments(null);
                        String shipmentFileName = uniqueShipmentFileName + TaxConstants.PDF_EXTENSION;
                        String uploadedShippingLabelPath = this.uploadFileToAwsS3Bucket(
                                new File(this.mailroomFilePath + uniqueShipmentFileName + TaxConstants.PDF_EXTENSION),
                                shipmentFileName);
                        processedOrder.setShippingLabels(uploadedShippingLabelPath);

                        if (isNotEqual.equals(false)) {
                            processedOrdersRepo.save(processedOrder);
                        } else {
                            processedOrderCounter = (shippingLabelsEnvelops.size() < shippingLabelsPackages.size()
                                    ? shippingLabelsEnvelops.size()
                                    : shippingLabelsPackages.size());
                            processedOrder
                                    .setOrdersCounter((shippingLabelsEnvelops.size() < shippingLabelsPackages.size()
                                            ? shippingLabelsEnvelops.size()
                                            : shippingLabelsPackages.size()));
                            processedOrdersRepo.save(processedOrder);
                        }

                        for (ShippingLabels label : shippingLabelsEnvelops) {
                            if (isNotEqual.equals(true)) {
                                if (erroredShippingEnvelopOrderNumbers.indexOf(label.getOrderId()) > -1) {
                                    adminSharedService.setShipingLabelAsVoid(label);
                                } else {
                                    label.setProcessBatchId(processedOrder.getId());
                                }
                            } else {
                                label.setProcessBatchId(processedOrder.getId());
                            }
                        }

                        for (ShippingLabels label : shippingLabelsPackages) {
                            if (isNotEqual.equals(true)) {
                                if (erroredShippingPackageOrderNumbers.indexOf(label.getOrderId()) > -1) {
                                    adminSharedService.setShipingLabelAsVoid(label);
                                } else {
                                    label.setProcessBatchId(processedOrder.getId());
                                }
                            } else {
                                label.setProcessBatchId(processedOrder.getId());
                            }
                        }
                        shippingLabelsRepo.saveAll(shippingLabelsEnvelops);
                        shippingLabelsRepo.saveAll(shippingLabelsPackages);
                        this.adminUtil.deleteFiles(uniqueFilename);
                        this.adminUtil.deleteFiles(uniqueShipmentFileName);
                        allProcessedOrderCounter += isNotEqual ? processedOrderCounter : ordersToUpdate.size();
                    } catch (Exception e) {
                        LOG.error("ERROR: Error_Saving_Processed_Data: mailRoomProcess()", e);
                    }
                }
                forIndex++;
                Thread.sleep(10000);
            }

            String warningMessage = "";
            if (!allProcessedOrderCounter.equals(allOrders.size())) {
                warningMessage = "WARNING: " + Integer.toString(allProcessedOrderCounter) + " orders in batch and "
                        + Integer.toString(allOrders.size()) + " orders processing status.";
            }
            finalResult.put("processedOrders", result);
            finalResult.put("warningMessage", warningMessage);

            try {
                Boolean isFoundOrder = false;
                for (String versionId : versionsList) {
                    ProcessedOrders processedOrder = processedOrdersRepo.findByVersionId(versionId);
                    if (processedOrder != null) {
                        LOG.info("Creating new Batch Now!");
                        Batch batch = new Batch();
                        batch.setPdfFile(processedOrder.getPdfFile());
                        batch.setShippingLabels(processedOrder.getShippingLabels());
                        batch.setStatus("Ready");
                        batch.setStartOrderNumber(processedOrder.getStartOrderNumber());
                        batch.setEndOrderNumber(processedOrder.getEndOrderNumber());
                        batchRepo.save(batch);

                        processedOrder.setSentToMailroom(true);
                        processedOrdersRepo.save(processedOrder);
                        shippingLabelsRepo.updateMailRoomBatchId(batch.getId(), processedOrder.getId());
                        isFoundOrder = true;
                    }
                }
                if (isFoundOrder) {
                    LOG.info("isFoundOrder!");
                    List<MailRoomUser> roomUsers = (List<MailRoomUser>) mailRoomUserRepo.findAll();
                    for (MailRoomUser mailRoomUser : roomUsers) {
                        mailjetSender.sendEmailOfMailRoom(mailRoomUser);
                    }

                }
                finalResult.put("uploadedBatchFilesInMailRoom", Boolean.toString(isFoundOrder));

            } catch (Exception e) {
                LOG.error("ERROR: processed orders sending to mail room ", e);
            }
            return finalResult;
        } else {
            LOG.error("ERROR: No processing orders found - AdminController.java:1995");
        }
        throw new NotFoundException();
    }

    @PutMapping("/order/id/{orderNum}/physicalCopy")
    public void mailNewPhysicalCopy(HttpSession session, @PathVariable Long orderNum,
                                    @RequestBody OnHoldRequest request) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {

            OrderRecord existing = optRecord.get();
            existing.setTrackingNumber("");
            existing.setStatus(OrderRecord.STATUS_PROCESSING);
            existing.setShipengineStatusCode("");
            if (request.isSendNotification()) {
                mailjetSender.sendProcessingEmail(existing);
            }
            orderRecordRepo.save(existing);
            shippingLabelsRepo.deleteByOrderId(existing.getOrderNum());
            updateNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found: mailNewPhysicalCopy()");
            throw new NotFoundException();
        }
    }

    private String uploadFileToAwsS3Bucket(File file, String fileName) {
        // TODO: same lines of code as in the UploadFileToAwsServiceImpl#upload(...) - need to remove duplication
        Date nowDate = new Date();
        int year = 1900 + nowDate.getYear();
        int month = nowDate.getMonth() + 1;
        int dayOfMonth = nowDate.getDate();
        String yearString = String.valueOf(year);
        String monthString = month < 10 ? String.valueOf("0" + month) : String.valueOf(month);
        String dayString = dayOfMonth < 10 ? String.valueOf("0" + dayOfMonth) : String.valueOf(dayOfMonth);
        String path = yearString + "/" + monthString + "/" + dayString;
        String uploadUrl = path + "/" + fileName;
        try {
            AWSCredentials awsCred = new BasicAWSCredentials(
                    this.awsAccessKey,
                    this.awsSecretKey);
            AmazonS3 s3client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                    .withRegion(Regions.US_EAST_2)
                    .build();
            if (!s3client.doesBucketExist(this.awsBucketName)) {
                s3client.createBucket(this.awsBucketName);
            }
            if (s3client.doesBucketExist(this.awsBucketName)) {
                s3client.putObject(
                        this.awsBucketName,
                        uploadUrl,
                        file);
            }
        } catch (Exception e) {
            uploadUrl = "";
            System.out.println("------------ Start Logs -------------");
            System.out.println(e.getMessage());
            System.out.println("------------- End Logs ------------");
        }
        return uploadUrl;
    }

    @GetMapping("/productPrices")
    public @ResponseBody Iterable<ProductsList> productLists(HttpSession session) {
        // authService.requireRole(session, AuthRole.ADMIN);
        try {
            return productPriceRepo.findAll();
        } catch (Exception ex) {
            LOG.error("ERROR: Get Product Prices: productLists()", ex);
            throw new NotFoundException();
        }
    }

    @GetMapping("/productPrice")
    public @ResponseBody CurrentProductPrices getProduct(@RequestParam String product_name) {
        float price = -1.0f;
        float customEnrollmentFee = 0.0f;
        float enrollmentFee = 0.0f;
        float customChangeOfAddressFee = 0.0f;
        float customExpressOptionFee = 0.0f;
        float customPayrollFee = 0.0f;

        Gson gson = new Gson();
        List<ProductsList> products = productPriceRepo.findByProductName(product_name);
        Iterator<ProductsList> pit = products.iterator();
        while (pit.hasNext()) {

            ProductsList product = pit.next();
            if (product != null) {
                Calendar innerCal = Calendar.getInstance();
                Date innerCurrentDate = innerCal.getTime();
                Long current = innerCurrentDate.getTime();

                Date timePeriodsStart = product.getTimePeriodsStart();
                Date timePeriodsEnd = product.getTimePeriodsEnd();

                String timeframes = product.getAdditionalTimeframes();
                Type listType = new TypeToken<List<Map<String, Object>>>() {
                }.getType();
                List<Map<String, Object>> timeframeList = gson.fromJson(timeframes, listType);

                if (timePeriodsStart != null && timePeriodsEnd == null) {
                    if (current > timePeriodsStart.getTime()) {
                        price = product.getChangePrice();
                    } else {
                        price = product.getCurrentPrice();
                    }
                } else if (timePeriodsStart == null && timePeriodsEnd != null) {
                    if (current < timePeriodsEnd.getTime()) {
                        price = product.getChangePrice();
                    } else {
                        price = product.getCurrentPrice();
                    }
                } else if (timePeriodsStart != null && timePeriodsEnd != null) {
                    if (current < timePeriodsEnd.getTime() && current > timePeriodsStart.getTime()) {
                        price = product.getChangePrice();
                    } else {
                        price = product.getCurrentPrice();
                    }
                }

                if (timeframeList != null && timeframeList.size() > 0) {
                    Iterator<Map<String, Object>> it = timeframeList.iterator();
                    while (it.hasNext()) {
                        Map<String, Object> o = it.next();

                        Object startDate = o.get("startDate");
                        Object endDate = o.get("endDate");
                        Object price0 = o.get("price");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        try {
                            TimeZone srcZone = TimeZone.getTimeZone("UTC"), targetZone = TimeZone.getTimeZone("US/Eastern");
                            sdf.setTimeZone(srcZone);
                            Date d0 = (startDate != null) ? sdf.parse(startDate.toString()) : null;
                            Date d1 = (endDate != null) ? sdf.parse(endDate.toString()) : null;
                            Float f_price = (price0 != null) ? Float.valueOf(price0.toString()) : -1.0f;

                            if (d0 != null && d1 == null) {
                                if (current > d0.getTime()) {
                                    price = f_price;
                                    break;
                                }
                            } else if (d0 == null && d1 != null) {
                                if (current < d1.getTime()) {
                                    price = f_price;
                                    break;
                                }
                            } else if (d0 != null && d1 != null) {
                                if (current < d1.getTime() && current > d0.getTime()) {
                                    price = f_price;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (product.getIsPrimary()) {
                    if (Float.compare(price, -1f) != 0) {
                        customEnrollmentFee = price;
                        enrollmentFee = price;
                    }
                } else {
                    if ("change_of_address".equals(product.getSubOptions())) {
                        if (Float.compare(price, -1f) != 0) {
                            customChangeOfAddressFee = price;
                        }
                    } else if ("express".equals(product.getSubOptions())) {
                        if (Float.compare(price, -1f) != 0) {
                            customExpressOptionFee = price;
                        }
                    } else if ("payroll".equals(product.getSubOptions())) {
                        if (Float.compare(price, -1f) != 0) {
                            customPayrollFee = price;
                        }
                    }
                }
            }
        }

        CurrentProductPrices productPrices = CurrentProductPrices.builder()
                .customEnrollmentFee(customEnrollmentFee).enrollmentFee(enrollmentFee)
                .customChangeOfAddressFee(customChangeOfAddressFee).customExpressOptionFee(customExpressOptionFee)
                .customPayrollFee(customPayrollFee).build();
        return productPrices;
    }

    @PostMapping("/updatePrice")
    public void updatePrice(HttpSession session, @RequestBody ProductsList productList) {
        if (productList != null) {
            productPriceRepo.save(productList);
        }
    }

    @PostMapping("/updateAllPrice")
    public void updateAllPrice(HttpSession session, @RequestBody List<ProductsList> productList) {
        if (productList != null) {
            for (int i = 0; i < productList.size(); i++) {
                ProductsList product = productList.get(i);
                productPriceRepo.save(product);
            }
        }
    }

    @GetMapping("/salesList")
    public @ResponseBody Iterable<SalesList> salesList(HttpSession session) {
        // authService.requireRole(session, AuthRole.ADMIN);
        try {
            return salesListRepo.findAll();
        } catch (Exception ex) {
            LOG.error("ERROR: Get Sales List: salesList()", ex);
            throw new NotFoundException();
        }
    }

    @PostMapping("/updateAllSales")
    public void updateAllSales(HttpSession session, @RequestBody List<SalesList> saleSList) {
        if (saleSList != null) {
            for (int i = 0; i < saleSList.size(); i++) {
                SalesList item = saleSList.get(i);
                salesListRepo.save(item);
            }
        }
    }

    @GetMapping("/penalty-order/id/{orderNum}")
    public @ResponseBody PenaltyOrder getPenaltyOrderRecord(HttpSession session, @PathVariable Long orderNum) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        Optional<PenaltyOrder> opt = penaltyRecordRepo.findById(orderNum);
        if (opt.isPresent()) {
            PenaltyOrder penaltyOrder = opt.get();
            return penaltyOrder;
        } else
            return null;
    }

    @PutMapping("/penalty-order/id/{orderNum}/update")
    public PenaltyOrder savePenalty(HttpSession session, @PathVariable Long orderNum,
                                    @RequestBody OnSaveRequestPenalty request) {
        PenaltyOrder orderRecord = request.getOrderRecord();
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            PenaltyOrder existing = optRecord.get();

            existing.setFirstName(orderRecord.getFirstName());
            existing.setLastName(orderRecord.getLastName());
            existing.setBillingPhone(orderRecord.getBillingPhone());
            existing.setEmail(orderRecord.getEmail());
            existing.setSsn(orderRecord.getSsn());

            existing.setBillingAddress1(orderRecord.getBillingAddress1());
            existing.setBillingAddress2(orderRecord.getBillingAddress2());
            existing.setBillingCity(orderRecord.getBillingCity());
            existing.setBillingState(orderRecord.getBillingState());
            existing.setBillingPostalCode(orderRecord.getBillingPostalCode());

            existing.setShippingAddress1(orderRecord.getShippingAddress1());
            existing.setShippingAddress2(orderRecord.getShippingAddress2());
            existing.setShippingCity(orderRecord.getShippingCity());
            existing.setShippingState(orderRecord.getShippingState());
            existing.setShippingPostalCode(orderRecord.getShippingPostalCode());

            existing.setSpouseFirstname(orderRecord.getSpouseFirstname());
            existing.setSpouseLastname(orderRecord.getSpouseLastname());
            existing.setSpouseSsn(orderRecord.getSpouseSsn());

            existing.setIrsAddress1(orderRecord.getIrsAddress1());
            existing.setIrsAddress2(orderRecord.getIrsAddress2());
            existing.setIrsCity(orderRecord.getIrsCity());
            existing.setIrsState(orderRecord.getIrsState());
            existing.setIrsZipcode(orderRecord.getIrsZipcode());
            existing.setMarried(orderRecord.getMarried());

            existing.setPenaltyAmountWaived(orderRecord.getPenaltyAmountWaived());
            existing.setPenaltyWaivedType(orderRecord.getPenaltyWaivedType());
            existing.setPenaltyWaivedYear(orderRecord.getPenaltyWaivedYear());

            if (orderRecord.getTrackingNumber() != null && !orderRecord.getTrackingNumber().isEmpty()) {
                existing.setTrackingNumber(orderRecord.getTrackingNumber());
                existing.setTrackingNumberEntry(new Date());
                existing.setStatus(OrderRecord.STATUS_COMPLETE);
            } else if (orderRecord.getTrackingNumber() != null && orderRecord.getTrackingNumber().isEmpty()) {
                existing.setTrackingNumber(orderRecord.getTrackingNumber());
            }

            existing = AddressUtil.resolveIrsAddress(existing);

            penaltyRecordRepo.save(existing);

            updatePenaltyNote(orderNum, "SYSTEM", "EDITED", request.getUser());

            if (request.getContent().length() > 0) {
                updatePenaltyNote(orderNum, "MANUAL", request.getContent(), request.getUser());
            }
            if (request.getEmailChangeContent() != null && request.getEmailChangeContent().length() > 0) {
                updatePenaltyNote(orderNum, "SYSTEM", request.getEmailChangeContent(), request.getUser());
            }
            if (request.getTrackingNumberContent() != null && request.getTrackingNumberContent().length() > 0) {
                updatePenaltyNote(orderNum, "SYSTEM", request.getTrackingNumberContent(), request.getUser());
            }
            return existing;
        } else {
            LOG.error("ERROR: Not Found Order: savePenalty(): 940");
            throw new NotFoundException();
        }
    }

    @PutMapping("/penalty-order/id/{orderNum}/cancel")
    public void cancelPenaltyOrderRecord(HttpSession session, @PathVariable Long orderNum,
                                         @RequestBody OnHoldRequest request) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            try {
                PenaltyOrder existing = optRecord.get();
                existing.setStatus(OrderRecord.STATUS_CANCELLED);
                penaltyRecordRepo.save(existing);

                //mailjetSender.sendPenaltyOrderCancelledEmail(existing);
            } catch (Exception e) {
                LOG.error("ERROR: Saving Cancelled Order " + orderNum + ": Line 817");
            }
            updatePenaltyNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found Order:" + orderNum + " cancelOrderRecord(): 810");
            throw new NotFoundException();
        }
    }

    @PutMapping("/penalty-order/id/{orderNum}/onhold")
    public void holdByPenaltyOrderNum(HttpSession session, @PathVariable Long orderNum, @RequestBody OnHoldRequest request)
            throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            PenaltyOrder existing = optRecord.get();

            if (OrderRecord.STATUS_ON_HOLD.equals(existing.getStatus())) {
                existing.setStatus(OrderRecord.STATUS_PROCESSING);

                if (request.isSendNotification()) {
                    mailjetSender.sendPenaltyOrderProcessingEmail(existing);
                }
            } else {
                existing.setStatus(OrderRecord.STATUS_ON_HOLD);

                if (request.isSendNotification()) {
                    mailjetSender.sendPenaltyOrderOnholdEmail(existing);
                }
            }

            penaltyRecordRepo.save(existing);
            updatePenaltyNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found Order: holdByOrderNum(): 810");
            throw new NotFoundException();
        }
    }

    @PutMapping("/penalty-order/id/{orderNum}/chargeback")
    public void chargeBackPenaltyOrderRecord(HttpSession session, @PathVariable Long orderNum,
                                             @RequestBody OnHoldRequest request) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            PenaltyOrder existing = optRecord.get();
            existing.setStatus(OrderRecord.STATUS_CHARGE_BACK);
            existing.setCbtype(request.getCbtype());
            penaltyRecordRepo.save(existing);
            updatePenaltyNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found Order: chargeBackOrderRecord(): 810");
            throw new NotFoundException();
        }
    }

    @PutMapping("/penalty-order/id/{orderNum}/physicalCopy")
    public void mailNewPhysicalCopyPenalty(HttpSession session, @PathVariable Long orderNum,
                                           @RequestBody OnHoldRequest request) throws IOException {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {

            PenaltyOrder existing = optRecord.get();
            existing.setTrackingNumber("");
            existing.setStatus(OrderRecord.STATUS_PROCESSING);
            //existing.setShipengineStatusCode("");
            if (request.isSendNotification()) {
                mailjetSender.sendPenaltyOrderProcessingEmail(existing);
            }
            penaltyRecordRepo.save(existing);

            //shippingLabelsRepo.deleteByOrderId(existing.getOrderNum());
            updatePenaltyNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found: mailNewPhysicalCopy()");
            throw new NotFoundException();
        }
    }


    @GetMapping("/ein/id/{orderNum}")
    public @ResponseBody
    EinOrder getEinOrderRecord(HttpSession session, @PathVariable Long orderNum) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        Optional<EinOrder> opt = einRecordRepo.findById(orderNum);
        if (opt.isPresent()) {
            EinOrder einOrder = opt.get();
            return einOrder;
        } else
            return null;
    }

    @PutMapping("/ein/id/{orderNum}/update")
    public EinOrder saveEin(HttpSession session, @PathVariable Long orderNum,
                            @RequestBody OnSaveRequestEin request) {
        EinOrder orderRecord = request.getOrderRecord();
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        Optional<EinOrder> optRecord = einRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            EinOrder existing = optRecord.get();

            existing.setEmail(orderRecord.getEmail());
            existing.setFirst_name(orderRecord.getFirst_name());
            existing.setLast_name(orderRecord.getLast_name());
            existing.setTitle(orderRecord.getTitle());
            existing.setIs_sec645(orderRecord.getIs_sec645());
            existing.setMiddle_name(orderRecord.getMiddle_name());
            existing.setNo_middle_name(orderRecord.getNo_middle_name());
            existing.setSuffix(orderRecord.getSuffix());
            existing.setSsn(orderRecord.getSsn());
            existing.setPhone_number(orderRecord.getPhone_number());

            existing.setSecondary_first_name(orderRecord.getSecondary_first_name());
            existing.setSecondary_middle_name(orderRecord.getSecondary_middle_name());
            existing.setSecondary_no_middle_name(orderRecord.getSecondary_no_middle_name());
            existing.setSecondary_last_name(orderRecord.getSecondary_last_name());
            existing.setSecondary_suffix(orderRecord.getSecondary_suffix());
            existing.setSecondary_ssn(orderRecord.getSecondary_ssn());

            existing.setMailing_address(orderRecord.getMailing_address());
            existing.setMailing_apt_suite(orderRecord.getMailing_apt_suite());
            existing.setMailing_city(orderRecord.getMailing_city());
            existing.setMailing_state(orderRecord.getMailing_state());
            existing.setMailing_zip_code(orderRecord.getMailing_zip_code());

            existing.setAddress(orderRecord.getAddress());
            existing.setApt_suite(orderRecord.getApt_suite());
            existing.setCity(orderRecord.getCity());
            existing.setState(orderRecord.getState());
            existing.setCounty(orderRecord.getCounty());
            existing.setZip_code(orderRecord.getZip_code());

            existing.setOrder_type(orderRecord.getOrder_type());
            existing.setSub_type(orderRecord.getSub_type());
            existing.setReason(orderRecord.getReason());
            existing.setBusiness_type(orderRecord.getBusiness_type());
            existing.setBusiness_sub_type(orderRecord.getBusiness_sub_type());
            existing.setBusiness_sub_type_2(orderRecord.getBusiness_sub_type_2());
            existing.setBusiness_sub_type_3(orderRecord.getBusiness_sub_type_3());
            existing.setBusiness_sub_type_4(orderRecord.getBusiness_sub_type_4());
            existing.setBusiness_details(orderRecord.getBusiness_details());
            existing.setLegal_name(orderRecord.getLegal_name());
            existing.setLlc_number_members(orderRecord.getLlc_number_members());
            existing.setState_incorporated(orderRecord.getState_incorporated());
            existing.setStart_date_year(orderRecord.getStart_date_year());
            existing.setStart_date_month(orderRecord.getStart_date_month());
            existing.setIs_w2_employees(orderRecord.getIs_w2_employees());
            existing.setDate_first_wages_year(orderRecord.getDate_first_wages_year());
            existing.setDate_first_wages_month(orderRecord.getDate_first_wages_month());

            existing.setPrevious_ein(orderRecord.getPrevious_ein());
            existing.setIs_previous_ein(orderRecord.getIs_previous_ein());
            einRecordRepo.save(existing);

            updateEinNote(orderNum, "SYSTEM", "EDITED", request.getUser());

            if (request.getContent().length() > 0) {
                updateEinNote(orderNum, "MANUAL", request.getContent(), request.getUser());
            }
            if (request.getEmailChangeContent() != null && request.getEmailChangeContent().length() > 0) {
                updateEinNote(orderNum, "SYSTEM", request.getEmailChangeContent(), request.getUser());
            }
            return existing;
        } else {
            LOG.error("ERROR: Not Found Order: saveEin(): 940");
            throw new NotFoundException();
        }
    }

    @PutMapping("/ein/id/{orderNum}/cancel")
    public void cancelEinOrderRecord(HttpSession session, @PathVariable Long orderNum,
                                     @RequestBody OnHoldRequest request) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<EinOrder> optRecord = einRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            try {
                EinOrder existing = optRecord.get();
                existing.setStatus(OrderRecord.STATUS_CANCELLED);
                einRecordRepo.save(existing);

                mailjetSender.sendEinOrderCancelledEmail(existing);
            } catch (Exception e) {
                LOG.error("ERROR: Saving Cancelled Order " + orderNum + ": Line 817");
            }
            updateEinNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found Order:" + orderNum + " cancelOrderRecord(): 810");
            throw new NotFoundException();
        }
    }

    @PutMapping("/ein/id/{orderNum}/docusign-email")
    public void resendDocuSignEmail(HttpSession session, @PathVariable Long orderNum, @RequestBody OnHoldRequest request) throws IOException, ApiException {
        List<AuthRole> roles = Arrays.asList(AuthRole.ADMIN, AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        Optional<EinOrder> optRecord = einRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            EinOrder existing = optRecord.get();

            existing.setStatus(OrderRecord.STATUS_AWAITING_SIGNATURE_SERVICE);
            mailjetSender.sendEinAwaitingSignatureServiceEmail(existing);

            einRecordRepo.save(existing);
            updateEinNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found Order: resendDocuSignEmail(): 810");
            throw new NotFoundException();
        }
    }

    @PutMapping("/ein/id/{orderNum}/chargeback")
    public void chargeBackEinOrderRecord(HttpSession session, @PathVariable Long orderNum,
                                         @RequestBody OnHoldRequest request) {
        List<AuthRole> roles = new ArrayList<AuthRole>();
        roles.add(AuthRole.ADMIN);
        roles.add(AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Optional<EinOrder> optRecord = einRecordRepo.findById(orderNum);
        if (optRecord.isPresent()) {
            EinOrder existing = optRecord.get();
            existing.setStatus(OrderRecord.STATUS_CHARGE_BACK);
            existing.setCbtype(request.getCbtype());
            einRecordRepo.save(existing);

            updateEinNote(request.getOrderNum(), request.getType(), request.getContent(), request.getUser());
        } else {
            LOG.error("ERROR: Not Found Order: chargeBackOrderRecord(): 810");
            throw new NotFoundException();
        }
    }

}
