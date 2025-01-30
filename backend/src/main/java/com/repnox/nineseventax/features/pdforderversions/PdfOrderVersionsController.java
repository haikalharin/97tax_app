package com.repnox.nineseventax.features.pdforderversions;

import com.repnox.nineseventax.features.ecommerce.OrderInfo;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.ein.EinRecordRepo;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanRepo;
import com.repnox.nineseventax.features.pdfcreator.PdfFormFilling;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.utils.TaxConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;

import static com.repnox.nineseventax.features.email.MailjetTemplates.*;

@RestController()
@RequestMapping("/api/pdforderversion")
public class PdfOrderVersionsController {

    private static final Logger logger = LoggerFactory.getLogger(PdfOrderVersionsController.class);

    @Autowired
    private PdfOrderVersionsRepo pdfOrderVersionRepo;

    @Autowired
    private  PaymentPlanRepo paymentPlanRepo;

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private PenaltyRecordRepo penaltyRecordRepo;

    @Autowired
    private EinRecordRepo einRecordRepo;
    
    @Autowired
    private PdfEmail pdfEmail;

    @PostMapping("/refreshOrder")
    public void save(HttpServletRequest request, @RequestBody OrderInfo orderInfo) throws Exception {
        try {
            PdfOrderVersions order = new PdfOrderVersions();
            PdfFormFilling pdfcreator = new PdfFormFilling();
            if (orderInfo.getProduct().equals(TaxConstants.PAYMENTPLAN)){
                PaymentPlanDetails plan = paymentPlanRepo.findByOrderNum(Long.parseLong(orderInfo.getOrderNumber()));
                order.setPdfFile(pdfcreator.PdfCreator(orderInfo, plan));
            } else {
                order.setPdfFile(pdfcreator.TaxLienPdfCreator(orderInfo));
            }
            order.setOrderId(orderInfo.getOrderNumber());
            ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findByOrderid(orderInfo.getOrderNumber());
            if(optRecord!= null && optRecord.size()>0){
                order.setVersionId(orderInfo.getOrderNumber() + "-" + (optRecord.size()));
            }else{
                order.setVersionId(orderInfo.getOrderNumber());
            }
            pdfOrderVersionRepo.save(order);
        } catch (Exception e) {
            logger.error("Failed to save the file", e);
            throw e;
        }
    }

    @PostMapping("penalty-waiver/refreshOrder")
    public void savePenaltyWaiverPdf(@RequestBody PenaltyOrder penaltyOrder) throws Exception {
        try {
            PdfOrderVersions order = new PdfOrderVersions();
            PdfFormFilling pdfcreator = new PdfFormFilling();

            penaltyOrder = penaltyRecordRepo.findById(penaltyOrder.getId()).get();

            String pwlfile = pdfcreator.PdfCreatorForPenaltyWaiverLetter(penaltyOrder);
            File file1 = null, file2 = new File(pwlfile);
            PDFMergerUtility PDFmerger = new PDFMergerUtility();
            PDFmerger.addSource(file2);
            String mergedFilename = "./merged_" + penaltyOrder.getId() + ".pdf";

            PDFmerger.setDestinationFileName(mergedFilename);

            int isPenaltyPaid = penaltyOrder.getIsPenaltyPaid();
            if(isPenaltyPaid == 1) {
                String f843file = pdfcreator.PdfCreatorForF843(penaltyOrder);
                file1 = new File(f843file);
                PDFmerger.addSource(file1);
            }
            
            PDFmerger.mergeDocuments();

            File mergedFile = new File(mergedFilename);
            byte[] fileContent = Files.readAllBytes(mergedFile.toPath());

            order.setOrderId(penaltyOrder.getId().toString());
            order.setPdfFile(fileContent);

            ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findByOrderid(penaltyOrder.getId().toString());
            if(optRecord!= null && optRecord.size()>0){
                order.setVersionId(penaltyOrder.getId() + "-v" + (optRecord.size()));
            }else{
                order.setVersionId(penaltyOrder.getId() + "-v" + 0);
            }
            pdfOrderVersionRepo.save(order);

            if(file1 != null)
                file1.delete();

            file2.delete();
            mergedFile.delete();

        } catch (Exception e) {
            logger.error("Failed to save the file", e);
            throw e;
        }
    }

