package com.repnox.nineseventax.features.ecommerce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private String authorizeTransactionId;

    private String transactionType;

    private String transactionStatus;

    private String cardNumberMasked;

    private String expirationDateMasked;

    private String authAmount;

    private String settlementAmount;

    @Column(name = "auth_code")
    private String authCode;

    private String avsResponse;

    private String cardCodeResponse;

    private String cavvResponse;

    private String responseReasonCode;

    private String responseReasonDescription;
}
