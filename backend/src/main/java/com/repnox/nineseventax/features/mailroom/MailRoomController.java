package com.repnox.nineseventax.features.mailroom;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.opencsv.CSVReaderHeaderAware;
import com.repnox.nineseventax.exceptions.BadRequestException;
import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.admin.UploadResult;
import com.repnox.nineseventax.features.auth.AuthService;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.mailroom.model.Batch;
import com.repnox.nineseventax.features.mailroom.model.BatchRepo;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabels;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabelsRepo;
import com.repnox.nineseventax.features.utils.AdminUtil;
import com.repnox.nineseventax.features.utils.TaxConstants;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@RequestMapping("/api/mailroom")
public class MailRoomController {
    private static final Logger LOG = LoggerFactory.getLogger(MailRoomController.class);

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private PenaltyRecordRepo penaltyRecordRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private BatchRepo batchRepo;

    @Autowired
    private MailjetSender mailjetSender;

    @Value("${mailroom.upload.path}")
    private String uploadPath;

    @Value("${aws.bucket.name}")
    private String awsBucketName;

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Autowired
    private ShippingLabelsRepo shippingLabelsRepo;

    public static final String MAILROOM_STATUS_MAILED = "Mailed";

    public static final String MAILROOM_STATUS_READY = "Ready";

    @GetMapping("/mailRoomBatches")
    public @ResponseBody ArrayList<Batch> getMailRoomBatches(HttpSession session) throws IOException {
        authService.requireAdminOrMailRoomUserRole(session);
        List<Object[]> batches = batchRepo.findAllBatch();
        return AdminUtil.convertBatch(batches);
    }

    private void setOrdersAsCompletedUsingBatchId(Long batchId) {
        ArrayList<ShippingLabels> shippingLabels = shippingLabelsRepo.findByMailroomBatchId(batchId);
        for (ShippingLabels label : shippingLabels) {
            if (label.getTrackingNumber() != null && label.getTrackingNumber().length() > 0
                    && label.getOrderId() != null) {
                Optional<OrderRecord> optRecord = orderRecordRepo.findById(label.getOrderId());
                Optional<PenaltyOrder> optPenaltyRecord = penaltyRecordRepo.findById(label.getOrderId());

                if (optRecord.isPresent()) {
                    OrderRecord existing = optRecord.get();
                    String orderStatus = existing.getStatus() != null ? existing.getStatus() : "";
                    String trackingNumber = existing.getTrackingNumber() != null ? existing.getTrackingNumber() : "";
                    if (!orderStatus.equals(OrderRecord.STATUS_COMPLETE) || trackingNumber.length() < 1) {
                        String processingsSpeed = existing.getProcessingSpeed() != null ? existing.getProcessingSpeed()
                                : "";
                        if (processingsSpeed.equals("Deluxe")) {
                            if (label.getServiceCode().equals("usps_priority_mail")) {
                                existing.setTrackingNumber(label.getTrackingNumber());
                                existing.setShipengineStatusCode(label.getTrackingStatus());
                                existing.setStatus(OrderRecord.STATUS_COMPLETE);
                                existing.setTrackingNumberEntry(new Date());
                                orderRecordRepo.save(existing);
                                try {
                                    mailjetSender.sendCompleteEmail(existing);
                                } catch (IOException e) {
                                    LOG.error("ERROR: Send Completed Email: 93", e);
                                }
                            }
                        } else {
                            existing.setTrackingNumber(label.getTrackingNumber());
                            existing.setShipengineStatusCode(label.getTrackingStatus());
                            existing.setStatus(OrderRecord.STATUS_COMPLETE);
                            existing.setTrackingNumberEntry(new Date());
                            orderRecordRepo.save(existing);
                            try {
                                mailjetSender.sendCompleteEmail(existing);
                            } catch (IOException e) {
                                LOG.error("ERROR: Send Completed Email: 105", e);
                            }
                        }
                    }
                } else if(optPenaltyRecord.isPresent()) {
                    PenaltyOrder existing = optPenaltyRecord.get();
                    String orderStatus = existing.getStatus() != null ? existing.getStatus() : "";
                    String trackingNumber = existing.getTrackingNumber() != null ? existing.getTrackingNumber() : "";
                    if (!orderStatus.equals(OrderRecord.STATUS_COMPLETE) || trackingNumber.length() < 1) {
                        String processingsSpeed = existing.getProcessingSpeed() != null ? existing.getProcessingSpeed()
                                : "";
                        if (processingsSpeed.equals("Deluxe")) {
                            if (label.getServiceCode().equals("usps_priority_mail")) {
                                existing.setTrackingNumber(label.getTrackingNumber());
                                existing.setShipengineStatusCode(label.getTrackingStatus());
                                existing.setStatus(OrderRecord.STATUS_COMPLETE);
                                existing.setTrackingNumberEntry(new Date());
                                penaltyRecordRepo.save(existing);
                                mailjetSender.sendPenaltyOrderCompleteEmail(existing);
                            }
                        } else {
                            existing.setTrackingNumber(label.getTrackingNumber());
                            existing.setShipengineStatusCode(label.getTrackingStatus());
                            existing.setStatus(OrderRecord.STATUS_COMPLETE);
                            existing.setTrackingNumberEntry(new Date());
                            penaltyRecordRepo.save(existing);
                            mailjetSender.sendPenaltyOrderCompleteEmail(existing);
                        }
                    }
                }
            }
        }
    }

