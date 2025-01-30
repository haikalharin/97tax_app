package com.repnox.nineseventax.features.email;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import com.repnox.nineseventax.features.auth.UserRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUser;
import com.repnox.nineseventax.features.oic.OicRepository;
import com.repnox.nineseventax.features.oic.model.OicCalculations;
import com.repnox.nineseventax.features.oic.model.OicModel;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanRepo;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalDetails;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalRepo;
import com.repnox.nineseventax.features.utils.DateUtil;
import com.repnox.nineseventax.features.utils.TaxConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.repnox.nineseventax.features.efile.IRSSubmissionService.ACCEPTED_STATUS;
import static com.repnox.nineseventax.features.email.MailjetTemplates.*;

@Component
public class MailjetSender {

    private static final Logger LOG = LoggerFactory.getLogger(MailjetSender.class);

    @Value("${email.smtp.host}")
    private String smtpHost;

    @Value("${email.smtp.socket.port}")
    private String socketPort;

    @Value("${email.smtp.socket.class}")
    private String socketClass;

    @Value("${email.smtp.auth}")
    private String emailAuth;

    @Value("${email.smtp.port}")
    private String smtpPort;

    @Value("${email.smtp.auth.key}")
    private String authKey;

    @Value("${email.smtp.auth.secret}")
    private String authSecret;

    @Value("${email.from.email}")
    private String fromEmail;

    @Value("${email.contact.to}")
    private String contactToEmail;

    @Value("${email.over25k.to}")
    private String over25kToEmail;

    @Value("${email.over50k.to}")
    private String over50kToEmail;

    @Value("${email.from.name}")
    private String fromEmailName;

    @Value("${docusign.app.url}")
    private String appURL;

    @Autowired
    private PaymentPlanRepo paymentPlanRepo;

    @Autowired
    private TaxLienRemovalRepo taxLienRemovalRepo;

    @Autowired
    private OicRepository oicRepository;

