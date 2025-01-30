package com.repnox.nineseventax.features.order;

import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.admin.AdminSharedService;
import com.repnox.nineseventax.features.aws.interfaces.UploadFileToAwsService;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalFetchTransactionService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalRefundService;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.models.LabelPackageEnvelopeResponse;
import com.repnox.nineseventax.features.order.interfaces.AddressService;
import com.repnox.nineseventax.features.order.interfaces.OrderProcessService;
import com.repnox.nineseventax.features.order.interfaces.RefundStaleOrderService;
import com.repnox.nineseventax.features.order.interfaces.VoidOrderService;
import com.repnox.nineseventax.features.order.repo.FulfillmentOrder;
import com.repnox.nineseventax.features.order.repo.FulfillmentRepo;
import com.repnox.nineseventax.features.pdforderversions.PdfOrderVersions;
import com.repnox.nineseventax.features.pdforderversions.PdfOrderVersionsRepo;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import com.repnox.nineseventax.features.processedorders.ProcessedOrders;
import com.repnox.nineseventax.features.processedorders.ProcessedOrdersRepo;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabels;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabelsRepo;
import com.repnox.nineseventax.features.utils.AdminUtil;
import com.repnox.nineseventax.features.utils.TaxConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

@Service
@Slf4j
public class OrderProcessServiceImpl implements OrderProcessService {

    @Value("${orbital.api.version}")
    private String version;

    @Value("${orbital.bin}")
    private String bin;

    @Value("${orbital.terminalId}")
    private String terminalId;

    @Value("${mailroom.upload.path}")
    private String mailroomFilePath;

    @Value("${shipengine_rate_limit}")
    private String shipEngineRateLimit;

    @Autowired
    private ProcessedOrdersRepo processedOrdersRepo;
    @Autowired
    private ShippingLabelsRepo shippingLabelsRepo;
    @Autowired
    private AdminSharedService adminSharedService;
    @Autowired
    private OrderRecordRepo orderRecordRepo;
    @Autowired
    private PenaltyRecordRepo penaltyRecordRepo;
    @Autowired
    private FulfillmentRepo fulfillmentRepo;
    @Autowired
    private OrbitalFetchTransactionService fetchTransactionService;
    @Autowired
    private OrbitalRefundService refundService;
    @Autowired
    private UploadFileToAwsService uploadFileToAwsService;
    @Autowired
    private AdminUtil adminUtil;
    @Autowired
    private MailjetSender mailjetSender;
    @Autowired
    private PdfOrderVersionsRepo pdfVersionRepo;
    @Autowired
    private AddressService addressService;
    @Autowired
    private RefundStaleOrderService refundStaleOrderService;
    @Autowired
    private VoidOrderService voidOrderService;

    @Override
    @Transactional
    public HashMap<String, Object> process() throws Exception{
        String warningMessage = "";

        log.info("Test values (SE_LIMIT)= " + this.bin + ", " + this.version + ", " + this.mailroomFilePath + ", " + this.shipEngineRateLimit);

        HashMap<String, Object> result4Orders = processOrders();

        HashMap<String, Object> result = new HashMap<>();

        Integer totalProcessed = result4Orders != null ? Integer.valueOf(result4Orders.get("allProcessedOrderCounter").toString()) : 0;
        Integer total = result4Orders != null ? Integer.valueOf(result4Orders.get("allOrderSize").toString()) : 0;

        if (!totalProcessed.equals(total)) {
            warningMessage = "WARNING: " + totalProcessed + " orders in batch and "
                    + total + " orders processing status.";
        }

        result.put("processedOrders", result4Orders.get("processedOrders"));
        result.put("allProcessedOrderCounter", totalProcessed);
        result.put("allOrderSize", total);
        result.put("warningMessage", warningMessage);

        return result;
    }

