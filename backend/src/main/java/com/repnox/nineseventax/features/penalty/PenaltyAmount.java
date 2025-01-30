package com.repnox.nineseventax.features.penalty;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Deprecated
public class PenaltyAmount implements Serializable {

    private static final long serialVersionUID = -7355005247895796718L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year")
    private String year;

    @Column(name = "amount")
    private String amount;

    @Column(name = "penatly_type")
    private String penatlyType;

    @Column(name = "order_id")
    private Long order_id;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPenatlyType() {
        return penatlyType;
    }

    public void setPenatlyType(String penatlyType) {
        this.penatlyType = penatlyType;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

}