    private void processsF843(@RequestBody PenaltyOrder penaltyOrder) throws IOException {
        PdfOrderVersions order = new PdfOrderVersions();
        PdfFormFilling pdfcreator = new PdfFormFilling();
        //order.setPdfFile(pdfcreator.PdfCreatorForF843(penaltyOrder));

        order.setOrderId(penaltyOrder.getId()+"_F843");
        ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findByOrderid(penaltyOrder.getId()+"_F843");
        if(optRecord!= null && optRecord.size()>0){
            order.setVersionId(penaltyOrder.getId()+"_F843" + "-v" + (optRecord.size()));
        }else{
            order.setVersionId(penaltyOrder.getId()+"_F843" + "-v" + 0);
        }
        pdfOrderVersionRepo.save(order);
    }

    private void processPenaltyWaiverFillableLetter(PenaltyOrder penaltyOrder) throws IOException {
        PdfOrderVersions order = new PdfOrderVersions();
        PdfFormFilling pdfcreator = new PdfFormFilling();

        // order.setPdfFile(pdfcreator.PdfCreatorForPenaltyWaiverLetter(penaltyOrder));

        order.setOrderId(penaltyOrder.getId()+"_PWFL");
        ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findByOrderid(order.getOrderId());
        if(optRecord!= null && optRecord.size()>0){
            order.setVersionId(penaltyOrder.getId()+"_PWFL" + "-v" + (optRecord.size()));
        }else{
            order.setVersionId(penaltyOrder.getId()+"_PWFL" + "-v" + 0);
        }
        pdfOrderVersionRepo.save(order);
    }

    @GetMapping("/getOrder/id/{orderNum}")
    public @ResponseBody ArrayList<PdfOrderVersions> getOrderRecord(HttpSession session, @PathVariable String orderNum ) throws Exception {
        try {

            ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findMetaInfoByOrderid(orderNum);

            if (optRecord!=null && optRecord.size()>0) {
                return optRecord;
            } else {
                return  new ArrayList<PdfOrderVersions>();
            }
        } catch (Exception e) {
            logger.error("Failed to get Order Records", e);
            throw e;
        }
    }

    /*
    @GetMapping("/getPenaltyOrder/id/{orderNum}")
    public @ResponseBody
    Map<String, Object> getPenaltyOrderRecord(HttpSession session, @PathVariable String orderNum ) throws Exception {
        try {

            ArrayList<PdfOrderVersions> f843Record = pdfOrderVersionRepo.findMetaInfoByOrderid(orderNum + "_F843");
            ArrayList<PdfOrderVersions> pwflRecord = pdfOrderVersionRepo.findMetaInfoByOrderid(orderNum + "_PWFL");

            Map<String, Object> result = new HashMap<>();

            if(f843Record != null)
                result.put("f843", f843Record);

            if(pwflRecord != null)
                result.put("pwfl", pwflRecord);

            return result;

        } catch (Exception e) {
            logger.error("Failed to get Order Records", e);
            throw e;
        }
    }*/

