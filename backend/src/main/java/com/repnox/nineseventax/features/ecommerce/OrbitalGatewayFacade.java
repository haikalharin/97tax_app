package com.repnox.nineseventax.features.ecommerce;

import com.repnox.nineseventax.features.ecommerce.chaseorbital.ChaseOrbitalGatewayServiceImpl;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.CustomerObject;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.OrbitalResponse;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class OrbitalGatewayFacade {

    private static final Logger logger = LoggerFactory.getLogger(OrbitalGatewayFacade.class);

    @Value("${net.authorize.environment}")
    public String environment;

    @Value("${net.authorize.login.id}")
    public String loginId;

    @Value("${net.authorize.transaction.key}")
    public String transactionKey;

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private MailjetSender mailjetSender;

    public OrbitalResponse refundTransaction(String customerRef, String transactionId) throws Exception {
        ValidationUtil.isNotBlank(customerRef, "Customer reference ID could not be blank.");
        ValidationUtil.isNotBlank(transactionId, "Transaction ID could not be blank.");
        try {
            OrderRecord optRecord = orderRecordRepo.findByAuthorizeTransactionId(transactionId);
            ValidationUtil.isNotNull(optRecord, "Could not found transaction with id " + transactionId);
            ValidationUtil.isNotNull(optRecord.getAmount(), "Could not refund transaction if amount is null. Transaction ID " + transactionId);

            Double amount = Double.parseDouble(optRecord.getAmount());

            ChaseOrbitalGatewayServiceImpl gatew = new ChaseOrbitalGatewayServiceImpl(); 
            gatew.initializeService(null);
           // OrbitalResponse refundTransactionResponse = gatew.refundPayment(customerRef, amount.doubleValue());
            return null;
        } catch (Exception e) {
            logger.error("ERROR: Refund Transaction: ", e);
        }
        return null;
    }

    public OrbitalResponse voidTransaction(String transactionId) {
        return null;
    }

    public AuthorizeResult authorizeCard(OrderInfo orderInfo, OrderRecord orderRecord, String ipAddress) {
        AuthorizeResult authorizeResult = new AuthorizeResult();
        boolean isSuccess = false;
        String resultMessage = "";
        try {
            ChaseOrbitalGatewayServiceImpl gatew = new ChaseOrbitalGatewayServiceImpl();
            gatew.initializeService(null);
            CustomerObject customerObject = new CustomerObject();
            customerObject.setAddress1(orderInfo.getBillingAddress1() != null ? orderInfo.getBillingAddress1() : "");
            customerObject.setAddress2(orderInfo.getBillingAddress2() != null ? orderInfo.getBillingAddress2() : "");
            customerObject.setCity(orderInfo.getBillingCity() != null ? orderInfo.getBillingCity() : "");
            customerObject.setEmail(orderInfo.getEmail() != null ? orderInfo.getEmail() : "");
            String name = orderInfo.getBillingFirstName() + " " + orderInfo.getBillingLastName();
            customerObject.setName(name);
            customerObject.setPhone(orderInfo.getBillingPhone() != null ? orderInfo.getBillingPhone() : "");
            customerObject.setStateAbbr(orderInfo.getBillingState() != null ? orderInfo.getBillingState() : "");
            customerObject.setCity(orderInfo.getBillingCity() != null ? orderInfo.getBillingCity() : "");
            customerObject.setZipCode(orderInfo.getBillingPostalCode() != null ? orderInfo.getBillingPostalCode() : "");
            customerObject.setCreditCardNumber(orderInfo.getCardNumber());
            customerObject.setExpireDateMMYY(orderInfo.getCardExpYear() + "" + orderInfo.getCardExpMonth());

			OrbitalResponse responseCreateCustomer = gatew.CreateProfile(customerObject);

            
            if (responseCreateCustomer.getIsGood() == true) {
                String customerRefNumber = responseCreateCustomer.getCustomerRefNum();
                orderRecord.setRefNumber(customerRefNumber);
                BigDecimal amount = (new BigDecimal(orderInfo.getAmount()).divide(new BigDecimal("100")).setScale(2, RoundingMode.CEILING));

                OrbitalResponse authResponse = gatew.AuthorizeAndCaptureWithSecureInfo(customerRefNumber, amount.doubleValue(), "222", orderRecord);
                logger.info("Error: " + authResponse.getMessage());
                if (authResponse.getIsError()) {
                }
    
                logger.info(" ORDERID + " +  authResponse.getOrderId() + " Amount redeemed=" + authResponse.getAmountRedeemedNoDecimal() + " Amount requested =" + authResponse.getAmountRequestedNoDecimal() + " Remaining bal =" + authResponse.getRemainingBalanceNoDecimal() );
                logger.info("GatewayReturnObject response (from AuthorizeAndCapture): " + authResponse.toString());
                logger.info("------------------------------------------------------------");
    
    
                String responseCode = authResponse.getResponseCode();
                orderRecord.setAuthorizeTransactionId(authResponse.getTxRefNum());
                orderRecord.setAuthorizeResponseCode(responseCode);
                orderRecord.setAuthorizeAuthCode(authResponse.getAuthCode());
                if (responseCode != null && responseCode.equals("00")) {
                    isSuccess = true;
                    resultMessage = "Card Transaction Approved";
    
                } else {
                    isSuccess = false;
                    resultMessage = authResponse.getMessage();
                }
                if ( isSuccess ) {
                    logger.info("Successful Transcation");
                    orderRecord.setStatus(OrderRecord.STATUS_PROCESSING);
                    if (authResponse.getMessage().length() > 0) {
                        orderRecord.setAuthorizeMessageCode(authResponse.getAuthCode());
                        orderRecord.setAuthorizeDescription(authResponse.getMessage());
                    } else {
                        orderRecord.setAuthorizeDescription("<No Description>");
                    }
                    try {
                        mailjetSender.sendProcessingEmail(orderRecord);
                    } catch (Exception e) {
                        logger.error("Unable to send confirmation email ", e);
                    }
                } else {
                    logger.error("Failed Transaction");
                    orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                    String errorCode = authResponse.getResponseCode();
                    orderRecord.setAuthorizeErrorCode(errorCode);
                    orderRecord.setAuthorizeErrorMessage(authResponse.getMessage());
                    if (authResponse.getMessage().length() <1) {
                        orderRecord.setAuthorizeDescription("<Unknown Error>");
                    }

                }
            } else {
                logger.info("Failed Transaction while Creating Profile");
                orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                if (responseCreateCustomer != null && responseCreateCustomer.getResponseCode() != null) {
                    orderRecord.setAuthorizeErrorCode(responseCreateCustomer.getResponseCode());
                    orderRecord.setAuthorizeErrorMessage(responseCreateCustomer.getMessage());
                    resultMessage = responseCreateCustomer.getMessage();
                } else {
                    orderRecord.setAuthorizeDescription("<Failed Transaction>");
                    resultMessage = "Card Transaction Failed!";
                }
                isSuccess = false;
            }

        } catch (Exception e) {
            logger.error("Aturhoize Card", e);
        }

        orderRecord.setCaptureFailed(!isSuccess);
        orderRecordRepo.save(orderRecord);

        authorizeResult.setSuccess(isSuccess);
        authorizeResult.setMessage(resultMessage);

        return authorizeResult;
    }
}
