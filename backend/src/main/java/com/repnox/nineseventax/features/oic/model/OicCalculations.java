package com.repnox.nineseventax.features.oic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OicCalculations implements Serializable {

    public static final String SELECTED_OPTION_12 = "12";
    public static final String SELECTED_OPTION_24 = "24";
    public static final String SELECTED_OPTION_EXCEPTION_5 = "exception-5";
    public static final String SELECTED_OPTION_EXCEPTION_24 = "exception-24";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long oicId;

    private Integer calc12MonthSettlement;

    private Integer calc24MonthSettlement;

    private Integer calcDueWithApplication;

    private String selectedSettlement;

    private Integer exceptionPaymentAmountMonthly;

    private Integer exceptionPaymentAmount1;

    private Integer exceptionPaymentAmount2;

    private Integer exceptionPaymentAmount3;

    private Integer exceptionPaymentAmount4;

    private Integer exceptionPaymentAmount5;

    private Integer totalAssets;

    private Integer totalIncome;

    private Integer totalExpenses;

    @JsonFormat(pattern = "M/d/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate exceptionPaymentDate1;

    @JsonFormat(pattern = "M/d/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate exceptionPaymentDate2;

    @JsonFormat(pattern = "M/d/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate exceptionPaymentDate3;

    @JsonFormat(pattern = "M/d/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate exceptionPaymentDate4;

    @JsonFormat(pattern = "M/d/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate exceptionPaymentDate5;

    private String exceptionExplanation;
}
