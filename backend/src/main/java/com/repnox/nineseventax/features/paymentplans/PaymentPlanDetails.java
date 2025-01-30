package com.repnox.nineseventax.features.paymentplans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentPlanDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderUrl;
    private Boolean isMobile;

    private Boolean isCalifornia;
    private Boolean isNewJersey;
    private Boolean isGeorgia;
    private Boolean isIllinois;
    private Boolean isMichigan;

    @Column
    private String dba;
    @Column
    private String illinoisAccountId;
    private Integer goodFaithPayment;
    private String mobile;

    private Long orderNum;

    private String firstName;

    private String lastName;

    private String phone;

    private String secondaryPhone;

    private String email;

    @Column
    private String ein;

    @Column
    private Boolean isOwedFromBusiness;

    @Column
    private Boolean payrollDeduction;
    
    @Column
    private String businessName;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalDebt;

    private Integer monthlyPayment;

    private Integer paymentMonths;

    private Boolean married;
    
    private String filingJointly;

	private String timeToCall;

    private String paymentDayOfMonth;

    private String spouseFirstName;

    private String spouseLastName;

    private String spouseSsn;

    private String processingSpeed;
}
