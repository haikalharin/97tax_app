package com.repnox.nineseventax.features.ecommerce;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = -3196476914445807712L;

    /**
     * DO NOT REMOVE!
     * This field is used as part of a honeypot anti-spam trap logic (IRS requirement).
     *
     * Hidden input value, as soon as it is filed with some data - we do assume there is a spammer
     * and block it with 555 error.
     */
    private String confirmation;

    private String id;
    private String version;
    private String msgType;
    private String processorId;
    private String merchantId;
    private String transactionPwd;
    private String transactionType;
    private String userAgent;
    private String browserHeader;
    private String ipAddress;
    private String orderNumber;
    private String orderDescription;
    private String amount;
    private String partnerCode;
    private BigDecimal totalDebt;
    private String currencyCode;
    private String shippingAmount;
    private String taxAmount;
    private String giftCardAmount;
    private String recurring;
    private String orderChannel;
    private String productCode;
    private String transactionMode;
    private String categoryCode;
    private String merchantData;
    private String billingFirstName;
    private String billingMiddleName;
    private String billingLastName;
    private String billingAddress1;
    private String billingAddress2;
    private String billingCity;
    private String billingState;
    private String billingPostalCode;
    private String billingCountryCode;
    private String billingPhone;
    private String secondaryPhone;
    private String shippingFirstName;
    private String shippingMiddleName;
    private String shippingLastName;
    private String shippingAddress1;
    private String shippingAddress2;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountryCode;
    private String shippingPhone;
    private String employerName;
    private String employerAddress1;
    private String employerAddress2;
    private String employerCity;
    private String employerState;
    private String employerZip;
    private int payFrequency;
    private String employerContactName;
    private String employerContactPhoneNumber;
    private Boolean payrollDeduction;
    private String email;
    private String item_Name_1;
    private String item_Desc_1;
    private String item_SKU_1;
    private String item_Price_1;
    private String item_Quantity_1;
    private String cardNumber;
    private String cardCvc;
    private String cardExpMonth;
    private String cardExpYear;
    private String recurringFrequency;
    private String recurringEnd;
    private String installment;
    private String acquirerPassword;
    private String product;
    private Boolean taxLienIsBusiness;
    private String taxLienType;
    private String serialNumber;
    private String taxLienBusinessName;
    private String taxLienBusinessEin;
    private String taxLienRemediationType;
    private String taxLienRemediationDescription;
    private Boolean taxLienAutomaticDebit;
    private Boolean isCalifornia;
    private Boolean isGeorgia;
    private Boolean isIllinois;
    private Boolean isNewJersey;
    private Boolean isMichigan;
    private Integer paymentMonths;
    private Boolean isOwedFromBusiness;
    private String businessName;
    private String ein;
    private String dba;
    private String illinoisAccountId;
    private Integer goodFaithPayment;
    private String mobile;
    private String filingJointly;
    private String ssn;
    private String married;
    private String paymentDayOfMonth;
    private String spouseFirstName;
    private String spouseLastName;
    private String spouseSsn;
    private String timeToCall;
    private String responseJwt;
    private String failureReason;
    private Date submissionDate;
    private String status;
    private String upsellProduct;
    private Integer upsellClicked;
    private Integer upsellShown;
    private Boolean physNotMailing;
    private String businessAddress1;
    private String businessAddress2;
    private String businessCity;
    private String businessState;
    private String businessPostalCode;
    private String processingSpeed;
    private Boolean hasOldAddress;
    private String oldAddress1;
    private String oldAddress2;
    private String oldCity;
    private String oldState;
    private String oldZip;
    private Boolean hasPriorNames;
    private String priorNames;
    private String bankName;
    private String bankAddress1;
    private String bankAddress2;
    private String bankCity;
    private String bankState;
    private String bankZip;
    private List<String> assessmentNumbers;
    private String treasuryAccountNumber;
    private List<PartnerInfo> partners;
    private Boolean hasBusinessPartner;
    private String businessEntityType;
    private Boolean isPenaltyWaiver;
    private String firstName;
    private String lastName;
    private String eciNumber;
    private String cardBrand;
    private String authCavv;

}
