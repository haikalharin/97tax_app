package com.repnox.nineseventax.features.penalty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "penalty_order")
public class PenaltyOrder implements Serializable {

    public static final String STR_FTFP = "Failure to File";
    public static final String STR_FTPP = "Failure to Pay";
    public static final String STR_FTDP = "Failure to Deposit";

    private static final long serialVersionUID = -7225005247895796718L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ORDERNUM_GEN")
    @TableGenerator(name = "ORDERNUM_GEN", table = "ORDERNUM_GEN_TBL", pkColumnName = "ORDERNUM_GEN_NAME", valueColumnName = "ORDERNUM_GEN_VAL", pkColumnValue = "ORDERNUM_GEN_PK", initialValue = 40000, allocationSize = 1)
    private Long id;

    @Column(name = "fname")
    private String firstName;
    @Column(name = "lname")
    private String lastName;
    @Column(name = "email")
    private String email;
    private String billingPhone;
    private String billingAddress1;
    private String billingAddress2;
    private String billingCity;
    private String billingState;
    private String billingPostalCode;
    private String shippingAddress1;
    private String shippingAddress2;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String ssn;

    private Boolean married;
    private String shippingFirstName;
    private String shippingLastName;

    @Transient
    private String shippingPhone;
    private String largestPenalty;
    private String penaltyAmountWaived;
    private String penaltyWaivedYear;
    private String penaltyWaivedType;
    private Integer isPenaltyPaid;
    private Boolean isLast3Filed;
    private Boolean isLast3IrsWavied;
    private Boolean isDecreaseIncomeTax;
    private String partnerCode;

    @Column
    private String authorizeTransactionId;


    @Column
    private String authorizeResponseCode;

    @Column
    private String authorizeMessageCode;

    @Column
    private String authorizeDescription;

    @Column
    private String authorizeAuthCode;

    @Column
    private String authorizeErrorCode;

    @Column
    private String authorizeErrorMessage;

    @Transient
    private String cardNumber;
    @Transient
    private String cardExpMonth;
    @Transient
    private String cardExpYear;
    @Transient
    private String cardCvc;
    @Transient
    private Boolean ownBusiness;
    private String status;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date statusLastChanged;

    @Column
    private String correlationId;

    @Column
    private Boolean authenticationFailed;

    @Column
    private String spouseFirstname;

    @Column
    private String spouseLastname;

    @Column
    private String spouseSsn;

    @Column
    private String irsAddress1;

    @Column
    private String irsAddress2;

    @Column
    private String irsCity;

    @Column
    private String irsState;

    @Column
    private String irsZipcode;

    @Column
    private String notes;

    @Column
    private String trackingNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date trackingNumberEntry;

    @Column(name = "trans_type")
    private String transactionType;

    @Column(name = "retry_attempt_count")
    private String retryAttempCount;

    @Column(name = "inquiry_retry_number")
    private String inquiryRetryNumber;

    @Column(name = "orbital_transaction_id")
    private String orbitalTransactionId;

    @Column(name = "orbital_transaction_number")
    private String orbitalTransactionNumber;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date createdDate;

    @Column
    private String orderId;

    @Column
    private String amount;

    @Column
    Integer cbtype;

    public Boolean getAuthenticationFailed() {
        return authenticationFailed;
    }

    public void setAuthenticationFailed(Boolean authenticationFailed) {
        this.authenticationFailed = authenticationFailed;
    }

    @Column
    private Boolean captureFailed;

    public Boolean getCaptureFailed() {
        return captureFailed;
    }

    public void setCaptureFailed(Boolean captureFailed) {
        this.captureFailed = captureFailed;
    }

    @Column
    private String shipengineStatusCode;

    @Column(name = "processing_speed")
    private String processingSpeed;

    @Column(name = "refund_auth_trans_id")
    String refundAuthTransId;

    @Column(name = "address_status")
    private String addressStatus;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payload_response_status")
    private String payloadResponseStatus;

    @Column(name = "signature_verification")
    private String signatureVerification;

    @Column(name = "auth_cavv")
    private String authCavv;

    @Column(name = "auth_eci")
    private String authEci;

    @Column(name = "auth_xid")
    private String authXid;

    @Column(name = "enrolled")
    private String enrolled;

    @Column(name = "eci_flag")
    private String eciFlag;

    @Column(name = "customer_ip_address")
    private String customerIpAddress;

    @Column(name = "refund_date")
    private Date refundDate;

