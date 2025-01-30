package com.repnox.nineseventax.features.admin;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import com.opencsv.CSVWriter;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanRepo;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalDetails;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalRepo;
import com.repnox.nineseventax.features.utils.StringUtility;
import com.repnox.nineseventax.features.utils.TaxConstants;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CsvWriter {
    private static final Logger LOG = LoggerFactory.getLogger(CsvWriter.class);
    @Autowired
    public PaymentPlanRepo paymentPlanRepo;

    @Autowired
    public TaxLienRemovalRepo taxLienRemovalRepo;

    private String married = "";
    private String spouseFirstName = "";
    private String spouseLastName = "";
    private String spouseSsn = "";
    private String bestTimeToCall = "";
    private String totalDebt = "";
    private String debtCents = "";
    private String monthlyPayment = "";
    private String paymentDay = "";
    private String company2 = "";
    private String line2 = "";
    private String address = "";
    private String city2 = "";
    private String state2 = "";
    private String zip2 = "";
    private String product = "";
    private String shippingAddress2 = "";

    public void writeToCSV(List<OrderRecord> productList, String filepath) throws Exception {
        File file = new File(filepath);
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile);
        try {

            // adding header to csv
            String[] header = { "Order Number", "Date", "Order Status", "First Name (Billing)", "Last Name (Billing)",
                    "First and Last", "social_security", "married", "wife_first_name", "wife_last_name",
                    "wife_social_no", "mailing_address", "apt_unit_number", "city_state_zip_address", "City", "State",
                    "Zip", "Phone (Billing)", "best_time_to_call", "owed_debt_total", "cents_numbers", "payment_amount",
                    "date_of_make_your_payment", "Company2", "line2", "Address", "city2", "state2", "zip2", "email",
                    "Order Date", "Service Type" };
            writer.writeNext(header);

            // add data to csv
            for (OrderRecord record : productList) {
                String exportDateStr = defaultString(new Date());
                PaymentPlanDetails paymentPlanDetails = null;
                StringUtility stringUtility = new StringUtility();
                TaxLienRemovalDetails taxlienPlanDetails = null;

                if (StringUtils.isNoneBlank(record.getProduct())
                        && record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
                    paymentPlanDetails = paymentPlanRepo.findByOrderNum(record.getOrderNum());
                } else if (StringUtils.isNoneBlank(record.getProduct())
                        && record.getProduct().equals(TaxConstants.TAXLIEN_REMOVAL)) {
                    taxlienPlanDetails = taxLienRemovalRepo.getByOrderNum(record.getOrderNum());
                }
                if (paymentPlanDetails == null && taxlienPlanDetails == null) {
                    continue;
                }
                if (paymentPlanDetails != null && record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
                    paymentPlanValues(paymentPlanDetails, record);
                } else if (taxlienPlanDetails != null && record.getProduct().equals(TaxConstants.TAXLIEN_REMOVAL)) {
                    taxlienValues(taxlienPlanDetails, record);
                }
                setAddress(record);
                try {
                    String[] data1 = { defaultString(record.getOrderNum()), exportDateStr,
                            StringUtils.isBlank(record.getStatus()) ? " " : record.getStatus(),
                            StringUtils.isBlank(record.getFirstName()) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(record.getFirstName()),
                            StringUtils.isBlank(record.getLastName()) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(record.getLastName()),
                            StringUtils.isBlank(record.getFirstName()) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(
                                            record.getFirstName() + " " + record.getLastName()),
                            StringUtils.isBlank(record.getSsn()) ? " " : record.getSsn(),
                            StringUtils.isBlank(this.married) ? " " : this.married,
                            StringUtils.isBlank(this.spouseFirstName) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(this.spouseFirstName),
                            StringUtils.isBlank(this.spouseLastName) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(this.spouseLastName),
                            StringUtils.isBlank(this.spouseSsn) ? " " : this.spouseSsn,
                            StringUtils.isBlank(record.getShippingAddress1()) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(record.getShippingAddress1()),
                            StringUtils.isBlank(this.shippingAddress2) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(this.shippingAddress2),
                            StringUtils.isBlank(record.getShippingCity() + " " + record.getShippingState() + " "
                                    + record.getShippingZip())
                                            ? " "
                                            : stringUtility.ReplaceSpecialCharactersToCharacters(
                                                    record.getShippingCity() + "- " + record.getShippingState() + " "
                                                            + record.getShippingZip()),
                            StringUtils.isBlank(record.getShippingCity()) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(record.getShippingCity()),
                            StringUtils.isBlank(record.getShippingState()) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(record.getShippingState()),
                            StringUtils.isBlank(record.getShippingZip()) ? " " : record.getShippingZip(),
                            StringUtils.isBlank(record.getPhone()) ? " " : record.getPhone(),
                            StringUtils.isBlank(this.bestTimeToCall) ? " " : this.bestTimeToCall,
                            StringUtils.isBlank(this.totalDebt) ? " " : this.totalDebt,
                            StringUtils.isBlank(this.debtCents) ? " " : this.debtCents,
                            StringUtils.isBlank(this.monthlyPayment) ? " " : this.monthlyPayment,
                            StringUtils.isBlank(this.paymentDay) ? " " : this.paymentDay,
                            StringUtils.isBlank(this.company2) ? " " : this.company2,
                            StringUtils.isBlank(this.line2) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(this.line2),
                            StringUtils.isBlank(this.address) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(this.address),
                            StringUtils.isBlank(this.city2) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(this.city2),
                            StringUtils.isBlank(this.state2) ? " "
                                    : stringUtility.ReplaceSpecialCharactersToCharacters(this.state2),
                            StringUtils.isBlank(this.zip2) ? " " : this.zip2,
                            StringUtils.isBlank(record.getEmail()) ? " " : record.getEmail(),
                            StringUtils.isBlank(record.getCreatedDate().toString()) ? " "
                                    : record.getCreatedDate().toString(),
                            StringUtils.isBlank(record.getProduct()) ? " " : this.product, };

                    writer.writeNext(data1);
                } catch (Exception e) {
                    LOG.info("CSV Writing error", e);
                    throw new Exception(e);
                }
            }

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void setAddress(OrderRecord record) {
        String state = record.getShippingState();
        Boolean isCalifornia = record.getIsCalifornia();
        Boolean isNewJersey = record.getIsNewJersey();
        Boolean isGeorgia = record.getIsGeorgia();
        Boolean isIllinois = record.getIsIllinois();
        Boolean isMichigan = record.getIsMichigan();

        if (BooleanUtils.isTrue(isCalifornia)) {
            this.company2 = TaxConstants.STATE_OF_CALIFORNIA;
            this.line2 = TaxConstants.FRANCHISE_TAX_BOARD;
            this.address = "P.O. Box 2952";
            this.city2 = "Sacramento";
            this.state2 = "CA";
            this.zip2 = "95812-2952";
        } else if (BooleanUtils.isTrue(isNewJersey)) {
            this.company2 = TaxConstants.NEW_JERSEY_DIVISION_OF_TAXATION;
            this.line2 = TaxConstants.PAYMENT_PLAN_UNIT;
            this.address = "P.O. Box 190";
            this.city2 = "Trenton";
            this.state2 = "NJ";
            this.zip2 = "08695-0190";
        } else if (BooleanUtils.isTrue(isGeorgia)) {
            this.company2 = TaxConstants.GEORGIA_DEPARTMENT_OF_REVENUE;
            this.line2 = TaxConstants.PROCESSING_CENTER;
            this.address = "P.O. Box 105596";
            this.city2 = "Atlanta";
            this.state2 = "GA";
            this.zip2 = "30374-0396";
        } else if (BooleanUtils.isTrue(isIllinois)) {
            this.company2 = TaxConstants.ILLINOIS_DEPARTMENT_OF_REVENUE;
            this.line2 = TaxConstants.INSTALLMENT_CONTRACT_UNIT;
            this.address = "PO BOX 19035";
            this.city2 = "Springfield";
            this.state2 = "IL";
            this.zip2 = "62794-9035";
        } else if (BooleanUtils.isTrue(isMichigan)) {
            this.company2 = TaxConstants.MICHIGAN_DEPARTMENT_OF_TREASURY;
            this.address = "PO BOX 30199";
            this.city2 = "Lansing";
            this.state2 = "MI";
            this.zip2 = "48909";
        } else {
            this.company2 = TaxConstants.DEPARTMENT_OF_THE_TREASURY;
            this.line2 = TaxConstants.INTERNAL_REVENUE_SERVICE;

            if (StringUtils.isNotBlank(state)) {
                if ("PR".equals(state) || "AE".equals(state) || "AP".equals(state) || "AA".equals(state)) {
                    this.address = "3651 South Ih 35 5501AUSC";
                    this.city2 = "Austin";
                    this.state2 = "TX";
                    this.zip2 = "78741-7855";
                } else if ("AK".equals(state) || "AZ".equals(state) || "CO".equals(state) || "CT".equals(state)
                        || "DE".equals(state) || "DC".equals(state) || "HI".equals(state) || "ID".equals(state)
                        || "IL".equals(state) || "ME".equals(state) || "MD".equals(state) || "MA".equals(state)
                        || "MT".equals(state) || "NV".equals(state) || "NH".equals(state) || "NJ".equals(state)
                        || "NM".equals(state) || "ND".equals(state) || "OR".equals(state) || "RI".equals(state)
                        || "SD".equals(state) || "TN".equals(state) || "UT".equals(state) || "VT".equals(state)
                        || "WA".equals(state) || "WI".equals(state) || "WY".equals(state)) {
                    this.address = "310 Lowell St. Stop 830";
                    this.city2 = "Andover";
                    this.state2 = "MA";
                    this.zip2 = "01810-5430";
                } else if ("AL".equals(state) || "FL".equals(state) || "GA".equals(state) || "KY".equals(state)
                        || "LA".equals(state) || "MS".equals(state) || "NC".equals(state) || "SC".equals(state)
                        || "TX".equals(state) || "VA".equals(state)) {
                    this.address = "P.O. Box 47421";
                    this.city2 = "Doraville";
                    this.state2 = "GA";
                    this.zip2 = "30362-0421";
                } else if ("AR".equals(state) || "CA".equals(state) || "IN".equals(state) || "IA".equals(state)
                        || "KS".equals(state) || "MI".equals(state) || "MN".equals(state) || "MO".equals(state)
                        || "NE".equals(state) || "NY".equals(state) || "OH".equals(state) || "OK".equals(state)
                        || "PA".equals(state) || "WV".equals(state)) {
                    this.address = "Stop P-4 5000";
                    this.city2 = "Kansas City";
                    this.state2 = "MO";
                    this.zip2 = "64999-0250";
                } else {
                    this.address = "310 Lowell St. Stop 830";
                    this.city2 = "Andover";
                    this.state2 = "MA";
                    this.zip2 = "01810-5430";
                }
            } else {
                this.address = "310 Lowell St. Stop 830";
                this.city2 = "Andover";
                this.state2 = "MA";
                this.zip2 = "01810-5430";
            }
        }

        String aptUnit = record.getShippingAddress2();

        if (StringUtils.isNotBlank(aptUnit)) {
            if (aptUnit.startsWith("lot") || aptUnit.startsWith("suite") || aptUnit.startsWith("ste")
                    || aptUnit.startsWith("apt") || aptUnit.startsWith("spc") || aptUnit.startsWith("space")) {
                this.shippingAddress2 = aptUnit;
            } else {
                this.shippingAddress2 = aptUnit;
            }
        } else {
            this.shippingAddress2 = "";
        }
    }

    public void paymentPlanValues(PaymentPlanDetails plan, OrderRecord record) throws Exception {
        Boolean isCalifornia = record.getIsCalifornia();
        Boolean isNewJersey = record.getIsNewJersey();
        Boolean isGeorgia = record.getIsGeorgia();
        Boolean isIllinois = record.getIsIllinois();
        Boolean isMichigan = record.getIsMichigan();

        try {
            if (BooleanUtils.isTrue(isCalifornia)) {
                this.product = TaxConstants.CALIFORNIA_PAYMENTPLAN;
            } else if (BooleanUtils.isTrue(isNewJersey)) {
                this.product = TaxConstants.NEW_JERSEY_PAYMENTPLAN;
            } else if (BooleanUtils.isTrue(isGeorgia)) {
                this.product = TaxConstants.GEORGIA_PAYMENTPLAN;
            } else if (BooleanUtils.isTrue(isIllinois)) {
                this.product=TaxConstants.ILLINOIS_PAYMENTPLAN;
            } else if (BooleanUtils.isTrue(isMichigan)) {
                this.product=TaxConstants.MICHIGAN_PAYMENTPLAN;
            } else {
                this.product = TaxConstants.IRS_PAYMENTPLAN;
            }

            this.married = StringUtils.isBlank(defaultString(plan.getMarried())) ? " "
                    : defaultString(plan.getMarried());
            this.spouseFirstName = StringUtils.isBlank(plan.getSpouseFirstName()) ? " " : plan.getSpouseFirstName();
            this.spouseLastName = StringUtils.isBlank(plan.getSpouseLastName()) ? " " : plan.getSpouseLastName();
            this.spouseSsn = StringUtils.isBlank(plan.getSpouseSsn()) ? " " : plan.getSpouseSsn();
            this.bestTimeToCall = StringUtils.isBlank(plan.getTimeToCall()) ? " " : plan.getTimeToCall();

            if (StringUtils.isNotBlank(defaultString(record.getTotalDebt()))) {
                this.totalDebt = defaultString(record.getTotalDebt().setScale(0, BigDecimal.ROUND_DOWN));
                this.debtCents = record.getTotalDebt().remainder(BigDecimal.ONE).multiply(new BigDecimal("100"))
                        .setScale(0, BigDecimal.ROUND_DOWN).toPlainString();
            } else {
                this.totalDebt = " ";
                this.debtCents = " ";
            }
            this.monthlyPayment = StringUtils.isBlank(plan.getMonthlyPayment().toString()) ? " "
                    : plan.getMonthlyPayment().toString();
            this.paymentDay = StringUtils.isBlank(plan.getPaymentDayOfMonth()) ? " " : plan.getPaymentDayOfMonth();
        } catch (Exception e) {
            LOG.error("Error creating  payment plan paymentPlanValues()", e);
            throw new Exception(e);
        }
    }

    public void taxlienValues(TaxLienRemovalDetails plan, OrderRecord record) throws Exception {
        try {
            this.married = " ";
            this.spouseFirstName = " ";
            this.spouseLastName = " ";
            this.spouseSsn = " ";
            this.bestTimeToCall = " ";

            if (StringUtils.isNotBlank(defaultString(record.getTotalDebt()))) {
                this.totalDebt = defaultString(record.getTotalDebt().setScale(0, BigDecimal.ROUND_DOWN));
                this.debtCents = record.getTotalDebt().remainder(BigDecimal.ONE).multiply(new BigDecimal("100"))
                        .setScale(0, BigDecimal.ROUND_DOWN).toPlainString();
            } else {
                this.totalDebt = " ";
                this.debtCents = " ";
            }
            this.monthlyPayment = defaultString(record.getTotalDebt());
            this.paymentDay = " ";
        } catch (Exception e) {
            LOG.error("Error creating Tax Lien taxlienValues()", e);
            throw new Exception(e);

        }

    }

    private String defaultString(BigDecimal v) {
        return v != null ? v.toPlainString() : "";
    }

    private String defaultString(Boolean v) {
        return v != null ? (v ? TaxConstants.YES : TaxConstants.NO) : TaxConstants.NO;
    }

    private String defaultString(Date v) {
        return v != null ? new SimpleDateFormat("MM/dd/yyyy").format(v) : "";
    }

    private String defaultString(Long v) {
        return v != null ? v.toString() : "";
    }

}