    @PostMapping("/emailpdf")
    public void sendMail(HttpServletRequest request, @RequestBody OrderInfo record) throws Exception {
        try {
            String address;
            String city2;
            String state2;
            String zip2;
            Integer templateid = 0;
            ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findByOrderid(record.getOrderNumber());
            byte[] encoded = Base64.getEncoder().encode(optRecord.get(optRecord.size()-1).getPdfFile());
            String state = record.getShippingState();
            Boolean isCalifornia = record.getIsCalifornia();
            Boolean isNewJersey = record.getIsNewJersey();
            Boolean isGeorgia = record.getIsGeorgia();
            Boolean isIllinois = record.getIsIllinois();
            HashMap<String, String> variables = new HashMap<>();
            String pattern = "MM/dd/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String orderDate = simpleDateFormat.format(record.getSubmissionDate()!=null ? record.getSubmissionDate() :new java.util.Date(System.currentTimeMillis()));

            variables.put(TaxConstants.PDF_EMAIL_FIRSTNAME, StringUtils.defaultIfBlank(record.getBillingFirstName(), "Customer"));

            if (isCalifornia != null && isCalifornia) {
                address = "P.O. Box 2952";
                city2 = "Sacramento";
                state2 = "CA";
                zip2 = "95812-2952";
            } else if (isNewJersey != null && isNewJersey) {
                address = "P.O. Box 190";
                city2 = "Trenton";
                state2 = "NJ";
                zip2 = "08695-0190"; 
            } else if (isGeorgia != null && isGeorgia) {
                address = "P.O. Box 105596";
                city2 = "Atlanta";
                state2 = "GA";
                zip2 = "30374-0396";
            }else if (isIllinois != null && isIllinois) {
                address = "PO BOX 19035";
                city2 = "Springfield";
                state2 = "IL";
                zip2 = "62794-9035";
            } else if ("PR".equals(state) || "AE".equals(state) || "AP".equals(state) || "AA".equals(state)) {
                address = "3651 South Ih 35 5501AUSC";
                city2 = "Austin";
                state2 = "TX";
                zip2 = "78741-7855";
            } else if ("AK" .equals(state) || "AZ" .equals(state) || "CO" .equals(state) || "CT" .equals(state) || "DE" .equals(state)
                || "DC" .equals(state) || "HI" .equals(state) || "ID" .equals(state) || "IL" .equals(state)
                || "ME" .equals(state) || "MD" .equals(state) || "MA" .equals(state) || "MT" .equals(state)
                || "NV" .equals(state) || "NH" .equals(state) || "NJ" .equals(state) || "NM" .equals(state)
                || "ND" .equals(state) || "OR" .equals(state) || "RI" .equals(state) || "SD" .equals(state)
                || "TN" .equals(state) || "UT" .equals(state) || "VT" .equals(state) || "WA" .equals(state)
                || "WI" .equals(state) || "WY" .equals(state)) {
                address = TaxConstants.DEFAULT_ADDRESS;
                city2 = TaxConstants.DEFAULT_CITY;
                state2 = TaxConstants.DEFAULT_STATE;
                zip2 = TaxConstants.DEFAULT_ZIP_CODE;
            } else if ("AL" .equals(state) || "FL" .equals(state) || "GA" .equals(state) || "KY" .equals(state)
                || "LA" .equals(state) || "MS" .equals(state) || "NC" .equals(state) || "SC" .equals(state)
                || "TX" .equals(state) || "VA" .equals(state)) {
                address = "P.O. Box 47421";
                city2 = "Doraville";
                state2 = "GA";
                zip2 = "30362-0421";
            } else if ("AR" .equals(state) || "CA" .equals(state) || "IN" .equals(state) || "IA" .equals(state)
                || "KS" .equals(state) || "MI" .equals(state) || "MN" .equals(state) || "MO" .equals(state)
                || "NE" .equals(state) || "NY" .equals(state) || "OH" .equals(state) || "OK" .equals(state)
                || "PA" .equals(state) || "WV" .equals(state)) {
                address = "Stop P-4 5000";
                city2 = "Kansas City";
                state2 = "MO";
                zip2 = "64999-0250";
            } else {
                address = TaxConstants.DEFAULT_ADDRESS;
                city2 = TaxConstants.DEFAULT_CITY;
                state2 = TaxConstants.DEFAULT_STATE;
                zip2 = TaxConstants.DEFAULT_ZIP_CODE;
            }
            variables.put(TaxConstants.PDF_EMAIL_ADDRESS, address);
            variables.put(TaxConstants.PDF_EMAIL_CITY, city2);
            variables.put(TaxConstants.PDF_EMAIL_STATE, state2);
            variables.put(TaxConstants.PDF_EMAIL_ZIP_CODE, zip2);

            if (record.getIsCalifornia() != null && record.getIsCalifornia() && record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
                templateid = californiaPaymentplanTemplateId;
            } else if (record.getIsNewJersey() != null && record.getIsNewJersey() && record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
                templateid = newJerseyPaymentplanTemplateId;
            } else if (record.getIsGeorgia() != null && record.getIsGeorgia() && record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
                String status = record.getStatus();
                if (status.equals(OrderRecord.STATUS_COMPLETE)) {
                    templateid = georgiaPaymentPlanCompleteTemplateId;
                } else if (status.equals(OrderRecord.STATUS_FAILED)) {
                    templateid = georgiaPaymentPlanFailedTemplateId;
                } else if (status.equals(OrderRecord.STATUS_INCOMPLETE)) {
                    templateid = georgiaPaymentPlanIncompleteTemplateId;
                } else if (status.equals(OrderRecord.STATUS_PROCESSING)) {
                    templateid = georgiaPaymentPlanProcessingTemplateId;
                } else if (status.toLowerCase().equals(OrderRecord.COPY_REQUESTED)) {
                    templateid = georgiaPaymentPlanCopyRequestedTemplateId;
                }
                OrderRecord orderRecord = orderRecordRepo.findById(Long.parseLong(record.getOrderNumber())).get();
                PaymentPlanDetails plan = paymentPlanRepo.findByOrderNum(Long.parseLong(record.getOrderNumber()));
                variables.put(TaxConstants.PDF_EMAIL_FIRSTNAME, record.getBillingFirstName());
                variables.put(TaxConstants.PDF_EMAIL_NAME, record.getBillingFirstName()+" "+record.getBillingLastName());
                variables.put(TaxConstants.PDF_EMAIL_MAILING_ADDRESS, record.getBillingAddress1());
                variables.put(TaxConstants.PDF_EMAIL_DATE_FORMATTED, orderDate);
                variables.put(TaxConstants.PDF_EMAIL_ORDER_NUMBER, record.getOrderNumber());
                variables.put(TaxConstants.PDF_EMAIL_MONTHLY_PAYMENT_AMOUNT, plan.getMonthlyPayment().toString());
                variables.put(TaxConstants.PDF_EMAIL_MONTHLY_PAYMENT_DATE, plan.getPaymentDayOfMonth());
                variables.put(TaxConstants.PDF_EMAIL_PAYMENT_AMOUNT, record.getAmount());
                variables.put(TaxConstants.PDF_EMAIL_TOTAL_DEBT_OWED, record.getAmount());
                variables.put(TaxConstants.PDF_EMAIL_TIME_TO_CALL, record.getTimeToCall());
                variables.put(TaxConstants.PDF_EMAIL_TRACKING_NUMBER,orderRecord.getTrackingNumber());
                variables.put(TaxConstants.PDF_EMAIL_TOTAL_DEBT_OWED, record.getAmount());
                variables.put(TaxConstants.PDF_EMAIL_PHONE,record.getBillingPhone());
            }
            else if (record.getIsIllinois() != null && record.getIsIllinois() && record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
                String status = record.getStatus();
                if (status.equals(OrderRecord.STATUS_COMPLETE)) {
                    templateid = illinoisPaymentPlanCompleteTemplateId;
                } else if (status.equals(OrderRecord.STATUS_FAILED)) {
                    templateid = illinoisPaymentPlanFailedTemplateId;
                } else if (status.equals(OrderRecord.STATUS_INCOMPLETE)) {
                    templateid = illinoisPaymentPlanIncompleteTemplateId;
                } else if (status.equals(OrderRecord.STATUS_PROCESSING)) {
                    templateid = illinoisPaymentPlanProcessingTemplateId;
                } else if (status.toLowerCase().equals(OrderRecord.COPY_REQUESTED)) {
                    templateid = illinoisPaymentPlanCopyRequestedTemplateId;
                }
                OrderRecord orderRecord = orderRecordRepo.findById(Long.parseLong(record.getOrderNumber())).get();
                PaymentPlanDetails plan = paymentPlanRepo.findByOrderNum(Long.parseLong(record.getOrderNumber()));
                variables.put(TaxConstants.PDF_EMAIL_FIRSTNAME, record.getBillingFirstName());
                variables.put(TaxConstants.PDF_EMAIL_NAME, record.getBillingFirstName()+" "+record.getBillingLastName());
                variables.put(TaxConstants.PDF_EMAIL_MAILING_ADDRESS, record.getBillingAddress1());
                variables.put(TaxConstants.PDF_EMAIL_DATE_FORMATTED, orderDate);
                variables.put(TaxConstants.PDF_EMAIL_ORDER_NUMBER, record.getOrderNumber());
                variables.put(TaxConstants.PDF_EMAIL_MONTHLY_PAYMENT_AMOUNT, plan.getMonthlyPayment().toString());
                variables.put(TaxConstants.PDF_EMAIL_MONTHLY_PAYMENT_DATE, plan.getPaymentDayOfMonth());
                variables.put(TaxConstants.PDF_EMAIL_PAYMENT_AMOUNT, record.getAmount());
                variables.put(TaxConstants.PDF_EMAIL_TOTAL_DEBT_OWED, record.getAmount());
                variables.put(TaxConstants.PDF_EMAIL_TIME_TO_CALL, record.getTimeToCall());
                variables.put(TaxConstants.PDF_EMAIL_TRACKING_NUMBER,orderRecord.getTrackingNumber());
                variables.put(TaxConstants.PDF_EMAIL_TOTAL_DEBT_OWED, record.getAmount());
                variables.put(TaxConstants.PDF_EMAIL_PHONE,record.getBillingPhone());
            }
            else {
                if (record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
                    templateid = paymentplanTemplateId;
                } else {
                    templateid = taxlienTemplateId;
                }
            }

            pdfEmail.sendTemplateEmail(new String(encoded), record.getEmail(), record.getBillingFirstName(),
                optRecord.get(optRecord.size()-1).getVersionId(), variables, templateid);
            updateNote(Long.parseLong(record.getOrderNumber()), "SYSTEM", "COPY SENT","ADMIN");
        } catch(Exception e) {
            logger.error("Failed to send email ", record.getOrderNumber(), e);
            throw e;
        }
    }