    @Column(name = "last_four_digits_card", columnDefinition = "VARCHAR", length = 10)
    private String last4DigitsCard;

    @Column
    private String cardBrand;

    public String getAuthorizeTransactionId() {
        return authorizeTransactionId;
    }

    public void setAuthorizeTransactionId(String authorizeTransactionId) {
        this.authorizeTransactionId = authorizeTransactionId;
    }

    public String getAuthorizeResponseCode() {
        return authorizeResponseCode;
    }

    public void setAuthorizeResponseCode(String authorizeResponseCode) {
        this.authorizeResponseCode = authorizeResponseCode;
    }

    public String getAuthorizeMessageCode() {
        return authorizeMessageCode;
    }

    public void setAuthorizeMessageCode(String authorizeMessageCode) {
        this.authorizeMessageCode = authorizeMessageCode;
    }

    public String getAuthorizeDescription() {
        return authorizeDescription;
    }

    public void setAuthorizeDescription(String authorizeDescription) {
        this.authorizeDescription = authorizeDescription;
    }

    public String getAuthorizeAuthCode() {
        return authorizeAuthCode;
    }

    public void setAuthorizeAuthCode(String authorizeAuthCode) {
        this.authorizeAuthCode = authorizeAuthCode;
    }

    public String getAuthorizeErrorCode() {
        return authorizeErrorCode;
    }

    public void setAuthorizeErrorCode(String authorizeErrorCode) {
        this.authorizeErrorCode = authorizeErrorCode;
    }

    public String getAuthorizeErrorMessage() {
        return authorizeErrorMessage;
    }

    public void setAuthorizeErrorMessage(String authorizeErrorMessage) {
        this.authorizeErrorMessage = authorizeErrorMessage;
    }

    public Date getStatusLastChanged() {
        return statusLastChanged;
    }

    public void setStatusLastChanged(Date statusLastChanged) {
        this.statusLastChanged = statusLastChanged;
    }

    public String getShippingFirstName() {
        return shippingFirstName;
    }

    public void setShippingFirstName(String shippingFirstName) {
        this.shippingFirstName = shippingFirstName;
    }

    public String getShippingLastName() {
        return shippingLastName;
    }

    public void setShippingLastName(String shippingLastName) {
        this.shippingLastName = shippingLastName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public Boolean getOwnBusiness() {
        return ownBusiness;
    }

    public void setOwnBusiness(Boolean ownBusiness) {
        this.ownBusiness = ownBusiness;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public Boolean getIsLast3Filed() {
        return isLast3Filed;
    }

    public void setIsLast3Filed(Boolean isLast3Filed) {
        this.isLast3Filed = isLast3Filed;
    }

    public Boolean getIsLast3IrsWavied() {
        return isLast3IrsWavied;
    }

    public void setIsLast3IrsWavied(Boolean isLast3IrsWavied) {
        this.isLast3IrsWavied = isLast3IrsWavied;
    }


    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.statusLastChanged = new Date();
    }

    public String getBillingAddress1() {
        return billingAddress1;
    }

    public void setBillingAddress1(String billingAddress1) {
        if (billingAddress1 != null) {
            this.billingAddress1 = billingAddress1;
        }
    }

    public String getBillingAddress2() {
        return billingAddress2;
    }

    public void setBillingAddress2(String billingAddress2) {
        if (billingAddress2 != null) {
            this.billingAddress2 = billingAddress2;
        }
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        if (billingCity != null) {
            this.billingCity = billingCity;
        }
    }

    public String getBillingState() {
        return billingState;
    }

    public String getBillingPhone() {
        return billingPhone;
    }

    public void setBillingPhone(String billingPhone) {
        this.billingPhone = billingPhone;
    }

    public void setBillingState(String billingState) {
        if (billingState != null) {
            this.billingState = billingState;
        }
    }

    public String getBillingPostalCode() {
        return billingPostalCode;
    }

    public void setBillingPostalCode(String billingPostalCode) {
        if (billingPostalCode != null) {
            this.billingPostalCode = billingPostalCode;
        }
    }

    public String getShippingAddress1() {
        return shippingAddress1;
    }

    public void setShippingAddress1(String shippingAddress1) {
        if (shippingAddress1 != null) {
            this.shippingAddress1 = shippingAddress1;
        }
    }

    public String getShippingAddress2() {
        return shippingAddress2;
    }

    public void setShippingAddress2(String shippingAddress2) {
        if (shippingAddress2 != null) {
            this.shippingAddress2 = shippingAddress2;
        }
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        if (shippingCity != null) {
            this.shippingCity = shippingCity;
        }
    }

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        if (shippingState != null) {
            this.shippingState = shippingState;
        }
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        if (shippingPostalCode != null) {
            this.shippingPostalCode = shippingPostalCode;
        }
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        if (ssn != null) {
            this.ssn = ssn;
        }
    }