    private HashMap<String, Object> processOrders() throws Exception {
        log.debug("Running OrderProcessServiceImpl->process");
        //void unsent orders (if any).
        voidOrderService.voidUnsentProcessOrders();

        // refund stale orders (if any).
//        try {
//            refundStaleOrderService.refund();
//        } catch(Exception e) {
//            log.error(e.getMessage());
//        }

        //check address
        addressService.validateAddress();

        Integer processedOrderCounter = 0;
        Integer allProcessedOrderCounter = 0;
        // List<OrderRecord> allOrders = orderRecordRepo.findOrderInProcessingAndNotShipengine();
        List<FulfillmentOrder> allOrders = fulfillmentRepo.findOrderInProcessingAndNotShipengine();
        if (CollectionUtils.isNotEmpty(allOrders)) {
            List<List<FulfillmentOrder>> orderChunks = ListUtils.partition(allOrders, Integer.valueOf(shipEngineRateLimit));
            HashMap<String, Object> finalResult = new HashMap<>();
            List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
            Integer forIndex = 0;
            for (List<FulfillmentOrder> orders : orderChunks) {
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
                        int maxLimit = Integer.valueOf(shipEngineRateLimit) / 2;
                        int totalOrderProcess = 0;
                        for (FulfillmentOrder updateOrder : ordersToUpdate) {
                            ++totalOrderProcess;
                            log.info("Start process order {}", totalOrderProcess);
                            count++;
                            if (count == maxLimit) {
                                count = 0;
                                Thread.sleep(70000);
                            }

                            ShippingLabels shippingLabelsPackage = new ShippingLabels();
                            shippingLabelsPackage.setOrderId(Long.valueOf(updateOrder.getId()));
                            try {
                                LabelPackageEnvelopeResponse labelPackageResponse = adminSharedService
                                        .postLabelPackageRequest(updateOrder);
                                String labelPackageDownload = labelPackageResponse.getLabel_download().getHref();
                                InputStream labelPackageStream = this.adminUtil
                                        .Base64ToStream(labelPackageDownload);
                                pdfMergerShipment.addSource(labelPackageStream);
                                adminSharedService.mapLabelResponseToShippinLabel(Long.valueOf(updateOrder.getId()),
                                        labelPackageResponse, shippingLabelsPackage);
                                shippingLabelsPackages.add(shippingLabelsPackage);
                            } catch (HttpStatusCodeException exception) {
                                if (exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                    log.error("ERROR: ShipEngine Error Too Many Request : 413 " + updateOrder.getId(), exception.getMessage());
                                    Thread.sleep(70000);
                                    try {
                                        LabelPackageEnvelopeResponse labelPackageResponse = adminSharedService
                                                .postLabelPackageRequest(updateOrder);
                                        String labelPackageDownload = labelPackageResponse.getLabel_download()
                                                .getHref();
                                        InputStream labelPackageStream = this.adminUtil
                                                .Base64ToStream(labelPackageDownload);
                                        pdfMergerShipment.addSource(labelPackageStream);
                                        adminSharedService.mapLabelResponseToShippinLabel(Long.valueOf(updateOrder.getId()),
                                                labelPackageResponse, shippingLabelsPackage);
                                        shippingLabelsPackages.add(shippingLabelsPackage);
                                    } catch (Exception e) {
                                        log.error("ERROR: ShipEngine Error Too Many Request : 423 " + updateOrder.getId(), e.getMessage());
                                    }
                                } else {
                                    log.error("ERROR: ShipEngine Error when the pacakge is created: 426" + updateOrder.getId(), exception);
                                    System.out
                                            .println(adminSharedService.createLabelPackageRequestData(updateOrder));
                                    erroredShippingPackageOrderNumbers.add(Long.valueOf(updateOrder.getId()));
                                    shippingLabelsPackage.setErrorMessage(exception.getResponseBodyAsString());
                                    shippingLabelsRepo.save(shippingLabelsPackage);
                                }
                            } catch (Exception e) {
                                log.error("ERROR: While create the Batch: 433 : OrderNum" + updateOrder.getId() + " ", e);
                            }

                            ShippingLabels shippingLabelsEnvelop = new ShippingLabels();
                            shippingLabelsEnvelop.setOrderId(Long.valueOf(updateOrder.getId()));
                            try {
                                LabelPackageEnvelopeResponse labelEnvelopeResponse = adminSharedService
                                        .postLabelEnvelopeRequest(updateOrder);
                                String labelPackageDownload = labelEnvelopeResponse.getLabel_download().getHref();
                                InputStream labelEnvelopStream = this.adminUtil
                                        .Base64ToStream(labelPackageDownload);
                                pdfMergerShipment.addSource(labelEnvelopStream);
                                adminSharedService.mapLabelResponseToShippinLabel(Long.valueOf(updateOrder.getId()),
                                        labelEnvelopeResponse, shippingLabelsEnvelop);
                                shippingLabelsEnvelops.add(shippingLabelsEnvelop);
                            } catch (HttpStatusCodeException exception) {
                                if (exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                    log.error("ERROR: ShipEngine Error Too Many Request: 447 : "
                                            + updateOrder.getId(), exception.getMessage());
                                    Thread.sleep(70000);
                                    try {
                                        LabelPackageEnvelopeResponse labelEnvelopeResponse = adminSharedService
                                                .postLabelEnvelopeRequest(updateOrder);
                                        String labelPackageDownload = labelEnvelopeResponse.getLabel_download()
                                                .getHref();
                                        InputStream labelEnvelopStream = this.adminUtil
                                                .Base64ToStream(labelPackageDownload);
                                        pdfMergerShipment.addSource(labelEnvelopStream);
                                        adminSharedService.mapLabelResponseToShippinLabel(Long.valueOf(updateOrder.getId()),
                                                labelEnvelopeResponse, shippingLabelsEnvelop);
                                        shippingLabelsEnvelops.add(shippingLabelsEnvelop);
                                    } catch (Exception e) {
                                        log.error("ERROR: ShipEngine Error Too Many Request : 440 "
                                                + updateOrder.getId(), e.getMessage());
                                    }
                                } else {
                                    log.error("ERROR: ShipEngine Error when the Envelop is created: 405"
                                            + updateOrder.getId(), exception);
                                    System.out.println(
                                            adminSharedService.createLabelEnvelopeRequestData(updateOrder));
                                    erroredShippingEnvelopOrderNumbers.add(Long.valueOf(updateOrder.getId()));
                                    shippingLabelsEnvelop.setErrorMessage(exception.getResponseBodyAsString());
                                    shippingLabelsRepo.save(shippingLabelsEnvelop);
                                }
                            } catch (Exception e) {
                                log.error("ERROR: While create the Batch: 467 : OrderNum"
                                        + updateOrder.getId() + " ", e);
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
                        String uploadedPdfPath = uploadFileToAwsService.upload(
                                new File(this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION),
                                pdfFileName);
                        processedOrder.setPdfFile(uploadedPdfPath);

                        // processedOrder.setPdfFile(this.adminUtil.fileToByte(this.mailroomFilePath +
                        // uniqueFilename + TaxConstants.PDF_EXTENSION));
                        pdfMergerShipment.mergeDocuments(null);
                        String shipmentFileName = uniqueShipmentFileName + TaxConstants.PDF_EXTENSION;
                        String uploadedShippingLabelPath = uploadFileToAwsService.upload(new File(
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
                        log.error("ERROR: While create the Batch: 462", e);
                    }
                }
                // }

                Thread.sleep(70000);
                forIndex++;
            }

            finalResult.put("processedOrders", result);
            finalResult.put("allProcessedOrderCounter", allProcessedOrderCounter);
            finalResult.put("allOrderSize", allOrders.size());
            return finalResult;
        } else {
            log.error("ERRROR: Not found processed Orders: 476");
        }

        return null;
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
            log.error("ERROR: Errors updating Notes Order Number => " + orderNum);
            throw new NotFoundException();
        }
    }

}
