package com.repnox.nineseventax.features.processedorders;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.admin.AdminSharedService;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.order.repo.FulfillmentRepo;
import com.repnox.nineseventax.features.penalty.PenaltyOrderRepo;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabels;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabelsRepo;
import com.repnox.nineseventax.features.utils.TaxConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;

@RestController()
@RequestMapping("/api/processedorders")
public class ProcessedOrdersController {

    private static final Logger logger = LoggerFactory.getLogger(ProcessedOrdersController.class);
    @Autowired
    private ProcessedOrdersRepo processedOrdersRepo;

    @Autowired
    private FulfillmentRepo fulfillmentRepo;

    @Autowired
    private ShippingLabelsRepo shippingLabelsRepo;

    @Autowired
    private AdminSharedService adminSharedService;

    @Value("${aws.bucket.name}")
    private String awsBucketName;

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @GetMapping("/allprocessedorders")
    public HashMap<String, Object> getAll(HttpServletRequest http) {

        // return (List<ProcessedOrders>)
        // processedOrdersRepo.findUnsentProcessedOrders(false);
        List<ProcessedOrders> orders = processedOrdersRepo.findUnsentProcessedOrders(false);

        List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        HashMap<String, Object> finalResult = new HashMap<>();
        Integer processedOrderCounter = 0;
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put("versionId", orders.get(i).getVersionId());

            try {
                processedOrderCounter = processedOrderCounter + orders.get(i).getOrdersCounter();
                temp.put("startOrderNumber", orders.get(i).getStartOrderNumber().toString());
                temp.put("endOrderNumber", orders.get(i).getEndOrderNumber().toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            result.add(temp);
        }

        Integer ordersInProcessCounter = fulfillmentRepo.findOrderInProcessingAndNotShipengine().size();

        Integer totalInProcess = ordersInProcessCounter;

        String warningMessage = "";
        if (!totalInProcess.equals(0) && !processedOrderCounter.equals(totalInProcess)) {
            warningMessage = "WARNING: " + processedOrderCounter + " orders in batch and "
                    + totalInProcess + " orders processing status.";
        }
        finalResult.put("processedOrders", result);
        finalResult.put("warningMessage", warningMessage);
        return finalResult;

        // return result;
    }

    @DeleteMapping("/deleteProcessedOrder/{versionIds}")
    public void delteProcessedOrder(HttpSession session, @PathVariable String versionIds) throws Exception {
        try {
            List<String> versionsList = Arrays.asList(versionIds.split(","));
            for (String versionId : versionsList) {
                ProcessedOrders processedOrder = processedOrdersRepo.findByVersionId(versionId);
                if (processedOrder != null) {
                    ArrayList<ShippingLabels> shippingLabels = shippingLabelsRepo
                            .findByProcessBatchId(processedOrder.getId());
                    for (ShippingLabels label : shippingLabels) {
                        adminSharedService.setShipingLabelAsVoid(label);
                    }
                }
                processedOrdersRepo.deleteByVersionId(versionId);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/downloadpdf/{versionId}")
    public @ResponseBody ResponseEntity<InputStreamResource> downladPdf(HttpSession session,
            @PathVariable String versionId)
            throws Exception {
        try {

            ProcessedOrders optRecord = processedOrdersRepo.findByVersionId(versionId);
            if (optRecord != null) {
                AWSCredentials awsCred = new BasicAWSCredentials(
                        this.awsAccessKey,
                        this.awsSecretKey);
                AmazonS3 s3client = AmazonS3ClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                        .withRegion(Regions.US_EAST_2)
                        .build();
                S3Object object = s3client.getObject(this.awsBucketName, optRecord.getPdfFile());
                S3ObjectInputStream objectContent = object.getObjectContent();
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + TaxConstants.PORT_POLIO_PDF)
                        .body(new InputStreamResource(objectContent));
            } else {
                throw new NotFoundException();
            }
        } catch (Exception e) {
            logger.error("Failed tod download pdf file", versionId, e);
            throw e;
        }
    }

    @GetMapping("/downloadLabelPdf/{versionId}")
    public @ResponseBody ResponseEntity<InputStreamResource> downloadLabelPdf(HttpSession session,
            @PathVariable String versionId)
            throws Exception {
        try {

            ProcessedOrders optRecord = processedOrdersRepo.findByVersionId(versionId);
            if (optRecord != null) {
                AWSCredentials awsCred = new BasicAWSCredentials(
                        this.awsAccessKey,
                        this.awsSecretKey);
                AmazonS3 s3client = AmazonS3ClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                        .withRegion(Regions.US_EAST_2)
                        .build();
                S3Object object = s3client.getObject(this.awsBucketName, optRecord.getShippingLabels());
                S3ObjectInputStream objectContent = object.getObjectContent();
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment;filename=" + TaxConstants.CURRENT_ORDERS_LABELS_PDF)
                        .body(new InputStreamResource(objectContent));
            } else {
                throw new NotFoundException();
            }
        } catch (Exception e) {
            logger.error("Failed to download labels pdf file", versionId, e);
            throw e;
        }
    }

}