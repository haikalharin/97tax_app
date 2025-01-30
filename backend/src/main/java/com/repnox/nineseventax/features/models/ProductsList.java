package com.repnox.nineseventax.features.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import javax.persistence.*;

import com.amazonaws.services.stepfunctions.builder.states.Retrier;

import org.junit.validator.PublicClassValidator;
@Entity
@Table(name="product_prices")
public class ProductsList implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PRODCUT_NUM_GEN")
    @TableGenerator(name = "PRODCUT_NUM_GEN", table = "PRODCUT_NUM_GEN_TBL", pkColumnName = "PRODCUT_NUM_GEN_NAME", valueColumnName = "PRODCUT_NUM_GEN_VAL", pkColumnValue = "PRODCUT_NUM_GEN_PK", initialValue = 40000, allocationSize = 1)
    private Long id;

    @Column(name = "product")
    private String product;

    @Column(name = "sub_options")
    private String sub_options;

    @Column(name = "current_price")
    private float current_price;

    @Column(name = "change_price")
    private float change_price;

    @Column(name = "time_periods_start")
    private Date time_periods_start;

    @Column(name = "time_periods_end")
    private Date time_periods_end;

    @Column(name = "additional_timeframes")
    private String additional_timeframes;

    @Column(name = "is_primary")
    private boolean is_primary;

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

    public String getSubOptions() {
        return sub_options;
    }

    public void setSubOptions(String sub_options) {
        this.sub_options = sub_options;
    }

    public float getCurrentPrice() {
        return this.current_price;
    }

    public void setCurrentPrice(float current_price) {
        this.current_price = current_price;
    }

    public float getChangePrice() {
        return this.change_price;
    }

    public void setChangePrice(float change_price) {
        this.change_price = change_price;
    }

    public Date getTimePeriodsStart() {
        return this.time_periods_start;
    }

    public void setTimePeriodsStart(Date time_periods_start) {
        this.time_periods_start = time_periods_start;
    }

    public Date getTimePeriodsEnd() {
        return this.time_periods_end;
    }

    public void setTimePeriodsEnd(Date time_periods_end) {
        this.time_periods_end = time_periods_end;
    }

    public Boolean getIsPrimary() {
        return this.is_primary;
    }

    public void setIsPrimary(boolean is_primary) {
        this.is_primary = is_primary;
    }

    public Date getLastUpdated() {
        return this.last_updated;
    }

    public void setLastUpdated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public String getAdditionalTimeframes() {
        return this.additional_timeframes;
    }

    public void setAdditionalTimeframes(String additional_timeframes) {
        this.additional_timeframes = additional_timeframes;
    }
}