    public Boolean getMarried() {
        return married;
    }

    public void setMarried(Boolean married) {

        this.married = married;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        if (cardNumber != null) {
            this.cardNumber = cardNumber;
        }
    }

    public String getCardExpMonth() {
        return cardExpMonth;
    }

    public void setCardExpMonth(String cardExpMonth) {
        if (cardExpMonth != null) {
            this.cardExpMonth = cardExpMonth;
        }
    }

    public String getCardExpYear() {
        return cardExpYear;
    }

    public void setCardExpYear(String cardExpYear) {
        if (cardExpYear != null) {
            this.cardExpYear = cardExpYear;
        }
    }

    public String getCardCvc() {
        return cardCvc;
    }

    public void setCardCvc(String cardCvc) {
        if (cardCvc != null) {
            this.cardCvc = cardCvc;
        }
    }

    public Boolean getIsDecreaseIncomeTax() {
        return isDecreaseIncomeTax;
    }

    public void setIsDecreaseIncomeTax(Boolean isDecreaseIncomeTax) {
        this.isDecreaseIncomeTax = isDecreaseIncomeTax;
    }

    public String getLargestPenalty() {
        return largestPenalty;
    }

    public void setLargestPenalty(String largestPenalty) {
        this.largestPenalty = largestPenalty;
    }

    private String toPenaltyTypeDescription(String type) {
        switch (type) {
            case "FTFP":
                return STR_FTFP;
            case "FTPP":
                return STR_FTPP;
            case "FTDP":
                return STR_FTDP;
            default:
                return "";
        }
    }

    private boolean checkOrderProcessing() {
        return StringUtils.isNotBlank(penaltyAmountWaived) &&
                StringUtils.isNotBlank(penaltyWaivedYear) &&
                StringUtils.isNotBlank(penaltyWaivedType);
    }

    public String getPenaltyTaxYear() {
        if (checkOrderProcessing()) {
            return penaltyWaivedYear;
        }
        return "";
    }

    public String getPenaltyType() {
        if (checkOrderProcessing()) {
            return toPenaltyTypeDescription(penaltyWaivedType);
        }
        return "-";
    }

    public String extractHighestPenalty() {
        if (checkOrderProcessing()) {
            return penaltyAmountWaived;
        }
        return "0";
    }

    public String getPenaltyAmountWaived() {
        return penaltyAmountWaived;
    }

    public void setPenaltyAmountWaived(String penaltyAmountWaived) {
        this.penaltyAmountWaived = penaltyAmountWaived;
    }

    public String getPenaltyWaivedYear() {
        return penaltyWaivedYear;
    }

    public void setPenaltyWaivedYear(String penaltyWaivedYear) {
        this.penaltyWaivedYear = penaltyWaivedYear;
    }

    public String getPenaltyWaivedType() {
        return penaltyWaivedType;
    }

    public void setPenaltyWaivedType(String penaltyWaivedType) {
        this.penaltyWaivedType = penaltyWaivedType;
    }

    public Integer getIsPenaltyPaid() {
        return isPenaltyPaid;
    }

