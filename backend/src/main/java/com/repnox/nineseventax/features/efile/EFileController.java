package com.repnox.nineseventax.features.efile;

import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.auth.AuthRole;
import com.repnox.nineseventax.features.auth.AuthService;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.efile.f9465.F9465SubmissionService;
import com.repnox.nineseventax.features.efile.jpa.ValidationError;
import com.repnox.nineseventax.features.efile.util.EFileUtil;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanRepo;
import gov.irs.efile.ValidationErrorListType;
import gov.irs.mef.AcknowledgementList;
import gov.irs.mef.services.transmitter.GetAckResult;
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

import javax.servlet.http.HttpSession;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.repnox.nineseventax.features.efile.IRSSubmissionService.ACCEPTED_STATUS;
import static com.repnox.nineseventax.features.efile.util.EFileUtil.submissionId;

@RestController()
@RequestMapping("/api/efile")
public class EFileController {

    private static final Logger LOG = LoggerFactory.getLogger(EFileController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private PaymentPlanRepo paymentPlanRepo;

    @Autowired
    private MailjetSender mailjetSender;

    @Value("${irs.efile.efin}")
    private String efin;

    @Autowired
    private F9465SubmissionService f9465SubmissionService;

    @Autowired
    private IRSSubmissionService irsSubmissionService;

    @PostMapping("/f9465/submit/{orderId}")
    public @ResponseBody String submitF9465(HttpSession session, @PathVariable Long orderId) throws Exception {
        LOG.info("submitF9465() called");
        authService.requireRole(session, AuthRole.ADMIN);

        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderId);
        PaymentPlanDetails details = paymentPlanRepo.findByOrderNum(orderId);
        String noteStatus = "";
        if (optRecord.isPresent() && details != null) {
            OrderRecord order = optRecord.get();

            String submissionId = submissionId(efin);
            String submissionXMLData = f9465SubmissionService.generateSubmission(order, details);
            String manifestXMLData = f9465SubmissionService.generateManifest(order, submissionId);

            // TODO: Add call logic with startAfterMillis: 5 minutes from now, 24 hours from now
            String ackXMLData = "";
            String submissionDateEST = "";
            String status = "";
            List<ValidationError> errorCodes = new ArrayList<>();
            try {
                GetAckResult ackResult = irsSubmissionService.submitFor(submissionId, submissionXMLData, manifestXMLData);
                AcknowledgementList ackList = ackResult.getAcknowledgementList();
                AcknowledgementList.Acknowledgement theAck = ackList.getAcknowledgementBySubmissionId(submissionId);
                if (theAck != null) {
                    ackXMLData = new String(theAck.toByteArray());
                    XMLGregorianCalendar submissionDate = theAck.getElectronicPostmarkTs();
                    submissionDateEST = EFileUtil.formatCalendar(submissionDate);
                    status = theAck.getAcceptanceStatusTxt();
                    noteStatus = ACCEPTED_STATUS.equals(status) ? "SUCCESSFUL" : "FAILED";
                    errorCodes = getErrorCodes(theAck.getValidationErrorList());
                } else {
                    status = "No ack returned";
                    noteStatus = "SUBMITTED, NO ACK RETURNED";
                }
            } catch (Exception e) {
                status = "Exception thrown";
                noteStatus = "FAILED";
                LOG.error("ERROR: Exception thrown: submitF9465(): 1777", e);
            }

            order.setF9465SubmissionId(submissionId);
            order.setF9465Submission(submissionXMLData);
            order.setF9465Manifest(manifestXMLData);
            order.setF9465AckResponse(ackXMLData);
            order.setF9465SubmissionDate(submissionDateEST);
            order.setF9465Status(status);
            order.setF9465ErrorCodes(errorCodes);
            boolean eFileSuccess = ACCEPTED_STATUS.equals(status);
            if (eFileSuccess) {
                order.setStatus(OrderRecord.STATUS_COMPLETE);
            }
            orderRecordRepo.save(order);

            if (eFileSuccess) {
                try {
                    mailjetSender.sendCompleteEmail(order);
                } catch (IOException e) {
                    LOG.error("ERROR: Send Completed Email: submitF9465(): 93", e);
                }
            }
        } else {
            LOG.error("ERROR: Order Not Found: submitF9465(): 1770");
            noteStatus = "FAILED, ORDER NOT FOUND";
            throw new NotFoundException();
        }

        updateNote(orderId, "SYSTEM", "FORM 9465 EFILE SUBMITTED (" + noteStatus + ")", "ADMIN");
        return "Ok";
    }

