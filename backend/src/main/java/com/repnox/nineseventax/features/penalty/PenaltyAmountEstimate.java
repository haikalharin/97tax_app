package com.repnox.nineseventax.features.penalty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table
@Deprecated
public class PenaltyAmountEstimate implements Serializable {

    private static final long serialVersionUID = -7355005246345796718L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long order_id;

    @Column(name = "year")
    private String year;

    @Column(name = "amount")
    private String amount;

    @Column(name = "failure_to_file_penalty_amount")
    private int failureToFilePenaltyAmount;

    @Column(name = "failure_to_pay_penalty_amount")
    private int failureToPayPenaltyAmount;

    @Column(name = "failure_to_deposit_penalty_amount")
    private int failureToDepositPenaltyAmount;

    private Boolean filedLate;
    private String filedMonth;
    private String filedYear;
    private String paidMonth;
    private String paidYear;

    private Date ddDate;
    private int daysLate;
    private int ddAmount;

    public Date getDdDate() {
        return ddDate;
    }

    public void setDdDate(Date ddDate) {
        this.ddDate = ddDate;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Boolean getFiledLate() {
        return filedLate;
    }

    public void setFiledLate(Boolean filedLate) {
        this.filedLate = filedLate;
    }

    public String getFiledMonth() {
        return filedMonth;
    }

    public void setFiledMonth(String filedMonth) {
        this.filedMonth = filedMonth;
    }

    public String getFiledYear() {
        return filedYear;
    }

    public void setFiledYear(String filedYear) {
        this.filedYear = filedYear;
    }

    public int getFailureToFilePenaltyAmount() {
        return failureToFilePenaltyAmount;
    }

    public void setFailureToFilePenaltyAmount(int failureToFilePenaltyAmount) {
        this.failureToFilePenaltyAmount = failureToFilePenaltyAmount;
    }

    public void addFailureToFilePenaltyAmount(int failureToFilePenaltyAmount) {
        this.failureToFilePenaltyAmount += failureToFilePenaltyAmount;
    }

    public int getFailureToPayPenaltyAmount() {
        return failureToPayPenaltyAmount;
    }

    public void setFailureToPayPenaltyAmount(int failureToPayPenaltyAmount) {
        this.failureToPayPenaltyAmount = failureToPayPenaltyAmount;
    }

    public void addFailureToPayPenaltyAmount(int failureToPayPenaltyAmount) {
        this.failureToPayPenaltyAmount += failureToPayPenaltyAmount;
    }

    public int getFailureToDepositPenaltyAmount() {
        return failureToDepositPenaltyAmount;
    }

    public void setFailureToDepositPenaltyAmount(int failureToDepositPenaltyAmount) {
        this.failureToDepositPenaltyAmount = failureToDepositPenaltyAmount;
    }

    public void addFailureToDepositPenaltyAmount(int failureToDepositPenaltyAmount) {
        this.failureToDepositPenaltyAmount += failureToDepositPenaltyAmount;
    }

    public String getPaidMonth() {
        return paidMonth;
    }

    public void setPaidMonth(String paidMonth) {
        this.paidMonth = paidMonth;
    }

    public String getPaidYear() {
        return paidYear;
    }

    public void setPaidYear(String paidYear) {
        this.paidYear = paidYear;
    }

    public int getDaysLate() {
        return daysLate;
    }

    public void setDaysLate(int daysLate) {
        this.daysLate = daysLate;
    }

    public int getDdAmount() {
        return ddAmount;
    }

    public void setDdAmount(int ddAmount) {
        this.ddAmount = ddAmount;
    }

}