    public void setIsPenaltyPaid(Integer isPenaltyPaid) {
        this.isPenaltyPaid = isPenaltyPaid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString() {
        return firstName + " " + lastName;
    }

    public String getSpouseFirstname() {
        return spouseFirstname;
    }

    public void setSpouseFirstname(String spouseFirstname) {
        this.spouseFirstname = spouseFirstname;
    }

    public String getSpouseLastname() {
        return spouseLastname;
    }

    public void setSpouseLastname(String spouseLastname) {
        this.spouseLastname = spouseLastname;
    }

    public String getSpouseSsn() {
        return spouseSsn;
    }

    public void setSpouseSsn(String spouseSsn) {
        this.spouseSsn = spouseSsn;
    }

    public String getIrsAddress1() {
        return irsAddress1 == null ? "" : irsAddress1;
    }

    public void setIrsAddress1(String irsAddress1) {
        this.irsAddress1 = irsAddress1;
    }

    public String getIrsAddress2() {
        return irsAddress2;
    }

    public void setIrsAddress2(String irsAddress2) {
        this.irsAddress2 = irsAddress2;
    }

    public String getIrsCity() {
        return irsCity == null ? "" : irsCity;
    }

    public void setIrsCity(String irsCity) {
        this.irsCity = irsCity;
    }

    public String getIrsState() {
        return irsState == null ? "" : irsState;
    }

    public void setIrsState(String irsState) {
        this.irsState = irsState;
    }

    public String getIrsZipcode() {
        return irsZipcode;
    }

    public void setIrsZipcode(String irsZipcode) {
        this.irsZipcode = irsZipcode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTrackingNumber() {
        if(trackingNumber == null)
            return "";
        else
            return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Date getTrackingNumberEntry() {
        return trackingNumberEntry;
    }

    public void setTrackingNumberEntry(Date trackingNumberEntry) {
        this.trackingNumberEntry = trackingNumberEntry;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getRetryAttempCount() {
        return retryAttempCount;
    }

    public void setRetryAttempCount(String retryAttempCount) {
        this.retryAttempCount = retryAttempCount;
    }

    public String getInquiryRetryNumber() {
        return inquiryRetryNumber;
    }

    public void setInquiryRetryNumber(String inquiryRetryNumber) {
        this.inquiryRetryNumber = inquiryRetryNumber;
    }

    public String getOrbitalTransactionId() {
        return orbitalTransactionId;
    }

    public void setOrbitalTransactionId(String orbitalTransactionId) {
        this.orbitalTransactionId = orbitalTransactionId;
    }

    public String getOrbitalTransactionNumber() {
        return orbitalTransactionNumber;
    }

    public void setOrbitalTransactionNumber(String orbitalTransactionNumber) {
        this.orbitalTransactionNumber = orbitalTransactionNumber;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getCbtype() {
        return cbtype;
    }

    public void setCbtype(Integer cbtype) {
        this.cbtype = cbtype;
    }

    public String getShipengineStatusCode() {
        return shipengineStatusCode;
    }

    public void setShipengineStatusCode(String shipengineStatusCode) {
        this.shipengineStatusCode = shipengineStatusCode;
    }

    public String getProcessingSpeed() {
        return processingSpeed;
    }

    public void setProcessingSpeed(String processingSpeed) {
        this.processingSpeed = processingSpeed;
    }

    public String getRefundAuthTransId() {
        return refundAuthTransId;
    }

    public void setRefundAuthTransId(String refundAuthTransId) {
        this.refundAuthTransId = refundAuthTransId;
    }

    public String getAddressStatus() {
        return addressStatus;
    }

    public void setAddressStatus(String addressStatus) {
        this.addressStatus = addressStatus;
    }

    public String getFullShippingAddress() {
        return getShippingAddress1() + ", " + getShippingCity() + ", " + getShippingState() + " " + getShippingPostalCode();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPayloadResponseStatus() {
        return payloadResponseStatus;
    }

    public void setPayloadResponseStatus(String payloadResponseStatus) {
        this.payloadResponseStatus = payloadResponseStatus;
    }

    public String getSignatureVerification() {
        return signatureVerification;
    }

    public void setSignatureVerification(String signatureVerification) {
        this.signatureVerification = signatureVerification;
    }

    public String getAuthCavv() {
        return authCavv;
    }

    public void setAuthCavv(String authCavv) {
        this.authCavv = authCavv;
    }

    public String getAuthEci() {
        return authEci;
    }

    public void setAuthEci(String authEci) {
        this.authEci = authEci;
    }

    public String getAuthXid() {
        return authXid;
    }

    public void setAuthXid(String authXid) {
        this.authXid = authXid;
    }

    public String getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(String enrolled) {
        this.enrolled = enrolled;
    }

    public String getEciFlag() {
        return eciFlag;
    }

    public void setEciFlag(String eciFlag) {
        this.eciFlag = eciFlag;
    }

    public String getCustomerIpAddress() {
        return customerIpAddress;
    }

    public void setCustomerIpAddress(String customerIpAddress) {
        this.customerIpAddress = customerIpAddress;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public String getLast4DigitsCard() {
        return last4DigitsCard;
    }

    public void setLast4DigitsCard(String last4DigitsCard) {
        this.last4DigitsCard = last4DigitsCard;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

}
