package com.repnox.nineseventax.features.ecommerce;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="partner")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Partner implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_name")
    private String partnerName;

    @Column(name = "partner_title")
    private String partnerTitle;

    @Column(name = "partner_effective_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date partnerEffectiveDate;

    @Column(name = "partner_address1")
    private String partnerAddress1;

    @Column(name = "partner_address2")
    private String partnerAddress2;

    @Column(name = "partner_city")
    private String partnerCity;

    @Column(name = "partner_state")
    private String partnerState;

    @Column(name = "partner_zip")
    private String partnerZip;

    @Column(name = "partner_phone_number")
    private String partnerPhoneNumber;

    @Column(name = "partner_ssn")
    private String partnerSsn;

    @Column(name = "partner_percent_ownership")
    private String partnerPercentOwnership;

    @ManyToOne
    @JoinColumn(name="order_record_id", referencedColumnName = "id")
    private OrderRecord orderRecordId;

    @JsonIgnore
    public OrderRecord getOrderRecordId() {
        return orderRecordId;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setOrderRecordId(OrderRecord orderRecordId) {
        this.orderRecordId = orderRecordId;
    }
}
