package com.repnox.nineseventax.features.ecommerce;

import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;

import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import com.repnox.nineseventax.features.productprices.ProductPriceRepo;
import net.authorize.Environment;
import net.authorize.api.contract.v1.*;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.GetTransactionDetailsController;
import net.authorize.api.controller.base.ApiOperationBase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @deprecated
 */
@Component
@Deprecated
public class AuthorizeNetFacade {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizeNetFacade.class);

    @Value("${net.authorize.environment}")
    public String environment;

    @Value("${net.authorize.login.id}")
    public String loginId;

    @Value("${net.authorize.transaction.key}")
    public String transactionKey;

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    PenaltyRecordRepo penaltyRecordRepo;

    @Autowired
    private MailjetSender mailjetSender;

    private final ProductPriceRepo productPriceRepo;

    AuthorizeNetFacade(@Autowired ProductPriceRepo productPriceRepo)
    {
        this.productPriceRepo = productPriceRepo;
    }


    public CreateTransactionResponse refundTransaction(String transactionId, String cardNumber, String expirationDate,
                                                       BigDecimal amount, String invoiceNumber) {

        ApiOperationBase.setEnvironment(Environment.valueOf(environment));

        MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(loginId);
        merchantAuthenticationType.setTransactionKey(transactionKey);

        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType("refundTransaction");
        txnRequest.setRefTransId(transactionId);
        txnRequest.setAmount(amount);
        OrderType order = new OrderType();
        order.setInvoiceNumber(invoiceNumber);
        txnRequest.setOrder(order);

        PaymentType payment = new PaymentType();
        CreditCardType cred = new CreditCardType();
        cred.setCardNumber(cardNumber);
        cred.setExpirationDate(expirationDate);
        payment.setCreditCard(cred);
        txnRequest.setPayment(payment);

        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        apiRequest.setMerchantAuthentication(merchantAuthenticationType);
        apiRequest.setTransactionRequest(txnRequest);

        CreateTransactionController controller = new CreateTransactionController(apiRequest);
        controller.execute();

        CreateTransactionResponse response;
        response = controller.getApiResponse();

        if (response != null) {

            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
                return response;
            } else {
                return null;
            }
        } else {
            logger.info("Got null response.");
            return null;
        }
    }

    public CreateTransactionResponse voidTransaction(String transactionId) {
        ApiOperationBase.setEnvironment(Environment.valueOf(environment));

        MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(loginId);
        merchantAuthenticationType.setTransactionKey(transactionKey);

        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType("voidTransaction");
        txnRequest.setRefTransId(transactionId);

        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        apiRequest.setMerchantAuthentication(merchantAuthenticationType);
        apiRequest.setTransactionRequest(txnRequest);

        CreateTransactionController controller = new CreateTransactionController(apiRequest);
        controller.execute();

        CreateTransactionResponse response;
        response = controller.getApiResponse();

        if (response != null) {
            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
                //logObject(response, CreateTransactionResponse.class);
                return response;
            } else {
                //logObject(response, CreateTransactionResponse.class);
                return null;
            }
        } else {
            logger.info("Got null response.");
            return null;
        }
    }

    public GetTransactionDetailsResponse getTransactionDetails(String transactionId) {
        ApiOperationBase.setEnvironment(Environment.valueOf(environment));

        MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(loginId);
        merchantAuthenticationType.setTransactionKey(transactionKey);

        GetTransactionDetailsRequest getRequest = new GetTransactionDetailsRequest();
        getRequest.setMerchantAuthentication(merchantAuthenticationType);
        getRequest.setTransId(transactionId);

        //logObject(getRequest, GetTransactionDetailsRequest.class);

        GetTransactionDetailsController controller = new GetTransactionDetailsController(getRequest);
        controller.execute();
        GetTransactionDetailsResponse getResponse = controller.getApiResponse();

        //logObject(getResponse, GetTransactionDetailsResponse.class);

        if (getResponse != null) {
            if (getResponse.getMessages().getResultCode() == MessageTypeEnum.OK) {

                return getResponse;
            } else {
                //logObject(getResponse, CreateTransactionResponse.class);
                return null;
            }
        } else {
            logger.info("Got null response.");
            return null;
        }
    }
    
    public AuthorizeResult authorizeCard(OrderInfo orderInfo, OrderRecord orderRecord, String ipAddress) {

        AuthorizeResult authorizeResult = new AuthorizeResult();
        String orderId = orderRecord.getOrderId();
        String cardLast4Digit = orderInfo.getCardNumber()!=null ? orderInfo.getCardNumber().substring(orderInfo.getCardNumber().length() - 4) : "0000";

        String _loginId = loginId;
        String _transactionKey = transactionKey;
        Environment env = Environment.valueOf(environment);
        if ("4000000000000002".equals(orderInfo.getCardNumber())) {
        	env = Environment.SANDBOX;
            // If using a test card for Cardinal Commerce, switch to a test card for auth.net
            orderInfo.setCardNumber("4111111111111111");
        }
        else if ("5200000000000007".equals(orderInfo.getCardNumber())) {
        	env = Environment.SANDBOX;
            // If using a test card for Cardinal Commerce, switch to a test card for auth.net
            orderInfo.setCardNumber("5424000000000015");
        }
        if(env == Environment.SANDBOX) {
        	_loginId="522uKBEhc";
        	_transactionKey="99De3Jy939X4T4xD";
        }
        
    	ApiOperationBase.setEnvironment(env);
    	
        logger.info("Environment: " + env);
        
        MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(_loginId);
        merchantAuthenticationType.setTransactionKey(_transactionKey);

        PaymentType paymentType = new PaymentType();
        CreditCardType creditCard = new CreditCardType();
        creditCard.setCardNumber(orderInfo.getCardNumber().replaceAll("[^0-9]", ""));
        String twoDigitYear = orderInfo.getCardExpYear().replaceAll("[^0-9]", "").substring(orderInfo.getCardExpYear().length() - 2);
        String twoDigitMonth = orderInfo.getCardExpMonth().replaceAll("[^0-9]", "");
        if (twoDigitMonth.length() == 1) {
            twoDigitMonth = "0" + twoDigitMonth;
        }
        creditCard.setExpirationDate(twoDigitMonth + twoDigitYear);
        creditCard.setCardCode(orderInfo.getCardCvc().replaceAll("[^0-9]", ""));
        paymentType.setCreditCard(creditCard);

        CustomerDataType customer = new CustomerDataType();
        customer.setEmail(orderInfo.getEmail());
        if (orderInfo.getTaxLienIsBusiness() != Boolean.TRUE && StringUtils.isNotBlank(orderRecord.getSsn())) {
            customer.setTaxId(orderRecord.getSsn().replaceAll("[^0-9]", ""));
        } else if (orderInfo.getTaxLienIsBusiness() == Boolean.TRUE && StringUtils.isNotBlank(orderRecord.getEin())) {
            customer.setTaxId(orderRecord.getEin().replaceAll("[^0-9]", ""));
        }

        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        txnRequest.setPayment(paymentType);
        txnRequest.setCustomer(customer);

        SettingType txnSettingType = new SettingType();
        txnSettingType.setSettingName("duplicateWindow");
        txnSettingType.setSettingValue("10");
        ArrayOfSetting settings = new ArrayOfSetting();
        settings.getSetting().add(txnSettingType);
        txnRequest.setTransactionSettings(settings);

        CustomerAddressType billingInfo = new CustomerAddressType();
        billingInfo.setPhoneNumber(orderRecord.getPhone().replaceAll("[^0-9]", ""));
        billingInfo.setEmail(orderRecord.getEmail());
        billingInfo.setFirstName(orderRecord.getFirstName());
        billingInfo.setLastName(orderRecord.getLastName());
        billingInfo.setAddress(orderRecord.getBillingAddress1()
                + (orderRecord.getBillingAddress2() != null ? ", " + orderRecord.getBillingAddress2() : ""));
        billingInfo.setCity(orderRecord.getBillingCity());
        billingInfo.setState(orderRecord.getBillingState());
        billingInfo.setZip(orderRecord.getBillingZip());
        
        NameAndAddressType shippingInfo = new NameAndAddressType();
        shippingInfo.setFirstName(orderRecord.getFirstName());
        shippingInfo.setLastName(orderRecord.getLastName());
        shippingInfo.setAddress(orderRecord.getShippingAddress1()
                + (orderRecord.getShippingAddress2() != null ? ", " + orderRecord.getShippingAddress2() : ""));
        shippingInfo.setCity(orderRecord.getShippingCity());
        shippingInfo.setState(orderRecord.getShippingState());
        shippingInfo.setZip(orderRecord.getShippingZip());

        OrderType orderType = new OrderType();
        orderType.setInvoiceNumber(orderRecord.getOrderNum().toString());

        Boolean isCalifornia = orderRecord.getIsCalifornia();
        Boolean isNewJersey = orderRecord.getIsNewJersey();
        Boolean isGeorgia = orderRecord.getIsGeorgia();
        Boolean isIllinois=orderRecord.getIsIllinois();
        if ("PaymentPlan".equals(orderRecord.getProduct())) {
            if (isCalifornia != null && isCalifornia) {
                orderType.setDescription("California Tax Payment Plan");
            } else if (isNewJersey != null && isNewJersey) {
                orderType.setDescription("New Jersey Tax Payment Plan");
            } else if (isGeorgia != null && isGeorgia) {
                orderType.setDescription("Georgia Tax Payment Plan");
            }else if (isIllinois != null && isIllinois) {
                orderType.setDescription("Illinois Tax Payment Plan");
            } else {
                orderType.setDescription("IRS Payment Plan Application");
            }
        } else {
            orderType.setDescription("Tax Lien Removal Application");
        }

        txnRequest.setOrder(orderType);
        txnRequest.setBillTo(billingInfo);
        txnRequest.setShipTo(shippingInfo);
        txnRequest.setAmount(new BigDecimal(orderInfo.getAmount())
                .divide(new BigDecimal("100")).setScale(2, RoundingMode.CEILING));

//        if (!"4000000000000002".equals(orderInfo.getCardNumber())) {
        // CcAuthenticationType cardholderAuth = new CcAuthenticationType();
        // cardholderAuth.setAuthenticationIndicator(orderRecord.getAuthEci());
        // cardholderAuth.setCardholderAuthenticationValue(orderRecord.getAuthCavv());
        // txnRequest.setCardholderAuthentication(cardholderAuth);
//        } else {
//            txnRequest.setAuthCode("ROHNFQ");
//        }

        txnRequest.setCustomerIP(ipAddress);
        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        apiRequest.setMerchantAuthentication(merchantAuthenticationType);
        apiRequest.setTransactionRequest(txnRequest);

        //logObject(apiRequest, CreateTransactionRequest.class);
        CreateTransactionController controller = new CreateTransactionController(apiRequest);
        controller.execute();

        CreateTransactionResponse response;
        response = controller.getApiResponse();

        boolean isSuccess = false;
        String resultMessage = "";

        if (response != null) {
            TransactionResponse result = response.getTransactionResponse();
            String transactionResponseCode = result != null ? result.getResponseCode() : "";

            // If API Response is OK, go ahead and check the transaction response
            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
                orderRecord.setAuthorizeTransactionId(result.getTransId());
                orderRecord.setAuthorizeResponseCode(transactionResponseCode);
                orderRecord.setAuthorizeAuthCode(result.getAuthCode());

                if (transactionResponseCode.equals("1")) {
                    isSuccess = true;
                    resultMessage = "Card Transaction Approved!";
                } else if (transactionResponseCode.equals("2")) {
                    isSuccess = false;
                    resultMessage = "Your payment was declined by your bank. Please contact your bank to resolve this or use a different card and try again.";
                } else if (transactionResponseCode.equals("3")) {
                    isSuccess = false;
                    resultMessage = "Card Transaction Error!";
                } else if (transactionResponseCode.equals("4")) {
                    isSuccess = false;
                    resultMessage = "Card Transaction Held for Review!";
                } else {
                    isSuccess = false;
                    resultMessage = "Card Transaction Failed!";
                }
                if (isSuccess) {
                    logger.info("Successful Transaction");
                    orderRecord.setStatus(OrderRecord.STATUS_PROCESSING);
                    if (result.getMessages().getMessage().size() > 0) {
                        TransactionResponse.Messages.Message responseMessage = result.getMessages().getMessage().get(0);
                        orderRecord.setAuthorizeMessageCode(responseMessage.getCode());
                        orderRecord.setAuthorizeDescription(responseMessage.getDescription());
                    } else {
                        orderRecord.setAuthorizeDescription("<No Description>");
                    }

                    try {
                        mailjetSender.sendProcessingEmail(orderRecord);
                    } catch (Exception e) {
                        logger.error("Unable to send confirmation email ", e);
                    }
                } else {
                    logger.info("Failed Transaction");
                    orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                    if (result.getErrors() != null && result.getErrors().getError().size() > 0) {
                        TransactionResponse.Errors.Error error = response.getTransactionResponse().getErrors().getError().get(0);
                        String errorCode = error.getErrorCode();
                        orderRecord.setAuthorizeErrorCode(errorCode);
                        orderRecord.setAuthorizeErrorMessage(error.getErrorText());
                        if (errorCode.equals("2")) {
                            resultMessage = "Your payment was declined by your bank. Please contact your bank to resolve this or use a different card and try again.";
                        } else if (errorCode.equals("44") || errorCode.equals("65")) {
                            resultMessage = "The 3 digit card code on the back of your card you entered is incorrect. Please correct the card code.";
                        } else {
                            resultMessage = error.getErrorText();
                        }
                    } else {
                        orderRecord.setAuthorizeDescription("<Unknown Error>");
                    }
                }
            } else {
                logger.info("Failed Transaction");
                orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                if (result != null && result.getErrors() != null) {
                    TransactionResponse.Errors.Error error = response.getTransactionResponse().getErrors().getError().get(0);
                    orderRecord.setAuthorizeErrorCode(error.getErrorCode());
                    orderRecord.setAuthorizeErrorMessage(error.getErrorText());
                    resultMessage = error.getErrorText();
                } else if (response.getMessages() != null && response.getMessages().getMessage().size() > 0) {
                    MessagesType.Message message = response.getMessages().getMessage().get(0);
                    orderRecord.setAuthorizeMessageCode(message.getCode());
                    orderRecord.setAuthorizeDescription(message.getText());
                    resultMessage = message.getText();
                } else {
                    orderRecord.setAuthorizeDescription("<Failed Transaction>");
                    resultMessage = "Card Transaction Failed!";
                }
                isSuccess = false;
            }
        } else {
            logger.info("No Response");
            if (controller.getResults().size() > 0) {
                for (String result : controller.getResults()) {
                    logger.error(result);
                }
            }
            orderRecord.setStatus(OrderRecord.STATUS_FAILED);
            ANetApiResponse errorResponse = controller.getErrorResponse();
            if (errorResponse.getMessages() != null && !errorResponse.getMessages().getMessage().isEmpty()) {
                MessagesType.Message message = errorResponse.getMessages().getMessage().get(0);
                orderRecord.setAuthorizeErrorCode(message.getCode());
                orderRecord.setAuthorizeErrorMessage(message.getText());
            } else {
                orderRecord.setAuthorizeDescription("<No Response>");
            }
            isSuccess = false;
            resultMessage = "No Response from Bank!";
        }

        orderRecord.setCaptureFailed(!isSuccess);
        orderRecordRepo.save(orderRecord);

        authorizeResult.setSuccess(isSuccess);
        authorizeResult.setMessage(resultMessage);

        return authorizeResult;
    }


    public AuthorizeResult authorizeCard(PenaltyOrder orderRecord, String ipAddress) {

        AuthorizeResult authorizeResult = new AuthorizeResult();
        String orderId = orderRecord.getId().toString();
        String cardLast4Digit = orderRecord.getCardNumber()!=null ? orderRecord.getCardNumber().substring(orderRecord.getCardNumber().length() - 4) : "0000";

        String _loginId = loginId;
        String _transactionKey = transactionKey;
        Environment env = Environment.valueOf(environment);
        if ("4000000000000002".equals(orderRecord.getCardNumber())) {
        	env = Environment.SANDBOX;
            // If using a test card for Cardinal Commerce, switch to a test card for auth.net
        	orderRecord.setCardNumber("4111111111111111");
        }
        else if ("5200000000000007".equals(orderRecord.getCardNumber())) {
        	env = Environment.SANDBOX;
            // If using a test card for Cardinal Commerce, switch to a test card for auth.net
        	orderRecord.setCardNumber("5424000000000015");
        }
        if(env == Environment.SANDBOX) {
        	_loginId="522uKBEhc";
        	_transactionKey="99De3Jy939X4T4xD";
        }
        
    	ApiOperationBase.setEnvironment(env);
    	
        logger.info("Environment: " + env);
        
        MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(_loginId);
        merchantAuthenticationType.setTransactionKey(_transactionKey);

        PaymentType paymentType = new PaymentType();
        CreditCardType creditCard = new CreditCardType();
        creditCard.setCardNumber(orderRecord.getCardNumber().replaceAll("[^0-9]", ""));
        String twoDigitYear = orderRecord.getCardExpYear().replaceAll("[^0-9]", "").substring(orderRecord.getCardExpYear().length() - 2);
        String twoDigitMonth = orderRecord.getCardExpMonth().replaceAll("[^0-9]", "");
        if (twoDigitMonth.length() == 1) {
            twoDigitMonth = "0" + twoDigitMonth;
        }
        creditCard.setExpirationDate(twoDigitMonth + twoDigitYear);
        creditCard.setCardCode(orderRecord.getCardCvc().replaceAll("[^0-9]", ""));
        paymentType.setCreditCard(creditCard);

        CustomerDataType customer = new CustomerDataType();
        customer.setEmail(orderRecord.getEmail());

        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        txnRequest.setPayment(paymentType);
        txnRequest.setCustomer(customer);

        SettingType txnSettingType = new SettingType();
        txnSettingType.setSettingName("duplicateWindow");
        txnSettingType.setSettingValue("10");
        ArrayOfSetting settings = new ArrayOfSetting();
        settings.getSetting().add(txnSettingType);
        txnRequest.setTransactionSettings(settings);

        CustomerAddressType billingInfo = new CustomerAddressType();
        billingInfo.setPhoneNumber(orderRecord.getBillingPhone().replaceAll("[^0-9]", ""));
        billingInfo.setEmail(orderRecord.getEmail());
        billingInfo.setFirstName(orderRecord.getFirstName());
        billingInfo.setLastName(orderRecord.getLastName());
        billingInfo.setAddress(orderRecord.getBillingAddress1()
                + (orderRecord.getBillingAddress2() != null ? ", " + orderRecord.getBillingAddress2() : ""));
        billingInfo.setCity(orderRecord.getBillingCity());
        billingInfo.setState(orderRecord.getBillingState());
        
        NameAndAddressType shippingInfo = new NameAndAddressType();
        shippingInfo.setFirstName(orderRecord.getFirstName());
        shippingInfo.setLastName(orderRecord.getLastName());
        shippingInfo.setAddress(orderRecord.getShippingAddress1()
                + (orderRecord.getShippingAddress2() != null ? ", " + orderRecord.getShippingAddress2() : ""));
        shippingInfo.setCity(orderRecord.getShippingCity());
        shippingInfo.setState(orderRecord.getShippingState());

//        OrderType orderType = new OrderType();
//        orderType.setInvoiceNumber(orderRecord.getId().toString());
//
//        
//
//        txnRequest.setOrder(orderType);
        txnRequest.setBillTo(billingInfo);
        txnRequest.setShipTo(shippingInfo);
        txnRequest.setAmount(new BigDecimal(productPriceRepo.findById(11L).get().getCurrentPrice()).setScale(2, RoundingMode.CEILING));

//        if (!"4000000000000002".equals(orderInfo.getCardNumber())) {
        // CcAuthenticationType cardholderAuth = new CcAuthenticationType();
        // cardholderAuth.setAuthenticationIndicator(orderRecord.getAuthEci());
        // cardholderAuth.setCardholderAuthenticationValue(orderRecord.getAuthCavv());
        // txnRequest.setCardholderAuthentication(cardholderAuth);
//        } else {
//            txnRequest.setAuthCode("ROHNFQ");
//        }

        txnRequest.setCustomerIP(ipAddress);
        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        apiRequest.setMerchantAuthentication(merchantAuthenticationType);
        apiRequest.setTransactionRequest(txnRequest);

        //logObject(apiRequest, CreateTransactionRequest.class);
        CreateTransactionController controller = new CreateTransactionController(apiRequest);
        controller.execute();

        CreateTransactionResponse response;
        response = controller.getApiResponse();

        boolean isSuccess = false;
        String resultMessage = "";

        if (response != null) {
            TransactionResponse result = response.getTransactionResponse();
            String transactionResponseCode = result != null ? result.getResponseCode() : "";

            // If API Response is OK, go ahead and check the transaction response
            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
                orderRecord.setAuthorizeTransactionId(result.getTransId());
                orderRecord.setAuthorizeResponseCode(transactionResponseCode);
                orderRecord.setAuthorizeAuthCode(result.getAuthCode());


                if (transactionResponseCode.equals("1")) {
                    isSuccess = true;
                    resultMessage = "Card Transaction Approved!";
                } else if (transactionResponseCode.equals("2")) {
                    isSuccess = false;
                    resultMessage = "Your payment was declined by your bank. Please contact your bank to resolve this or use a different card and try again.";
                } else if (transactionResponseCode.equals("3")) {
                    isSuccess = false;
                    resultMessage = "Card Transaction Error!";
                } else if (transactionResponseCode.equals("4")) {
                    isSuccess = false;
                    resultMessage = "Card Transaction Held for Review!";
                } else {
                    isSuccess = false;
                    resultMessage = "Card Transaction Failed!";
                }
                if (isSuccess) {
                    logger.info("Successful Transaction");
                    orderRecord.setStatus(OrderRecord.STATUS_PROCESSING);
                    if (result.getMessages().getMessage().size() > 0) {
                        TransactionResponse.Messages.Message responseMessage = result.getMessages().getMessage().get(0);
                        orderRecord.setAuthorizeMessageCode(responseMessage.getCode());
                        orderRecord.setAuthorizeDescription(responseMessage.getDescription());
                    } else {
                        orderRecord.setAuthorizeDescription("<No Description>");
                    }

                    try {
//                        mailjetSender.sendProcessingEmail(orderRecord);
                    } catch (Exception e) {
                        logger.error("Unable to send confirmation email ", e);
                    }
                } else {
                    logger.info("Failed Transaction");
                    orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                    if (result.getErrors() != null && result.getErrors().getError().size() > 0) {
                        TransactionResponse.Errors.Error error = response.getTransactionResponse().getErrors().getError().get(0);
                        String errorCode = error.getErrorCode();
                        orderRecord.setAuthorizeErrorCode(errorCode);
                        orderRecord.setAuthorizeErrorMessage(error.getErrorText());
                        if (errorCode.equals("2")) {
                            resultMessage = "Your payment was declined by your bank. Please contact your bank to resolve this or use a different card and try again.";
                        } else if (errorCode.equals("44") || errorCode.equals("65")) {
                            resultMessage = "The 3 digit card code on the back of your card you entered is incorrect. Please correct the card code.";
                        } else {
                            resultMessage = error.getErrorText();
                        }
                    } else {
                        orderRecord.setAuthorizeDescription("<Unknown Error>");
                    }
                }
            } else {
                logger.info("Failed Transaction");
                orderRecord.setStatus(OrderRecord.STATUS_FAILED);
                if (result != null && result.getErrors() != null) {
                    TransactionResponse.Errors.Error error = response.getTransactionResponse().getErrors().getError().get(0);
                    orderRecord.setAuthorizeErrorCode(error.getErrorCode());
                    orderRecord.setAuthorizeErrorMessage(error.getErrorText());
                    resultMessage = error.getErrorText();
                } else if (response.getMessages() != null && response.getMessages().getMessage().size() > 0) {
                    MessagesType.Message message = response.getMessages().getMessage().get(0);
                    orderRecord.setAuthorizeMessageCode(message.getCode());
                    orderRecord.setAuthorizeDescription(message.getText());
                    resultMessage = message.getText();
                } else {
                    orderRecord.setAuthorizeDescription("<Failed Transaction>");
                    resultMessage = "Card Transaction Failed!";
                }
                isSuccess = false;
            }
        } else {
            logger.info("No Response");
            if (controller.getResults().size() > 0) {
                for (String result : controller.getResults()) {
                    logger.error(result);
                }
            }
            orderRecord.setStatus(OrderRecord.STATUS_FAILED);
            ANetApiResponse errorResponse = controller.getErrorResponse();
            if (errorResponse.getMessages() != null && !errorResponse.getMessages().getMessage().isEmpty()) {
                MessagesType.Message message = errorResponse.getMessages().getMessage().get(0);
                orderRecord.setAuthorizeErrorCode(message.getCode());
                orderRecord.setAuthorizeErrorMessage(message.getText());
            } else {
                orderRecord.setAuthorizeDescription("<No Response>");
            }
            isSuccess = false;
            resultMessage = "No Response from Bank!";	
        }

        orderRecord.setCaptureFailed(!isSuccess);
        penaltyRecordRepo.save(orderRecord);

        authorizeResult.setSuccess(isSuccess);
        authorizeResult.setMessage(resultMessage);

        return authorizeResult;
    }

    private void logObject(Object object, Class<?> type) {
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(type);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(object, stringWriter);
            logger.info(stringWriter.toString());

        } catch (JAXBException e) {
            logger.error("Trying to log " + type + " from authorize.net", e);
        }
    }

}