    public void sendNewUserEmail(UserRecord newUser) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("email_templates/NewUserEmail.html");
        StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, newUser).flush();
        String to = newUser.getEmail();
        String subject = "Registration successful!";

        sendHtmlEmail(to, subject, stringWriter.toString());
    }

    public void sendPasswordResetEmail(PasswordResetEmail passwordResetEmail) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("email_templates/PasswordResetEmail.html");
        StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, passwordResetEmail).flush();

        sendHtmlEmail(passwordResetEmail.getToEmail(), "Password Reset", stringWriter.toString());
    }

    public void sendContactEmail(ContactEmail email) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("email_templates/ContactEmail.html");
        StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, email).flush();
        String to = "";
        if (email.getOver25k() == Boolean.TRUE) {
            to = over25kToEmail;
        } else if (email.getOver50k() == Boolean.TRUE) {
            to = over50kToEmail;
        } else {
            to = contactToEmail;
        }

        sendHtmlEmail(to, email.getSubject(), stringWriter.toString(), email.getEmail());
    }

    public MailjetResponse sendHtmlEmail(String to, String subject, String html) {
        return sendHtmlEmail(to, subject, html, null);
    }

    public MailjetResponse sendHtmlEmail(String to, String subject, String html, String replyTo) {
        MailjetClient client = new MailjetClient(authKey, authSecret, new ClientOptions("v3.1"));
        JSONObject message = new JSONObject();

        message.put(
                Emailv31.Message.FROM,
                new JSONObject()
                        .put(Emailv31.Message.EMAIL, fromEmail)
                        .put(Emailv31.Message.NAME, fromEmailName)
        ).put(
                Emailv31.Message.TO,
                new JSONArray().put(new JSONObject()
                        .put(Emailv31.Message.EMAIL, to)
                        .put(Emailv31.Message.NAME, to))
        ).put(
                Emailv31.Message.SUBJECT,
                subject
        ).put(
                Emailv31.Message.HTMLPART,
                html
        );

        if (replyTo != null) {
            message.put(Emailv31.Message.REPLYTO,
                    new JSONObject()
                            .put(Emailv31.Message.EMAIL, replyTo)
                            .put(Emailv31.Message.NAME, replyTo)
            );
        }

        //LOG.info("Email Request: "+message.toString());

        MailjetRequest email = new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, (new JSONArray()).put(message));

        try {
            MailjetResponse response = client.post(email);

            //LOG.info("Email Response: "+response.getData().toString());
            return response;
        } catch (Exception e) {
            LOG.error("Unable to send contact email! ", e);
            return null;
        }
    }

    public void sendProcessingEmail(OrderRecord orderRecord) throws IOException {
        if (TaxConstants.PAYMENTPLAN.equals(orderRecord.getProduct())) {
            PaymentPlanDetails paymentPlan = paymentPlanRepo.findByOrderNum(orderRecord.getOrderNum());
            sendPaymentPlanProcessingEmail(orderRecord, paymentPlan);
        } else if (TaxConstants.TAXLIEN_REMOVAL.equals(orderRecord.getProduct())) {
            TaxLienRemovalDetails details = taxLienRemovalRepo.getByOrderNum(orderRecord.getOrderNum());
            sendTaxLienRemovalProcessingEmail(orderRecord, details);
        } else if ("OfferInCompromise".equals(orderRecord.getProduct())) {
            OicModel model = oicRepository.getByOrderNum(orderRecord.getOrderNum());
            sendOicProcessingEmail(orderRecord, model);
        }
    }

    public void sendCompleteEmail(OrderRecord orderRecord) throws IOException {
        if (TaxConstants.PAYMENTPLAN.equals(orderRecord.getProduct())) {
            PaymentPlanDetails paymentPlan = paymentPlanRepo.findByOrderNum(orderRecord.getOrderNum());
            sendPaymentPlanCompleteEmail(orderRecord, paymentPlan);
        } else if (TaxConstants.TAXLIEN_REMOVAL.equals(orderRecord.getProduct())) {
            TaxLienRemovalDetails details = taxLienRemovalRepo.getByOrderNum(orderRecord.getOrderNum());
            sendTaxLienRemovalCompleteEmail(orderRecord, details);
        } else if ("OfferInCompromise".equals(orderRecord.getProduct())) {
            OicModel model = oicRepository.getByOrderNum(orderRecord.getOrderNum());
            sendOicCompleteEmail(orderRecord, model);
        }
    }

    public void sendOicProcessingEmail(OrderRecord orderRecord, OicModel oicModel) {
        HashMap<String, String> variables = mapOicEmailData(orderRecord, oicModel);
        String subject = "Offer in Compromise " + orderRecord.getOrderNum() + " In Progress";
        sendTemplateEmail(orderRecord.getEmail(), subject, oicProcessingTemplateId, variables);
    }

    public void sendOicCompleteEmail(OrderRecord orderRecord, OicModel oicModel) {
        HashMap<String, String> variables = mapOicEmailData(orderRecord, oicModel);
        String subject = "Offer in Compromise " + orderRecord.getOrderNum() + " Complete";
        sendTemplateEmail(orderRecord.getEmail(), subject, oicCompleteTemplateId, variables);
    }

    private HashMap<String, String> mapOicEmailData(OrderRecord orderRecord, OicModel oicModel) {
        HashMap<String, String> variables = commonVariables(orderRecord);
        variables.put("totalDebtOwed", orderRecord.getTotalDebt().toPlainString());
        OicCalculations oicCalculations = oicModel.getOicCalculations();
        if (OicCalculations.SELECTED_OPTION_12.equals(oicCalculations.getSelectedSettlement())) {
            variables.put("totalSettlement", formatInteger(oicCalculations.getCalc12MonthSettlement()));
            variables.put("monthlyPaymentAmount", formatInteger(oicCalculations.getCalc12MonthSettlement()/12));
        } else if (OicCalculations.SELECTED_OPTION_24.equals(oicCalculations.getSelectedSettlement())) {
            variables.put("totalSettlement", formatInteger(oicCalculations.getCalc24MonthSettlement()));
            variables.put("monthlyPaymentAmount", formatInteger(oicCalculations.getCalc24MonthSettlement()/24));
        } else if (OicCalculations.SELECTED_OPTION_EXCEPTION_5.equals(oicCalculations.getSelectedSettlement())) {
            variables.put("totalSettlement", formatInteger(
                    oicCalculations.getExceptionPaymentAmount1()
                    + oicCalculations.getExceptionPaymentAmount2()
                    + oicCalculations.getExceptionPaymentAmount3()
                    + oicCalculations.getExceptionPaymentAmount4()
                    + oicCalculations.getExceptionPaymentAmount5()
                )
            );
            variables.put("monthlyPaymentAmount", "Custom");
        } else if (OicCalculations.SELECTED_OPTION_EXCEPTION_24.equals(oicCalculations.getSelectedSettlement())) {
            variables.put("totalSettlement", formatInteger(oicCalculations.getExceptionPaymentAmountMonthly() * 24));
            variables.put("monthlyPaymentAmount", formatInteger(oicCalculations.getExceptionPaymentAmountMonthly()));
        }

        variables.put("totalIncome", formatInteger(oicCalculations.getTotalIncome()));
        variables.put("totalAssets", formatInteger(oicCalculations.getTotalAssets()));
        variables.put("totalExpenses", formatInteger(oicCalculations.getTotalExpenses()));
        variables.put("monthlyPaymentDate", "");
        variables.put("timeToCall", "");
        return variables;
    }

    public void sendTaxLienRemovalProcessingEmail(OrderRecord orderRecord, TaxLienRemovalDetails details) {
        HashMap<String, String> variables = mapTaxLienData(orderRecord, details);

        String subject = "Tax Lien Removal " + orderRecord.getOrderNum() + " In Progress";

        sendTemplateEmail(orderRecord.getEmail(), subject, taxLienRemovalProcessingTemplateId, variables);
    }

    public void sendTaxLienRemovalCompleteEmail(OrderRecord orderRecord, TaxLienRemovalDetails details) {
        HashMap<String, String> variables = mapTaxLienData(orderRecord, details);

        String subject = "Tax Lien Removal " + orderRecord.getOrderNum() + " Complete";

        sendTemplateEmail(orderRecord.getEmail(), subject, taxLienRemovalCompleteTemplateId, variables);
    }

    private HashMap<String, String> mapTaxLienData(OrderRecord orderRecord, TaxLienRemovalDetails details) {
        HashMap<String, String> variables = commonVariables(orderRecord);
        String businessName = StringUtils.isNotBlank(details.getTaxLienBusinessName()) ? details.getTaxLienBusinessName() : "N/A";
        variables.put("businessName", businessName);
        variables.put("serialNumber", details.getSerialNumber());
        variables.put("removalOption", "");
        return variables;
    }

    public void sendPaymentPlanProcessingEmail(OrderRecord orderRecord, PaymentPlanDetails details) {
        Boolean isCalifornia = orderRecord.getIsCalifornia();
        Boolean isNewJersey = orderRecord.getIsNewJersey();
        Boolean isGeorgia = orderRecord.getIsGeorgia();
        Boolean isIllinois = orderRecord.getIsIllinois();
        Boolean isMichigan = orderRecord.getIsMichigan();
        HashMap<String, String> variables = mapPaymentPlanEmailData(orderRecord, details);
        String subject = null;
        Integer templateId = null;

        if (isCalifornia != null && isCalifornia) {
            subject = "California Payment Plan Application " + orderRecord.getOrderNum() + " In Progress";
            templateId = californiaPaymentPlanProcessingTemplateId;
        } else if (isNewJersey != null && isNewJersey) {
            subject = "New Jersey Payment Plan Application " + orderRecord.getOrderNum() + " In Progress";
            templateId = newJerseyPaymentPlanProcessingTemplateId;
        } else if (isGeorgia != null && isGeorgia) {
            subject = "Georgia Payment Plan Application " + orderRecord.getOrderNum() + " In Progress";
            templateId = georgiaPaymentPlanProcessingTemplateId;
        } else if (isIllinois != null && isIllinois) {
            subject = "Illinois Payment Plan Application " + orderRecord.getOrderNum() + " In Progress";
            templateId = illinoisPaymentPlanProcessingTemplateId;
        } else if (isMichigan != null && isMichigan) {
            subject = "Michigan Payment Plan Application " + orderRecord.getOrderNum() + " In Progress";
            templateId = michiganPaymentPlanProcessingTemplateId;
        } else {
            subject = "IRS Payment Plan Application " + orderRecord.getOrderNum() + " In Progress";
            templateId = paymentPlanProcessingTemplateId;
        }

        sendTemplateEmail(orderRecord.getEmail(), subject, templateId, variables);
    }

    public void sendPaymentPlanCompleteEmail(OrderRecord orderRecord, PaymentPlanDetails details) {
        Boolean isCalifornia = orderRecord.getIsCalifornia();
        Boolean isNewJersey = orderRecord.getIsNewJersey();
        Boolean isGeorgia = orderRecord.getIsGeorgia();
        Boolean isIllinois = orderRecord.getIsIllinois();
        Boolean isMichigan = orderRecord.getIsMichigan();
        HashMap<String, String> variables = mapPaymentPlanEmailData(orderRecord, details);
        String subject = null;
        Integer templateId = null;

        if (isCalifornia != null && isCalifornia) {
            subject = "California Payment Plan Application " + orderRecord.getOrderNum() + " Complete";
            templateId = californiaPaymentPlanCompleteTemplateId;
        } else if (isNewJersey != null && isNewJersey) {
            subject = "New Jersey Payment Plan Application " + orderRecord.getOrderNum() + " Complete";
            templateId = newJerseyPaymentPlanCompleteTemplateId;
        } else if (isGeorgia != null && isGeorgia) {
            subject = "Georgia Payment Plan Application " + orderRecord.getOrderNum() + " Complete";
            templateId = georgiaPaymentPlanCompleteTemplateId;
        }else if (isIllinois != null && isIllinois) {
            subject = "Illinois Payment Plan Application " + orderRecord.getOrderNum() + " Complete";
            templateId = illinoisPaymentPlanCompleteTemplateId;
        } else if (isMichigan != null && isMichigan) {
            subject = "Michigan Payment Plan Application " + orderRecord.getOrderNum() + " Complete";
            templateId = michiganPaymentPlanCompleteTemplateId;
        } else {
            subject = "IRS Payment Plan Application " + orderRecord.getOrderNum() + " Complete";
            templateId = paymentPlanCompleteTemplateId;

            boolean eFile = ACCEPTED_STATUS.equals(orderRecord.getF9465Status()); // indicates that order completed using e-File
            if (eFile) {
                templateId = paymentPlanEfileCompleteTemplateId;
                variables.put("SubmissionID", orderRecord.getF9465SubmissionId());
                variables.put("PractitionerPIN", orderRecord.getF9465SubmissionId());
            }
        }

        sendTemplateEmail(orderRecord.getEmail(), subject, templateId, variables);
    }

    private HashMap<String, String> mapPaymentPlanEmailData(OrderRecord orderRecord, PaymentPlanDetails details) {
        HashMap<String, String> variables = commonVariables(orderRecord);
        variables.put("totalDebtOwed", orderRecord.getTotalDebt().toPlainString());
        variables.put("monthlyPaymentAmount", formatInteger(details.getMonthlyPayment()));
        variables.put("monthlyPaymentDate", details.getPaymentDayOfMonth());
        variables.put("timeToCall", details.getTimeToCall());
        return variables;
    }

    private HashMap<String, String> commonVariables(OrderRecord orderRecord) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("trackingNumber", orderRecord.getTrackingNumber());
        variables.put("orderNumber", orderRecord.getOrderNum().toString());
        variables.put("dateFormatted", formatDate(orderRecord.getCreatedDate()));
        variables.put("paymentAmount", centsToDollars(orderRecord.getAmount()));
        variables.put("name", formatName(orderRecord.getFirstName(), orderRecord.getLastName()));
        variables.put("phone", orderRecord.getPhone());
        variables.put("mailAddress", formatMailAddress(orderRecord));
        return variables;
    }

    public void sendCancelledEmail(OrderRecord orderRecord) throws IOException {
        HashMap<String, String> variables = commonVariables(orderRecord);
        String paymentPlan = "IRS Payment Plan Application";

        if (orderRecord.getIsCalifornia() != null) {
            paymentPlan = "California Payment Plan Application";
        } else if (orderRecord.getIsNewJersey() != null) {
            paymentPlan = "New Jersey Payment Plan Application";
        } else if (orderRecord.getIsGeorgia() != null) {
            paymentPlan = "Georgia Payment Plan Application";
        } else if (orderRecord.getIsIllinois() != null) {
            paymentPlan = "Illinois Payment Plan Application";
        } else if (orderRecord.getIsMichigan() != null) {
            paymentPlan = "Michigan Payment Plan Application";
        }

        variables.put("paymentPlan", paymentPlan);
        String subject = "97tax.com Order " + orderRecord.getOrderNum() + " Cancelled";
        sendTemplateEmail(orderRecord.getEmail(), subject, orderCancelledTemplateId, variables);
    }

    public void sendInCompleteEmail(OrderRecord orderRecord) throws IOException {
        if (TaxConstants.PAYMENTPLAN.equals(orderRecord.getProduct())) {
            PaymentPlanDetails paymentPlan = paymentPlanRepo.findByOrderNum(orderRecord.getOrderNum());
            sendPaymentPlanIncompleteEmail(orderRecord, paymentPlan);
        } else if (TaxConstants.TAXLIEN_REMOVAL.equals(orderRecord.getProduct())) {
            sendTaxLienIncompleteEmail(orderRecord);
        }
    }

    public void sendPaymentPlanIncompleteEmail(OrderRecord orderRecord, PaymentPlanDetails details) {
        Boolean isCalifornia = orderRecord.getIsCalifornia();
        Boolean isNewJersey = orderRecord.getIsNewJersey();
        Boolean isGeorgia = orderRecord.getIsGeorgia();
        Boolean isIllinois = orderRecord.getIsIllinois();
        Boolean isMichigan = orderRecord.getIsMichigan();
        HashMap<String, String> variables = mapPaymentPlanIncompleteOrFailedEmailData(orderRecord, details);
        String subject = null;
        Integer templateId = null;

        if (isCalifornia != null && isCalifornia) {
            subject = "California Payment Plan Application Incomplete";
            templateId = californiaPaymentPlanIncompleteTemplateId;
        } else if (isNewJersey != null && isNewJersey) {
            subject = "New Jersey Payment Plan Application Incomplete";
            templateId = newJerseyPaymentPlanIncompleteTemplateId;
        } else if (isGeorgia != null && isGeorgia) {
            subject = "Georgia Payment Plan Application Incomplete";
            templateId = georgiaPaymentPlanIncompleteTemplateId;
        } else if (isIllinois != null && isIllinois) {
            subject = "Illinois Payment Plan Application Incomplete";
            templateId = illinoisPaymentPlanIncompleteTemplateId;
        } else if (isMichigan != null && isMichigan) {
            subject = "Michigan Payment Plan Application Incomplete";
            templateId = michiganPaymentPlanIncompleteTemplateId;
        } else {
            subject = "IRS Payment Plan Application Incomplete";
            templateId = paymentPlanIncompleteTemplateId;
        }

        sendTemplateEmail(orderRecord.getEmail(), subject, templateId, variables);
    }

    public void sendPaymentPlanFailedEmail(OrderRecord orderRecord, PaymentPlanDetails details) {
        Boolean isCalifornia = orderRecord.getIsCalifornia();
        Boolean isNewJersey = orderRecord.getIsNewJersey();
        Boolean isGeorgia = orderRecord.getIsGeorgia();
        Boolean isIllinois = orderRecord.getIsIllinois();
        Boolean isMichigan = orderRecord.getIsMichigan();
        HashMap<String, String> variables = mapPaymentPlanIncompleteOrFailedEmailData(orderRecord, details);
        String subject = null;
        Integer templateId = null;

        if (isCalifornia != null && isCalifornia) {
            subject = "California Payment Plan Application Failed";
            templateId = californiaPaymentPlanFailedTemplateId;
        } else if (isNewJersey != null && isNewJersey) {
            subject = "New Jersey Payment Plan Application Failed";
            templateId = newJerseyPaymentPlanFailedTemplateId;
        } else if (isGeorgia != null && isGeorgia) {
            subject = "Georgia Payment Plan Application Failed";
            templateId = georgiaPaymentPlanFailedTemplateId;
        } else if (isIllinois != null && isIllinois) {
            subject = "Illinois Payment Plan Application Failed";
            templateId = illinoisPaymentPlanFailedTemplateId;
        } else if (isMichigan != null && isMichigan) {
            subject = "Michigan Payment Plan Application Failed";
            templateId = michiganPaymentPlanFailedTemplateId;
        } else {
            subject = "IRS Payment Plan Application Failed";
            templateId = paymentPlanFailedTemplateId;
        }

        sendTemplateEmail(orderRecord.getEmail(), subject, templateId, variables);
    }

    public void sendTaxLienFailedEmail(OrderRecord orderRecord) {
    	HashMap<String, String> variables = mapPaymentPlanIncompleteOrFailedEmailData(orderRecord, null);

        String subject = "Tax Lien Removal Application Failed";

        sendTemplateEmail(orderRecord.getEmail(), subject, taxLienFailedTemplateId, variables);
    }

    public void sendTaxLienIncompleteEmail(OrderRecord orderRecord) {
    	HashMap<String, String> variables = mapPaymentPlanIncompleteOrFailedEmailData(orderRecord, null);

        String subject = "Tax Lien Removal Application Incomplete";

        sendTemplateEmail(orderRecord.getEmail(), subject, taxLienIncompleteTemplateId, variables);
    }
    public void sendOnHoldOrderEmail(OrderRecord orderRecord) {
    	 HashMap<String, String> variables = commonVariables(orderRecord);
    	 String subject = "97tax.com Order " + orderRecord.getOrderNum() + " On Hold";
    	 sendTemplateEmail(orderRecord.getEmail(), subject, orderOnHoldTemplateId, variables);
    }
    public void sendInvalidAddressShippingEmail(OrderRecord orderRecord, PaymentPlanDetails details) {
    	 HashMap<String, String> variables = mapPaymentPlanEmailData(orderRecord, details);
    	 String subject = "97tax.com Order " + orderRecord.getOrderNum() + " Invalid Address/RTS";
    	 sendTemplateEmail(orderRecord.getEmail(), subject, orderInvalidAddressTemplateId, variables);
    }
    public String getPaymentPlanName(OrderRecord orderRecord) {
    	 String paymentPlan = "IRS Payment Plan Application";
     	if (orderRecord.getIsCalifornia()) {
     	     paymentPlan = "California Payment Plan Application";
     	} else if (orderRecord.getIsNewJersey()) {
     	     paymentPlan = "New Jersey Payment Plan Application";
     	} else if (orderRecord.getIsGeorgia()) {
     	     paymentPlan = "Georgia Payment Plan Application";
     	} else if (orderRecord.getIsIllinois()) {
     	     paymentPlan = "Illinois Payment Plan Application";
     	} else if (orderRecord.getIsMichigan()) {
            paymentPlan = "Michigan Payment Plan Application";
        }

		return paymentPlan;
    }
    public void sendShippingPackageDeliveredEmail(OrderRecord orderRecord) {
    	HashMap<String, String> variables = new HashMap<>();
        variables.put("orderNumber", orderRecord.getOrderNum().toString());
        variables.put("dateFormatted", formatDate(orderRecord.getCreatedDate()));

    	variables.put("paymentPlan", getPaymentPlanName(orderRecord));
    	String subject = "97tax.com Order " + orderRecord.getOrderNum() + " Package Delivered";
    	sendTemplateEmail(orderRecord.getEmail(), subject, orderPackageDeliveredTemplateId, variables);
    }
    public void sendShippingEnvelopeDeliveredEmail(OrderRecord orderRecord) {
    	HashMap<String, String> variables = new HashMap<>();
        variables.put("orderNumber", orderRecord.getOrderNum().toString());
        variables.put("dateFormatted", formatDate(orderRecord.getCreatedDate()));

    	variables.put("paymentPlan", getPaymentPlanName(orderRecord));
    	String subject = "97tax.com Order " + orderRecord.getOrderNum() + " Envelope Delivered";
    	sendTemplateEmail(orderRecord.getEmail(), subject, orderEnvelopeDeliveredTemplateId, variables);
    }
    public void sendShippingPackageDelayedEmail(OrderRecord orderRecord, PaymentPlanDetails details) {
    	HashMap<String, String> variables = mapPaymentPlanEmailData(orderRecord, details);
    	variables.put("paymentPlan", getPaymentPlanName(orderRecord));
   	 	String subject = "97tax.com Order " + orderRecord.getOrderNum() + " Package Delayed";
   	 	sendTemplateEmail(orderRecord.getEmail(), subject, orderPackageDelayedTemplateId, variables);
    }
    public void sendEmailOfMailRoom(MailRoomUser mailRoomUser) {
    	HashMap<String, String> variables = new HashMap<String, String>();

    	String subject = "New MailRoom Notification";

    	sendTemplateEmail(mailRoomUser.getEmail(), mailRoomTemplateId, variables);
    }

    private HashMap<String, String> mapPaymentPlanIncompleteOrFailedEmailData(OrderRecord orderRecord, PaymentPlanDetails details) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("name", orderRecord.getFirstName());

        if (details != null) {
	        variables.put("totalDebtOwed", orderRecord.getTotalDebt().toPlainString());
	        variables.put("monthlyPaymentAmount", formatInteger(details.getMonthlyPayment()));
        }
        return variables;
    }

    private String formatMailAddress(OrderRecord orderRecord) {
        return orderRecord.getShippingAddress1()
                + (orderRecord.getShippingAddress2() != null ? " " + orderRecord.getShippingAddress2() : "")
                + ", "
                + orderRecord.getShippingCity() + ", "
                + orderRecord.getShippingState() + " "
                + orderRecord.getShippingZip();
    }

    private String formatName(String first, String last) {
        return first + " " + last;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("MM/dd/yyyy").format(date);
    }

    private String formatInteger(Integer amount) {
        if (amount != null) {
            return amount.toString();
        } else {
            return "";
        }
    }

    private String centsToDollars(String amount) {
        try {
            return new BigInteger(amount).divide(new BigInteger("100")).toString();
        } catch (Exception e) {
            LOG.error("Unable to format amount: "+amount, e);
            return "";
        }
    }


    public MailjetResponse sendTemplateEmail(String to, String subject, Integer templateId, Map<String,String> variables) {
        return sendTemplateEmail(Arrays.asList(to), subject, templateId, variables);
    }

    public MailjetResponse sendTemplateEmail(List<String> toList, String subject, Integer templateId, Map<String,String> variables) {
        MailjetClient client = new MailjetClient(authKey, authSecret, new ClientOptions("v3.1"));
        JSONObject message = new JSONObject();

        JSONObject variablesJson = new JSONObject();
        variables.entrySet().stream().forEach(e -> variablesJson.put(e.getKey(), e.getValue()));

        JSONArray recipients = new JSONArray();
        for (String to : toList) {
            recipients.put(new JSONObject()
                    .put(Emailv31.Message.EMAIL, to)
                    .put(Emailv31.Message.NAME, to));
        }

        message.put(Emailv31.Message.FROM,
                        new JSONObject()
                                .put(Emailv31.Message.EMAIL, fromEmail)
                                .put(Emailv31.Message.NAME, fromEmailName))
                .put(Emailv31.Message.TO, recipients)
                .put(Emailv31.Message.SUBJECT, subject)
                .put(Emailv31.Message.TEMPLATEID, templateId)
                .put(Emailv31.Message.TEMPLATELANGUAGE, true)
                .put(Emailv31.Message.VARIABLES, variablesJson);

        MailjetRequest email = new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, (new JSONArray()).put(message));

        try {
            MailjetResponse response = client.post(email);
            return response;
        } catch (Exception e) {
            LOG.error("Unable to send email! ", e);
            return null;
        }
    }


    public MailjetResponse sendTemplateEmail(String to, Integer templateId, Map<String,String> variables) {
        MailjetClient client = new MailjetClient(authKey, authSecret, new ClientOptions("v3.1"));
        JSONObject message = new JSONObject();

        JSONObject variablesJson = new JSONObject();
        variables.entrySet().stream().forEach(e -> variablesJson.put(e.getKey(), e.getValue()));

        message.put(
                Emailv31.Message.FROM,
                new JSONObject()
                        .put(Emailv31.Message.EMAIL, fromEmail)
                        .put(Emailv31.Message.NAME, fromEmailName)
        ).put(
                Emailv31.Message.TO,
                new JSONArray().put(new JSONObject()
                        .put(Emailv31.Message.EMAIL, to)
                        .put(Emailv31.Message.NAME, to))
        ).put(
                Emailv31.Message.TEMPLATEID,
                templateId
        ).put(
                Emailv31.Message.TEMPLATELANGUAGE,
                true
        ).put(
                Emailv31.Message.VARIABLES,
                variablesJson
        )
        ;

        MailjetRequest email = new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, (new JSONArray()).put(message));

        try {
            MailjetResponse response = client.post(email);
            return response;
        } catch (Exception e) {
            LOG.error("Unable to send email! ", e);
            return null;
        }
    }


    public void sendPenaltyOrderIncompleteEmail(PenaltyOrder penaltyOrder) {
        HashMap<String, String> variables = new HashMap<>();

        variables.put("name", penaltyOrder.getFirstName() + " " + penaltyOrder.getLastName());

        String subject = "Penalty Waiver Order " + penaltyOrder.getId() + " Incomplete";

        sendTemplateEmail(penaltyOrder.getEmail(), subject, penaltyWaiverIncompleteTemplateId, variables);
    }

    public void sendPenaltyOrderCancelledEmail(PenaltyOrder penaltyOrder) {
        HashMap<String, String> variables = new HashMap<>();

        String dateFormatted = new SimpleDateFormat("M/d/yyyy hh:mm:ss a").format(penaltyOrder.getCreatedDate());
        variables.put("orderNumber", penaltyOrder.getId().toString());
        variables.put("dateFormatted", dateFormatted);
        variables.put("paymentPlan", "Penalty Waiver Application");

        int paymentAmount = Integer.valueOf(penaltyOrder.getAmount())/100;
        variables.put("paymentAmount", String.valueOf(paymentAmount));

        String subject = "Penalty Waiver Order " + penaltyOrder.getId() + " Cancelled";

        sendTemplateEmail(penaltyOrder.getEmail(), subject, penaltyWaiverCancelledTemplateId, variables);
    }

    public void sendPenaltyOrderOnholdEmail(PenaltyOrder penaltyOrder) {
        HashMap<String, String> variables = new HashMap<>();

        variables.put("name", penaltyOrder.getFirstName() + " " + penaltyOrder.getLastName());
        variables.put("phone", penaltyOrder.getBillingPhone());
        variables.put("orderNumber", penaltyOrder.getId().toString());
        String dateFormatted = new SimpleDateFormat("M/d/yyyy hh:mm:ss a").format(penaltyOrder.getCreatedDate());
        variables.put("dateFormatted", dateFormatted);
        String address = penaltyOrder.getFullShippingAddress();
        variables.put("mailAddress", address);
        int paymentAmount = penaltyOrder.getAmount() != null ? Integer.valueOf(penaltyOrder.getAmount()) : 0;
        paymentAmount = paymentAmount / 100;
        variables.put("paymentAmount", String.valueOf(paymentAmount));


        String subject = "Penalty Waiver Order " + penaltyOrder.getId() + " On Hold";

        sendTemplateEmail(penaltyOrder.getEmail(), subject, penaltyWaiverOnholdTemplateId, variables);
    }

    public void sendPenaltyOrderFailedEmail(PenaltyOrder penaltyOrder) {
        HashMap<String, String> variables = new HashMap<>();

        String subject = "Penalty Waiver Order " + penaltyOrder.getId() + " Failed";

        sendTemplateEmail(penaltyOrder.getEmail(), subject, penaltyWaiverFailedTemplateId, variables);
    }

    public void sendPenaltyOrderProcessingEmail(PenaltyOrder penaltyOrder) {
        HashMap<String, String> variables = new HashMap<>();
        String dateFormatted = new SimpleDateFormat("M/d/yyyy hh:mm:ss a").format(penaltyOrder.getCreatedDate());
        int paymentAmount = Integer.valueOf(penaltyOrder.getAmount()) / 100;

        variables.put("orderNumber", penaltyOrder.getId().toString());
        variables.put("dateFormatted", dateFormatted);
        variables.put("paymentAmount", String.valueOf(paymentAmount));
        variables.put("name", penaltyOrder.getFirstName() + " " + penaltyOrder.getLastName());
        variables.put("phone", penaltyOrder.getBillingPhone());
        String address = penaltyOrder.getFullShippingAddress();
        variables.put("mailAddress", address);
        variables.put("totalDebtOwed", ""); // Check template, should not be used anymore
        variables.put("waivablePenaltyAmount", penaltyOrder.getPenaltyAmountWaived());
        variables.put("penaltyType", penaltyOrder.getPenaltyWaivedType());
        variables.put("penaltyYear", penaltyOrder.getPenaltyWaivedYear());

        String subject = "Penalty Waiver Order " + penaltyOrder.getId() + " Processing";

        sendTemplateEmail(penaltyOrder.getEmail(), subject, penaltyWaiverProcessingTemplateId, variables);
    }

    public void sendPenaltyOrderCompleteEmail(PenaltyOrder penaltyOrder) {
        HashMap<String, String> variables = new HashMap<>();
        String dateFormatted = new SimpleDateFormat("M/d/yyyy hh:mm:ss a").format(penaltyOrder.getCreatedDate());
        int paymentAmount = Integer.valueOf(penaltyOrder.getAmount()) / 100;
        String trackingNumber = penaltyOrder.getTrackingNumber();

        variables.put("trackingNumber", trackingNumber);
        variables.put("orderNumber", penaltyOrder.getId().toString());
        variables.put("dateFormatted", dateFormatted);
        variables.put("paymentAmount", String.valueOf(paymentAmount));
        variables.put("name", penaltyOrder.getFirstName() + " " + penaltyOrder.getLastName());
        variables.put("phone", penaltyOrder.getBillingPhone());
        String address = penaltyOrder.getFullShippingAddress();
        variables.put("mailAddress", address);
        variables.put("waivablePenaltyAmount", penaltyOrder.getPenaltyAmountWaived());
        variables.put("penaltyType", penaltyOrder.getPenaltyWaivedType());
        variables.put("penaltyYear", penaltyOrder.getPenaltyWaivedYear());

        String subject = "Penalty Waiver Order " + penaltyOrder.getId() + " Complete";

        sendTemplateEmail(penaltyOrder.getEmail(), subject, penaltyWaiverCompleteTemplateId, variables);
    }

    public void sendEinOnholdEmail(EinOrder einOrder) {
        HashMap<String, String> variables = new HashMap<>();

        variables.put("name", einOrder.getFirst_name() + " " + einOrder.getLast_name());
        variables.put("orderNumber", einOrder.getId().toString());

        String subject = "EIN Application Order " + einOrder.getId() + " On Hold";

        sendTemplateEmail(einOrder.getEmail(), subject, einOnholdTemplateId, variables);
    }

    public void sendEinAwaitingSignatureServiceEmail(EinOrder einOrder) {
        HashMap<String, String> variables = new HashMap<>();
        String docuSignPageURL = appURL + "/ein-docu-sign?order=" + einOrder.getId(); // + "&correlationId=" + einOrder.getCorrelationId();

        variables.put("name", einOrder.getFirst_name() + " " + einOrder.getLast_name());
        variables.put("orderNumber", einOrder.getId().toString());
        variables.put("docuSignURL", docuSignPageURL);
        String shortURL = docuSignPageURL;
//        String shortURL = UriComponentsBuilder.fromUriString(docuSignPageURL)
//                .replaceQuery(null)
//                .build(Collections.emptyMap()).toString();
        variables.put("docuSignURLShort", shortURL);
        variables.put("dateFormatted", formatDate(einOrder.getCreatedDate()));

        int paymentAmount = Integer.valueOf(einOrder.getAmount()) / 100;
        variables.put("paymentAmount", String.valueOf(paymentAmount));

        variables.put("phone", einOrder.getPhone_number());
        variables.put("mailAddress", einOrder.getEmail());
        variables.put("reasonForEIN", einOrder.getReason());
        variables.put("employeesEIN", einOrder.getIs_w2_employees() == 1 ? "true" : "false");

        String subject = "EIN Application Order " + einOrder.getId() + " Awaiting Signature";

        sendTemplateEmail(einOrder.getEmail(), subject, einAwaitingSignatureServiceTemplateId, variables);
    }

    public void sendEinFailedEmail(EinOrder einOrder) {
        HashMap<String, String> variables = new HashMap<>();

        String subject = "EIN Application Order " + einOrder.getId() + " Failed";

        sendTemplateEmail(einOrder.getEmail(), subject, einFailedTemplateId, variables);
    }

    public void sendEinOrderCancelledEmail(EinOrder einOrder) {
        HashMap<String, String> variables = new HashMap<>();

        variables.put("orderNumber", einOrder.getId().toString());
        variables.put("dateFormatted", formatDate(einOrder.getCreatedDate()));
        variables.put("paymentPlan", "EIN Application");

        int paymentAmount = Integer.valueOf(einOrder.getAmount()) / 100;
        variables.put("paymentAmount", String.valueOf(paymentAmount));

        String subject = "EIN Order " + einOrder.getId() + " Cancelled";

        sendTemplateEmail(einOrder.getEmail(), subject, einCancelledTemplateId, variables);
    }

    public void sendEinIncompleteEmail(EinOrder einOrder) {
        HashMap<String, String> variables = new HashMap<>();

        variables.put("name", einOrder.getFirst_name() + " " + einOrder.getLast_name());

        String subject = "EIN Application Order " + einOrder.getId() + " Incomplete";

        sendTemplateEmail(einOrder.getEmail(), subject, einIncompleteTemplateId, variables);
    }

    public void sendEinPaymentMissingEmail(EinOrder einOrder) {
        HashMap<String, String> variables = new HashMap<>();
        String paymentPageURL = appURL + "/ein-payment?order=" + einOrder.getId(); // + "&correlationId=" + einOrder.getCorrelationId();

        variables.put("name", einOrder.getFirst_name() + " " + einOrder.getLast_name());
        variables.put("paymentURL", paymentPageURL);
        String shortURL = paymentPageURL;
//        String shortURL = UriComponentsBuilder.fromUriString(paymentPageURL)
//                .replaceQuery(null)
//                .build(Collections.emptyMap()).toString();
        variables.put("paymentURLShort", shortURL);

        String subject = "EIN Application Order " + einOrder.getId() + " Payment Missing";

        sendTemplateEmail(einOrder.getEmail(), subject, einPaymentMissingTemplateId, variables);
    }

    public void sendEinOrderProcessingEmail(EinOrder einOrder) {
        HashMap<String, String> variables = new HashMap<>();

        variables.put("name", einOrder.getFirst_name() + " " + einOrder.getLast_name());
        variables.put("orderNumber", einOrder.getId().toString());
        variables.put("dateFormatted", formatDate(einOrder.getCreatedDate()));

        int paymentAmount = Integer.valueOf(einOrder.getAmount()) / 100;
        variables.put("paymentAmount", String.valueOf(paymentAmount));

        variables.put("phone", einOrder.getPhone_number());
        variables.put("mailAddress", einOrder.getEmail());
        variables.put("reasonForEIN", einOrder.getReason());
        variables.put("employeesEIN", einOrder.getIs_w2_employees() == 1 ? "true" : "false");

        String subject = "EIN Order " + einOrder.getId() + " Processing";

        sendTemplateEmail(einOrder.getEmail(), subject, einProcessingTemplateId, variables);
    }

    public void sendEinOrderCompleteEmail(EinOrder einOrder) {
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

        sendTemplateEmail(einOrder.getEmail(), subject, einCompleteTemplateId, variables);
    }

    public void sendRefundAlert(List<String> emails, String orderURL) {
        Map<String, String> variables = new HashMap<String, String>() {{
            put("orderURL", orderURL);
        }};

        String subject = "97tax ALERT: Manager Refund Required";
        sendTemplateEmail(emails, subject, refundAlertTemplateId, variables);
    }

}