    private List<ValidationError> getErrorCodes(ValidationErrorListType errorsList) {
        List<ValidationError> result = new ArrayList<>();
        if (errorsList == null) {
            return result;
        }

        List<ValidationErrorListType.ValidationErrorGrp> errors =  errorsList.getValidationErrorGrp();
        if (errors == null || errors.isEmpty()) {
            return result;
        }

        for (ValidationErrorListType.ValidationErrorGrp next: errors) {
            ValidationError nextError = new ValidationError();
            nextError.setDocumentId(next.getDocumentId());
            nextError.setXpathContentTxt(next.getXpathContentTxt());
            nextError.setErrorCategoryCd(next.getErrorCategoryCd());
            nextError.setErrorMessageTxt(next.getErrorMessageTxt());
            nextError.setRuleNum(next.getRuleNum());
            nextError.setSeverityCd(next.getSeverityCd());
            result.add(nextError);
        }
        return result;
    }

    @GetMapping("/f9465/xml/submission/{orderId}/sample")
    public ResponseEntity<InputStreamResource> downloadF9465SubmissionXMLSample(HttpSession session, @PathVariable Long orderId) throws Exception {
        LOG.info("downloadF9465SubmissionXMLSample() called");
        authService.requireRole(session, AuthRole.ADMIN);

        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderId);
        PaymentPlanDetails details = paymentPlanRepo.findByOrderNum(orderId);
        if (optRecord.isPresent() && details != null) {
            OrderRecord order = optRecord.get();
            String submission = f9465SubmissionService.generateSubmission(order, details);
            InputStream stream = new ByteArrayInputStream(submission.getBytes(StandardCharsets.UTF_8));

            String filename = "submission_sample.xml";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                    .body(new InputStreamResource(stream));
        } else {
            LOG.error("ERROR: Order Not Found: downloadF9465SubmissionXMLSample(): 1771");
            throw new NotFoundException();
        }
    }

    @GetMapping("/f9465/xml/manifest/{orderId}/sample")
    public ResponseEntity<InputStreamResource> downloadF9465ManifestXMLSample(HttpSession session, @PathVariable Long orderId) throws Exception {
        LOG.info("downloadF9465ManifestXMLSample() called");
        authService.requireRole(session, AuthRole.ADMIN);

        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderId);
        if (optRecord.isPresent()) {
            OrderRecord order = optRecord.get();
            String submissionId = submissionId(efin);
            String manifest = f9465SubmissionService.generateManifest(order, submissionId);
            InputStream stream = new ByteArrayInputStream(manifest.getBytes(StandardCharsets.UTF_8));

            String filename = "manifest_sample.xml";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                    .body(new InputStreamResource(stream));
        } else {
            LOG.error("ERROR: Order Not Found: downloadF9465ManifestXMLSample(): 1772");
            throw new NotFoundException();
        }
    }

    @GetMapping("/f9465/xml/{type}/{orderId}")
    public ResponseEntity<InputStreamResource> downloadF9465XML(
            HttpSession session, @PathVariable String type, @PathVariable Long orderId) throws Exception {
        LOG.info("downloadF9465XML() called");
        authService.requireRole(session, AuthRole.ADMIN);

        Optional<OrderRecord> optRecord = orderRecordRepo.findById(orderId);
        if (optRecord.isPresent()) {
            OrderRecord order = optRecord.get();
            String body;
            if ("submission".equalsIgnoreCase(type)) {
                body = order.getF9465Submission();
            } else if ("manifest".equalsIgnoreCase(type)) {
                body = order.getF9465Manifest();
            } else {
                body = order.getF9465AckResponse();
            }

            if (StringUtils.isBlank(body)) {
                throw new NotFoundException();
            }
            InputStream stream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
            String filename = type + "_" + orderId + ".xml";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                    .body(new InputStreamResource(stream));
        } else {
            LOG.error("ERROR: Order Not Found: downloadF9465XML(): 1773");
            throw new NotFoundException();
        }
    }

    // TODO: Encapsulate this logic, too much of unnecessary copies
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

}
