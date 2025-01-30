package com.repnox.nineseventax.features.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="sales_list")
public class SalesList implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SALES_NUM_GEN")
    @TableGenerator(name = "SALES_NUM_GEN", table = "SALES_NUM_GEN_TBL", pkColumnName = "SALES_NUM_GEN_NAME", valueColumnName = "SALES_NUM_GEN_VAL", pkColumnValue = "SALES_NUM_GEN_PK", initialValue = 40000, allocationSize = 1)
    private Long id;

    @Column(name = "product")
    private String product;

    @Column(name = "start_time")
    private Date start_time;

    @Column(name = "end_time")
    private Date end_time;

    @Column(name = "start_daily")
    private Date start_daily;

    @Column(name = "end_daily")
    private Date end_daily;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "on_off")
    private boolean on_off;

    @Column(name = "last_updated")
    private Date last_updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Date getStartTime() {
        return start_time;
    }

    public void setStartTime(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEndTime() {
        return this.end_time;
    }

    public void setEndTime(Date end_time) {
        this.end_time = end_time;
    }

    public Date getStartDaily() {
        return start_daily;
    }

    public void setStartDaily(Date start_daily) {
        this.start_daily = start_daily;
    }

    public Date getEndDaily() {
        return end_daily;
    }

    public void setEndDaily(Date end_daily) {
        this.end_daily = end_daily;
    }

    public Boolean getOnOff() {
        return this.on_off;
    }

    public void setOnOff(boolean on_off) {
        this.on_off = on_off;
    }

    public Date getLastUpdated() {
        return this.last_updated;
    }

    public void setLastUpdated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public String getPhoneNumber() {
        return this.phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

}
