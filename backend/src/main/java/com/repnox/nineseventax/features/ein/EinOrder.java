package com.repnox.nineseventax.features.ein;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "ein_orders")
@Getter
@Setter
public class EinOrder {
    private static final long serialVersionUID = -7225005247895796718L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ORDERNUM_GEN")
    @TableGenerator(name = "ORDERNUM_GEN", table = "ORDERNUM_GEN_TBL", pkColumnName = "ORDERNUM_GEN_NAME", valueColumnName = "ORDERNUM_GEN_VAL", pkColumnValue = "ORDERNUM_GEN_PK", initialValue = 40000, allocationSize = 1)
    private Long id;

    @Transient private Long userId;  // Use non PII id for GTag.
    @Transient private String card_number;

    private String order_type;
    private String sub_type;
    private String reason;
    private String llc_number_members;
    private String business_type;
    private String business_sub_type;
    private String business_sub_type_2;
    private String business_sub_type_3;
    private String business_sub_type_4;
    private String business_details;
    private String first_name;
    private String middle_name;
    private Boolean no_middle_name;
    private String last_name;
    private String title;
    private Integer is_sec645;
    private String suffix;
    private String email;
    private String ssn;

    /* Secondary individual: Trustee for Trusts and Deceased person for Estates */
    private String secondary_first_name;
    private String secondary_middle_name;
    private Boolean secondary_no_middle_name;
    private String secondary_last_name;
    private String secondary_suffix;
    private String secondary_ssn;

    private String address;
    private String apt_suite;
    private String city;
    private String state;
    private String county;
    private String zip_code;
    private String phone_number;
    private Integer is_diff_mailing_address;
    private String mailing_address;
    private String mailing_apt_suite;
    private String mailing_city;
    private String mailing_state;
    private String mailing_zip_code;
    private String mailing_country;
    private String request_reason;
    private String business_country;
    private String business_state;
    private Integer start_date_year;
    private Integer start_date_month;
    private String legal_name;
    private String state_incorporated;
    private Integer is_same_physical_address;
    private Boolean is_previous_ein;
    private String previous_ein;
    private Integer accounting_close_month;
    private String reit_type;
    private Integer is_large_motor_vehicle;
    private Integer is_gambling;
    private Integer is_excise_tax_form720;
    private Integer is_atf;
    private Integer is_w2_employees;
    private Integer date_first_wages_year;
    private Integer date_first_wages_month;
    private Integer max_ees_next12mos_agri;
    private Integer max_ees_next12mos_household;
    private Integer max_ees_next12mos_other;
    private Boolean is_employment_tax_liability;
    private String card_partner_code;
    private String card_holder_name;
    private String card_expire_date;
    private Integer amount;
    private String orderId;

    private Date refundDate;
    @Column(name = "last_four_digits_card", columnDefinition = "VARCHAR", length = 10)
    private String last4DigitsCard;
    private String cardBrand;

    private String correlationId;
    private String authorizeAuthCode;
    private String authorizeDescription;
    private String authorizeTransactionId;
    private String authorizeErrorCode;
    private String authorizeErrorMessage;
    String refundAuthTransId;
    private String transactionId;
    private String payloadResponseStatus;
    private String signatureVerification;
    private String authCavv;
    private String authEci;
    private String authXid;
    private String enrolled;
    private String eciFlag;
    private String customerIpAddress;

    @Column(name = "trans_type")
    private String transactionType;
    private Integer retryAttempCount;
    private String inquiryRetryNumber;
    private String orbitalTransactionId;
    private String orbitalTransactionNumber;

    @Column
    Integer cbtype;

    private String ein;
    private String letterPdf;
    private String status;
    private Integer errorCode;
    private boolean manuallyRun;
    private String last_saved_page;

    @Column(name = "envelope_id", length = 36)
    private String dsEnvelopeId;
    @Column(name = "envelope_created_at")
    private Long dsEnvelopeMillis; // milliseconds stored in UTC
    @Column(name = "f8821_s3_path", length = 256)
    private String f8821S3Path;

    @Column(name = "bot_id")
    // TODO: Was not used so far, IP rotation is done using EIP associate/disassociate approach, only single bot is needed in terms of loading/amount of EIN orders
    private String botId;
    private Long startAfterMillis; // milliseconds stored in UTC for correct comparison
    private Integer restartsCount;
    private Integer accessDeniedCount;

    @Column
    private String notes;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date notesEntry;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date statusLastChanged;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date createdDate;

    @Transient
    private String card_cvc;

    public String getCardExpMonth() {
        if (StringUtils.isBlank(this.card_expire_date)) {
            return "";
        }
        return this.card_expire_date.substring(0, 2);
    }

    public String getCardExpYear() {
        if (StringUtils.isBlank(this.card_expire_date)) {
            return "";
        }
        Date date = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("YYYY");
        String year = fmt.format(date);

        String result = year.substring(0, 2) + this.card_expire_date.substring(3, 5);
        return result;
    }

    public void setStatus(String status) {
        this.status = status;
        this.statusLastChanged = new Date();
    }

}
