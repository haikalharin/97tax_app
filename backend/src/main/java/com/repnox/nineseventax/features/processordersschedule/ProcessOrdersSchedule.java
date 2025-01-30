package com.repnox.nineseventax.features.processordersschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "process_orders_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessOrdersSchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "is_automatic")
    private Boolean isAutomatic;

    @Column(name = "automated_time")
    private Date automatedTime;

    @Column(name = "excluded_holidays")
    private String excludedHolidays;
}