    @GetMapping("/downloadPDF/{batchId}")
    public ResponseEntity<InputStreamResource> downloadPDF(HttpServletRequest request, HttpSession session,
            @PathVariable Long batchId) throws IOException {
        authService.requireAdminOrMailRoomUserRole(session);

        Optional<Batch> optionalBatch = batchRepo.findById(batchId);
        if (optionalBatch.isPresent()) {
            try {
                Batch currentBatch = optionalBatch.get();
                String filePath = currentBatch.getPdfFile();
                currentBatch.setIsPdfDownload(true);
                if (currentBatch.getIsLabelDownload()) {
                    currentBatch.setStatus(MAILROOM_STATUS_MAILED);
                    this.setOrdersAsCompletedUsingBatchId(batchId);
                }
                batchRepo.save(currentBatch);
                AWSCredentials awsCred = new BasicAWSCredentials(
                        this.awsAccessKey,
                        this.awsSecretKey);
                AmazonS3 s3client = AmazonS3ClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                        .withRegion(Regions.US_EAST_2)
                        .build();
                S3Object object = s3client.getObject(this.awsBucketName, filePath);
                S3ObjectInputStream objectContent = object.getObjectContent();
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + TaxConstants.PORT_POLIO_PDF)
                        .body(new InputStreamResource(objectContent));
            } catch (Exception e) {
                LOG.error("ERROR: Occurend downloading PDF", e);
                return null;
            }
        } else {
            LOG.error("ERROR: Not Founding Batch: downloadPDF()");
            throw new NotFoundException();
        }
    }

    @GetMapping("/downloadLabels/{batchId}")
    public ResponseEntity<InputStreamResource> downloadShipingLabels(HttpServletRequest request, HttpSession session,
            @PathVariable Long batchId) throws IOException {
        authService.requireAdminOrMailRoomUserRole(session);

        Optional<Batch> optionalBatch = batchRepo.findById(batchId);
        if (optionalBatch.isPresent()) {
            try {
                Batch currentBatch = optionalBatch.get();
                currentBatch.setIsLabelDownload(true);
                if (currentBatch.getIsPdfDownload()) {
                    currentBatch.setStatus(MAILROOM_STATUS_MAILED);
                    this.setOrdersAsCompletedUsingBatchId(batchId);
                }
                batchRepo.save(currentBatch);
                AWSCredentials awsCred = new BasicAWSCredentials(
                        this.awsAccessKey,
                        this.awsSecretKey);
                AmazonS3 s3client = AmazonS3ClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                        .withRegion(Regions.US_EAST_2)
                        .build();
                S3Object object = s3client.getObject(this.awsBucketName, currentBatch.getShippingLabels());
                S3ObjectInputStream objectContent = object.getObjectContent();
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment;filename=" + TaxConstants.CURRENT_ORDERS_LABELS_PDF)
                        .body(new InputStreamResource(objectContent));
            } catch (Exception e) {
                LOG.error("ERROR: Occured downloading labels", e);
                return null;
            }
        } else {
            LOG.error("ERROR: Not Founding Batch: downloadShipingLabels()");
            throw new NotFoundException();
        }
    }

    @PostMapping("/upload_tracking/{batchId}")
    public @ResponseBody List<UploadResult> uploadTrackingNumbers(HttpSession session,
            @RequestParam("file") MultipartFile file, @PathVariable Long batchId) {
        authService.requireAdminOrMailRoomUserRole(session);

        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new InputStreamReader(file.getInputStream()))) {
            List<UploadResult> results = new ArrayList<>();
            List<String[]> records = reader.readAll();
            for (String[] next : records) {
                UploadResult result = new UploadResult();
                try {
                    Long orderNum;
                    try {
                        orderNum = Long.parseLong(next[18].replaceAll("\\D+", ""));
                        System.out.println(orderNum);
                    } catch (Exception e) {
                        LOG.error("ERROR: Parsing OrderNimber: downloadPDF()");
                        continue;
                    }

                    result.setOrderNum(orderNum);
                    Optional<OrderRecord> optionalRecord = orderRecordRepo.findById(orderNum);
                    Optional<PenaltyOrder> optionalPenaltyOrder = penaltyRecordRepo.findById(orderNum);

                    if (optionalRecord.isPresent()) {
                        OrderRecord record = optionalRecord.get();

                        if (!OrderRecord.STATUS_PROCESSING.equals(record.getStatus())
                                && !authService.isAdminRole(session)) {
                            result.setMessage(
                                    "Warning - Order was not in Pending status, it was: " + record.getStatus());
                        } else {
                            if (OrderRecord.STATUS_PROCESSING.equals(record.getStatus())) {
                                result.setMessage("Success");
                            } else {
                                result.setMessage(
                                        "Warning - Order was not in Pending status, it was: " + record.getStatus());
                            }

                            if (authService.isAdminRole(session) || StringUtils.isBlank(record.getTrackingNumber())) {
                                record.setTrackingNumber(next[7].replaceAll("\\D+", ""));
                                record.setTrackingNumberEntry(new Date());
                                record.setStatus(OrderRecord.STATUS_COMPLETE);
                            }
                        }

                        orderRecordRepo.save(record);
                        mailjetSender.sendCompleteEmail(record);
                    } else if(optionalPenaltyOrder.isPresent()) {
                        PenaltyOrder record = optionalPenaltyOrder.get();

                        if (!OrderRecord.STATUS_PROCESSING.equals(record.getStatus())
                                && !authService.isAdminRole(session)) {
                            result.setMessage(
                                    "Warning - Penalty Order was not in Pending status, it was: " + record.getStatus());
                        } else {
                            if (OrderRecord.STATUS_PROCESSING.equals(record.getStatus())) {
                                result.setMessage("Success");
                            } else {
                                result.setMessage(
                                        "Warning - Penalty Order was not in Pending status, it was: " + record.getStatus());
                            }

                            if (authService.isAdminRole(session) || StringUtils.isBlank(record.getTrackingNumber())) {
                                record.setTrackingNumber(next[7].replaceAll("\\D+", ""));
                                record.setTrackingNumberEntry(new Date());
                                record.setStatus(OrderRecord.STATUS_COMPLETE);
                            }
                        }

                        penaltyRecordRepo.save(record);
                        mailjetSender.sendPenaltyOrderCompleteEmail(record);
                    } else {
                        result.setMessage("Error - Order number did not exist in database");
                    }

                    results.add(result);
                } catch (Exception e) {
                    LOG.error("ERROR: Unable to process: Mailcontroller.java: 218", e);
                    result.setMessage(
                            "Error - unable to process row. Please contact the administrator and check the logs.");
                }
            }

            Optional<Batch> optionalBatch = batchRepo.findById(batchId);
            if (optionalBatch.isPresent()) {
                Batch batch = optionalBatch.get();
                batch.setStatus("Mailed");
                batchRepo.save(batch);
            }

            return results;
        } catch (IOException e) {
            LOG.error("ERROR: Mailcontroller.java: 233", e);
            throw new BadRequestException();
        }
    }
}
