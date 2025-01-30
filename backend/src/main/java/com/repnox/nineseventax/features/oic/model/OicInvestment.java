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
public class OicInvestment implements Serializable {

    public enum InvestmentType {
        STOCKS, BONDS, _401K, IRA, RETIREMENT, OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long oicId;

    private InvestmentType type;

    private String typeDescription;

    private String name;

    private String accountNumber;

    private Integer marketValue;

    private Integer loanBalance;

    private Boolean businessAsset;
}