    private void updateNote(long orderNum, String type, String content, String user) {
    	String note_detail = "";
    	
    	Date date = new Date();  
	    // SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");  
	    // String strDate= formatter.format(date);  
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");  
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
	    String strDate= formatter.format(date);

    	note_detail = type + "=*=" + content + "=*=" + user + "=*=" + strDate;    	
    	OrderRecord orderRecord = orderRecordRepo.findById(orderNum).get();
        if (orderRecord != null) {
            String details = "";
            if(orderRecord.getNotes() == null) {
            	details = note_detail;
            }else {
            	details = orderRecord.getNotes() + "###" + note_detail;            	
            }
            orderRecord.setNotes(details);
            orderRecordRepo.save(orderRecord);
        }
    }

    private void updatePenaltyNote(long orderNum, String type, String content, String user) {
        String note_detail = "";

        Date date = new Date();
        // SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        // String strDate= formatter.format(date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String strDate= formatter.format(date);

        note_detail = type + "=*=" + content + "=*=" + user + "=*=" + strDate;
        PenaltyOrder orderRecord = penaltyRecordRepo.findById(orderNum).get();
        if (orderRecord != null) {
            String details = "";
            if(orderRecord.getNotes() == null) {
                details = note_detail;
            }else {
                details = orderRecord.getNotes() + "###" + note_detail;
            }
            orderRecord.setNotes(details);
            penaltyRecordRepo.save(orderRecord);
        }
    }

