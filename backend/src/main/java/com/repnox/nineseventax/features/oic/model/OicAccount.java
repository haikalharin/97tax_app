package com.repnox.nineseventax.features.oic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OicAccount implements Serializable {

    public enum AccountType {
        CASH, CHECKING, SAVINGS, MONEY_MARKET, ONLINE, CARD, VIRTUAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long oicId;

    private AccountType type;

    private String bankName;

    private String accountNumber;

    private Integer value;

    private Boolean businessAsset;
}
