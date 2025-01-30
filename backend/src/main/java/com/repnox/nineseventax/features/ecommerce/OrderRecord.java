package com.repnox.nineseventax.features.ecommerce;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.repnox.nineseventax.features.efile.jpa.ValidationError;
import com.repnox.nineseventax.features.efile.jpa.ValidationErrorListConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRecord implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7225005246935796728L;

    public static final String STATUS_INCOMPLETE = "Incomplete";

    public static final String STATUS_CANCELLED = "Cancelled";

    public static final String STATUS_PROCESSING = "Processing";

    public static final String STATUS_ON_HOLD = "On Hold";

    public static final String STATUS_CHARGE_BACK = "Charge Back";

    public static final String STATUS_FAILED = "Failed";

    public static final String STATUS_COMPLETE = "Completed";

    public static final String STATUS_DELETED = "Deleted";

    public static final String STATUS_AWAITING_SIGNATURE_SERVICE = "Awaiting Signature";

    public static final String STATUS_SIGNED = "Signed";

    public static final String STATUS_BOT_ERROR = "Bot Error";

    public static final String COPY_REQUESTED = "copy requested";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ORDERNUM_GEN")
    @TableGenerator(name = "ORDERNUM_GEN", table = "ORDERNUM_GEN_TBL", pkColumnName = "ORDERNUM_GEN_NAME", valueColumnName = "ORDERNUM_GEN_VAL", pkColumnValue = "ORDERNUM_GEN_PK", initialValue = 40000, allocationSize = 1)
    private Long orderNum;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date submissionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date statusLastChanged;

    @Column
    private String status;


    @Column
    private String correlationId;

    @Column(name = "fname")
    private String firstName;

    @Column(name = "lname")
    private String lastName;

    @Column
    private String phone;

    @Column
    private String secondaryPhone;

    @Column
    private String email;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalDebt;

    @Column
    private String amount;

    @Column
    private String partnerCode;

    @Column
    private Boolean isCalifornia;

    @Column
    private Boolean isNewJersey;

    @Column
    private Boolean isGeorgia;

    @Column
    private Boolean isIllinois;

    @Column
    private Integer paymentMonths;

    @Column(name = "billaddr1")
    private String billingAddress1;

    @Column(name = "billaddr2")
    private String billingAddress2;

    @Column(name = "billcity")
    private String billingCity;

    @Column(name = "billstate")
    private String billingState;

    @Column(name = "billzip")
    private String billingZip;

    @Column(name = "shipaddr1")
    private String shippingAddress1;

    @Column(name = "shipaddr2")
    private String shippingAddress2;

    @Column(name = "shipcity")
    private String shippingCity;

    @Column(name = "shipstate")
    private String shippingState;

    @Column(name = "shipzip")
    private String shippingZip;

    @Column(name = "address_status")
    private String addressStatus;

    @Column(name = "employername")
    private String employerName;

    @Column(name = "employeraddress1")
    private String employerAddress1;

    @Column(name = "employeraddress2")
    private String employerAddress2;

    @Column(name = "employercity")
    private String employerCity;

    @Column(name = "employerstate")
    private String employerState;

    @Column(name = "employerzip")
    private String employerZip;

    @Column(name = "payfrequency")
    private Integer payFrequency;

    @Column(name = "employercontactname")
    private String employerContactName;

    @Column(name = "employercontactphonenumber")
    private String employerContactPhoneNumber;

    @Column(name = "payrolldeduction")
    private Boolean payrollDeduction;

    @Column
    private String cardBin;

    @Column
    private String threeDsVersion;

    @Column
    private String errorDesc;

    @Column
    private String acsUrl;

    @Column
    private String errorNo;

    @Column
    private String payloadRequest;

    @Lob
    @Column
    private String payloadResponse;

    @Column
    private String eciFlag;

    @Column
    private String enrolled;

    @Column
    private String transactionId;

    @Column
    private String orderId;

    @Column
    private String cardBrand;

    @Column
    private Boolean lookupFailed;

    @Column
    private String payloadResponseStatus;

    @Column
    private String signatureVerification;

    @Column
    private String authEci;

    @Column
    private String authXid;

    @Column
    private String authCavv;

    @Column
    private String authErrorNo;

    @Column
    private String authErrorDesc;

    @Column
    private Boolean authenticationFailed;

    @Column
    private String product;

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

    @Column
    private Boolean captureFailed;

    @Column
    private Boolean gotIrsFormQ;

    @Column
    private Boolean existingTaxLienQ;

    @Column
    private String ssn;

    @Column
    private String ein;

    @Column
    private String businessName;
    @Column
    private String dba;

    @Column
    private String illinoisAccountId;

    @Column
    private Integer goodFaithPayment;

    @Column
    private String mobile;

    @Column
    private Boolean isOwedFromBusiness = false;

    @Column
    private String refundAuthTransId;

    @Column
    private String trackingNumber;

    @Column
    private String shipengineStatusCode;

    @Column(name = "processing_speed")
    private String processingSpeed;

    @Column(name = "ref_number")
    private String refNumber;

    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date trackingNumberEntry;

    @Column(columnDefinition = "text")
    private String notes;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date notesEntry;

    @Column(name = "f9465_status")
    private String f9465Status;
    @Column(name = "f9465_manifest")
    private String f9465Manifest;
    @Column(name = "f9465_submission")
    private String f9465Submission;
    @Column(name = "f9465_ack_response")
    private String f9465AckResponse;
    @Column(name = "f9465_submission_id")
    private String f9465SubmissionId;
    @Column(name = "f9465_submission_date")
    private String f9465SubmissionDate;
    @Column(name = "f9465_error_codes")
    @Convert(converter = ValidationErrorListConverter.class)
    private List<ValidationError> f9465ErrorCodes;

    @Column
    private Long batch;

    @Column
    private String customerIpAddress;

    @Column(name = "upsell_product")
    private String upsellProduct;

    @Column(name = "upsell_clicked")
    private Integer upsellClicked = 0;

    @Column(name = "upsell_shown")
    private Integer upsellShown = 0;

    @Column(name = "phys_not_mailing")
    private Boolean physNotMailing = false;

    @Column(name = "busaddr1")
    private String businessAddress1;

    @Column(name = "busaddr2")
    private String businessAddress2;

    @Column(name = "buscity")
    private String businessCity;

    @Column(name = "busstate")
    private String businessState;

    @Column(name = "buszip")
    private String businessZip;

    @Column
    Integer cbtype;

    @Column(name = "has_old_address")
    private Boolean hasOldAddress;

    @Column(name = "old_address1")
    private String oldAddress1;

    @Column(name = "old_address2")
    private String oldAddress2;

    @Column(name = "old_city")
    private String oldCity;

    @Column(name = "old_state")
    private String oldState;

    @Column(name = "old_zip")
    private String oldZip;

    @Column(name = "has_prior_names")
    private Boolean hasPriorNames;

    @Column(name = "prior_names")
    private String priorNames;

    @Column(name = "trans_type")
    private String transactionType;

    @Column(name = "outstanding_amount")
    private BigDecimal outstandingAmount;

    @Column(name = "retry_attempt_count")
    private String retryAttempCount;

    @Column(name = "inquiry_retry_number")
    private String inquiryRetryNumber;

    @Column(name = "orbital_transaction_id")
    private String orbitalTransactionId;

    @Column(name = "orbital_transaction_number")
    private String orbitalTransactionNumber;

    @Column(name = "assessment_number")
    private String assessmentNumber;

    @Column(name = "treasury_account_number")
    private String treasuryAccountNumber;

    @Column(name = "is_michigan")
    private Boolean isMichigan;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_address1")
    private String bankAddress1;

    @Column(name = "bank_address2")
    private String bankAddress2;

    @Column(name = "bank_city")
    private String bankCity;

    @Column(name = "bank_state")
    private String bankState;

    @Column(name = "bank_zip")
    private String bankZip;

    @Column(name = "business_entity_type")
    private String businessEntityType;

    @OneToMany(mappedBy = "orderRecordId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Partner> partners;

    @Column(name = "refund_date")
    private Date refundDate;

    @Column(name = "last_four_digits_card", columnDefinition = "VARCHAR", length = 10)
    private String last4DigitsCard;

    @Column(name = "honeypot_millis")
    /**
     * DO NOT REMOVE!
     * This field is used as part of a honeypot anti-spam trap logic (IRS requirement).
     *
     * We will store here {@link System#currentTimeMillis()} every time record will be updated/created.
     * This way we will check that 2 seconds gone before `PUT /ecommerce/presave` called (first API
     * call on the checkout page) - to detect a spammer.
     */
    private Long lastUpdatedMillis;

    public void setStatus(String status) {
        this.status = status;
        this.statusLastChanged = new Date();
    }

    public void setIsOwedFromBusiness(Boolean isOwedFromBusiness) {
        if (isOwedFromBusiness != null) {
            this.isOwedFromBusiness = isOwedFromBusiness;
        }
    }
}