    private void updateEinNote(long orderNum, String type, String content, String user) {
        String note_detail = "";

        Date date = new Date();
        // SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        // String strDate= formatter.format(date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String strDate= formatter.format(date);

        note_detail = type + "=*=" + content + "=*=" + user + "=*=" + strDate;
        EinOrder orderRecord = einRecordRepo.findById(orderNum).get();
        if (orderRecord != null) {
            String details = "";
            if(orderRecord.getNotes() == null) {
                details = note_detail;
            }else {
                details = orderRecord.getNotes() + "###" + note_detail;
            }
            orderRecord.setNotes(details);
            einRecordRepo.save(orderRecord);
        }
    }

    @GetMapping("/pdfDownload/id/{versionId}")
    public @ResponseBody ResponseEntity<Resource> downloadPdf(HttpSession session, @PathVariable String versionId) throws Exception {
        try {
            ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findByVersionId(versionId);
            if (optRecord!=null && optRecord.size()>0) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + optRecord.get(0).getOrderId() + TaxConstants.PDF_EXTENSION)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new ByteArrayResource(optRecord.get(0).getPdfFile()));
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to download the pdf file for version ",versionId, e);
            throw e;
        }
    }

    @PostMapping("/penalty/emailpdf")
    public void sendPenaltyMail(HttpServletRequest request, @RequestBody OrderInfo record) throws Exception {
        try {
            PenaltyOrder penaltyOrder = penaltyRecordRepo.findById(Long.valueOf(record.getOrderNumber())).get();

            ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findByOrderid(record.getOrderNumber());
            byte[] encoded = Base64.getEncoder().encode(optRecord.get(optRecord.size()-1).getPdfFile());

            String address = StringUtils.trimToEmpty(penaltyOrder.getIrsAddress1()) + " " +
                    StringUtils.trimToEmpty(penaltyOrder.getIrsAddress2());
            HashMap<String, String> variables = new HashMap<>();
            variables.put(TaxConstants.PDF_EMAIL_FIRSTNAME, StringUtils.defaultIfBlank(penaltyOrder.getFirstName(), "Customer"));
            variables.put(TaxConstants.PDF_EMAIL_ADDRESS, "");
            variables.put(TaxConstants.PDF_EMAIL_CITY, penaltyOrder.getIrsCity());
            variables.put(TaxConstants.PDF_EMAIL_STATE, penaltyOrder.getIrsState());
            variables.put(TaxConstants.PDF_EMAIL_ZIP_CODE, penaltyOrder.getIrsZipcode());

            pdfEmail.sendTemplateEmail(new String(encoded), record.getEmail(), record.getBillingFirstName(),
                    optRecord.get(optRecord.size()-1).getVersionId(), variables, penaltyWaiverEmailCopyRequestedId);
            updatePenaltyNote(Long.parseLong(record.getOrderNumber()), "SYSTEM", "COPY SENT","ADMIN");

        } catch(Exception e) {
            logger.error("Failed to send email ", record.getOrderNumber(), e);
            throw e;
        }
    }

    @PostMapping("/ein/emailpdf")
    public void sendEinMail(HttpServletRequest request, @RequestBody OrderInfo record) throws Exception {
        try {
            EinOrder einOrder = einRecordRepo.findById(Long.valueOf(record.getOrderNumber())).get();

            ArrayList<PdfOrderVersions> optRecord = pdfOrderVersionRepo.findByOrderid(record.getOrderNumber());
            byte[] encoded = Base64.getEncoder().encode(optRecord.get(optRecord.size()-1).getPdfFile());

            HashMap<String, String> variables = new HashMap<>();
            variables.put(TaxConstants.PDF_EMAIL_FIRSTNAME, StringUtils.defaultIfBlank(einOrder.getFirst_name(), "Customer"));

            pdfEmail.sendTemplateEmail(new String(encoded), record.getEmail(), record.getBillingFirstName(),
                    optRecord.get(optRecord.size()-1).getVersionId(), variables, einEmailCopyRequestedId);
            updateEinNote(Long.parseLong(record.getOrderNumber()), "SYSTEM", "COPY SENT", "ADMIN");
        } catch(Exception e) {
            logger.error("Failed to send email ", record.getOrderNumber(), e);
            throw e;
        }
    }

}
