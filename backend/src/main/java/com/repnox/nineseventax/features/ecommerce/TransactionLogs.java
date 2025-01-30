package com.repnox.nineseventax.features.ecommerce;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "transaction_logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionLogs implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7225005246935796728L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date createdDate;

    @Column(name = "order_id")
    private Long order_id;

    @Column(name = "cardinal_response")
    private String cardinal_response;

    @Column(name = "orbital_payment_response")
    private String orbital_payment_response;

    @Column(name = "exception_message")
    private String exception_message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public String getCardinal_response() {
        return cardinal_response;
    }

    public void setCardinal_response(String cardinal_response) {
        this.cardinal_response = cardinal_response;
    }

    public String getOrbital_payment_response() {
        return orbital_payment_response;
    }

    public void setOrbital_payment_response(String orbital_payment_response) {
        this.orbital_payment_response = orbital_payment_response;
    }

    public String getException_message() {
        return exception_message;
    }

    public void setException_message(String exception_message) {
        this.exception_message = exception_message;
    }
}
