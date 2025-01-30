package com.repnox.nineseventax.features.efile.f9465.model;

import gov.irs.efile.*;
import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IRS9465Type", propOrder = {
        "f9465TaxReturnTypeCd",
        "iaTaxYrDt",
        "busIATaxYrDt",
        "taxPeriodDetailGrp",
        "personFullName",
        "primaryNameControlTxt",
        "primarySSN",
        "spouseName",
        "spouseNameControlTxt",
        "spouseSSN",
        "newAddressInd",
        "homePhoneGrp",
        "workPhoneGrp",
        "taxDueAmt",
        "additionalBalanceDueAmt",
        "totalBalanceDueAmt",
        "paymentAmt",
        "totalTaxDueAmt",
        "calculatedMonthlyPymtAmt",
        "paymentDueAmt",
        "revisedMonthlyPaymentAmt",
        "canNotIncreasePaymentInd",
        "paymentDueDayNum",
        "routingTransitNum",
        "bankAccountNum",
        "noElectronicPaymentInd",
        "payrollDeductionAgreementInd",
        "primaryResidenceCountyNm",
        "maritalStatusSingleInd",
        "maritalStatusMarriedInd",
        "shareHouseholdExpnsWithSpsInd",
        "dependentsClaimedCnt",
        "age65OrOlderHouseholdCnt",
        "primaryPdOnceAWeekInd",
        "primaryPdOnceEveryTwoWeeksInd",
        "primaryPdOnceAMonthInd",
        "primaryPdTwiceAMonthInd",
        "primaryNetIncomePerPayPrdAmt",
        "spousePdOnceAWeekInd",
        "spousePdOnceEveryTwoWeeksInd",
        "spousePdOnceAMonthInd",
        "spousePdTwiceAMonthInd",
        "spouseNetIncomePerPayPrdAmt",
        "vehicleCnt",
        "carPaymentCnt",
        "healthInsuranceInd",
        "healthInsurancePremiumDedInd",
        "mthlyHealthInsurancePremiumAmt",
        "courtOrderedPaymentInd",
        "courtOrderedPaymentDedInd",
        "mthlyCourtOrderedPaymentAmt",
        "childOrDependentCareExpenseAmt"
})
@XmlSeeAlso({
        IRS9465.class
})
public class IRS9465Type {

    @XmlElement(name = "F9465TaxReturnTypeCd", required = true)
    protected List<String> f9465TaxReturnTypeCd;
    @XmlElement(name = "IATaxYrDt")
    protected String iaTaxYrDt;
    @XmlElement(name = "BusIATaxYrDt")
    protected String busIATaxYrDt;
    @XmlElement(name = "TaxPeriodDetailGrp")
    protected TaxPeriodDetailGrp taxPeriodDetailGrp;
    @XmlElement(name = "PersonFullName", required = true)
    protected PersonFullNameType personFullName;
    @XmlElement(name = "PrimaryNameControlTxt", required = true)
    protected String primaryNameControlTxt;
    @XmlElement(name = "PrimarySSN", required = true)
    protected String primarySSN;
    @XmlElement(name = "SpouseName")
    protected PersonFullNameType spouseName;
    @XmlElement(name = "SpouseNameControlTxt")
    protected String spouseNameControlTxt;
    @XmlElement(name = "SpouseSSN")
    protected String spouseSSN;
    @XmlElement(name = "NewAddressInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType newAddressInd;
    @XmlElement(name = "HomePhoneGrp")
    protected HomePhoneGrp homePhoneGrp;
    @XmlElement(name = "WorkPhoneGrp")
    protected WorkPhoneGrp workPhoneGrp;
    @XmlElement(name = "TaxDueAmt", required = true)
    protected BigInteger taxDueAmt;
    @XmlElement(name = "AdditionalBalanceDueAmt")
    protected BigInteger additionalBalanceDueAmt;
    @XmlElement(name = "TotalBalanceDueAmt", required = true)
    protected BigInteger totalBalanceDueAmt;
    @XmlElement(name = "PaymentAmt")
    protected BigInteger paymentAmt;
    @XmlElement(name = "TotalTaxDueAmt")
    protected BigInteger totalTaxDueAmt;
    @XmlElement(name = "CalculatedMonthlyPymtAmt")
    protected BigInteger calculatedMonthlyPymtAmt;
    @XmlElement(name = "PaymentDueAmt")
    protected BigInteger paymentDueAmt;
    @XmlElement(name = "RevisedMonthlyPaymentAmt")
    protected BigInteger revisedMonthlyPaymentAmt;
    @XmlElement(name = "CanNotIncreasePaymentInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType canNotIncreasePaymentInd;
    @XmlElement(name = "PaymentDueDayNum")
    protected int paymentDueDayNum;
    @XmlElement(name = "RoutingTransitNum")
    protected String routingTransitNum;
    @XmlElement(name = "BankAccountNum")
    protected String bankAccountNum;
    @XmlElement(name = "NoElectronicPaymentInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType noElectronicPaymentInd;
    @XmlElement(name = "PayrollDeductionAgreementInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType payrollDeductionAgreementInd;
    @XmlElement(name = "PrimaryResidenceCountyNm")
    protected String primaryResidenceCountyNm;
    @XmlElement(name = "MaritalStatusSingleInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType maritalStatusSingleInd;
    @XmlElement(name = "MaritalStatusMarriedInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType maritalStatusMarriedInd;
    @XmlElement(name = "ShareHouseholdExpnsWithSpsInd")
    protected Boolean shareHouseholdExpnsWithSpsInd;
    @XmlElement(name = "DependentsClaimedCnt")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger dependentsClaimedCnt;
    @XmlElement(name = "Age65OrOlderHouseholdCnt")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger age65OrOlderHouseholdCnt;
    @XmlElement(name = "PrimaryPdOnceAWeekInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType primaryPdOnceAWeekInd;
    @XmlElement(name = "PrimaryPdOnceEveryTwoWeeksInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType primaryPdOnceEveryTwoWeeksInd;
    @XmlElement(name = "PrimaryPdOnceAMonthInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType primaryPdOnceAMonthInd;
    @XmlElement(name = "PrimaryPdTwiceAMonthInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType primaryPdTwiceAMonthInd;
    @XmlElement(name = "PrimaryNetIncomePerPayPrdAmt")
    protected BigInteger primaryNetIncomePerPayPrdAmt;
    @XmlElement(name = "SpousePdOnceAWeekInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType spousePdOnceAWeekInd;
    @XmlElement(name = "SpousePdOnceEveryTwoWeeksInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType spousePdOnceEveryTwoWeeksInd;
    @XmlElement(name = "SpousePdOnceAMonthInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType spousePdOnceAMonthInd;
    @XmlElement(name = "SpousePdTwiceAMonthInd")
    @XmlSchemaType(name = "string")
    protected CheckboxType spousePdTwiceAMonthInd;
    @XmlElement(name = "SpouseNetIncomePerPayPrdAmt")
    protected BigInteger spouseNetIncomePerPayPrdAmt;
    @XmlElement(name = "VehicleCnt")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger vehicleCnt;
    @XmlElement(name = "CarPaymentCnt")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger carPaymentCnt;
    @XmlElement(name = "HealthInsuranceInd")
    protected Boolean healthInsuranceInd;
    @XmlElement(name = "HealthInsurancePremiumDedInd")
    protected Boolean healthInsurancePremiumDedInd;
    @XmlElement(name = "MthlyHealthInsurancePremiumAmt")
    protected BigInteger mthlyHealthInsurancePremiumAmt;
    @XmlElement(name = "CourtOrderedPaymentInd")
    protected Boolean courtOrderedPaymentInd;
    @XmlElement(name = "CourtOrderedPaymentDedInd")
    protected Boolean courtOrderedPaymentDedInd;
    @XmlElement(name = "MthlyCourtOrderedPaymentAmt")
    protected BigInteger mthlyCourtOrderedPaymentAmt;
    @XmlElement(name = "ChildOrDependentCareExpenseAmt")
    protected BigInteger childOrDependentCareExpenseAmt;

    public List<String> getF9465TaxReturnTypeCd() {
        if (f9465TaxReturnTypeCd == null) {
            f9465TaxReturnTypeCd = new ArrayList<String>();
        }
        return this.f9465TaxReturnTypeCd;
    }

    public String getIATaxYrDt() {
        return iaTaxYrDt;
    }

    public void setIATaxYrDt(String value) {
        this.iaTaxYrDt = value;
    }

    public String getBusIATaxYrDt() {
        return busIATaxYrDt;
    }

    public void setBusIATaxYrDt(String value) {
        this.busIATaxYrDt = value;
    }

    public TaxPeriodDetailGrp getTaxPeriodDetailGrp() {
        return taxPeriodDetailGrp;
    }

    public void setTaxPeriodDetailGrp(TaxPeriodDetailGrp value) {
        this.taxPeriodDetailGrp = value;
    }

    public PersonFullNameType getPersonFullName() {
        return personFullName;
    }

    public void setPersonFullName(PersonFullNameType value) {
        this.personFullName = value;
    }

    public String getPrimaryNameControlTxt() {
        return primaryNameControlTxt;
    }

    public void setPrimaryNameControlTxt(String value) {
        this.primaryNameControlTxt = value;
    }

    public String getPrimarySSN() {
        return primarySSN;
    }

    public void setPrimarySSN(String value) {
        this.primarySSN = value;
    }

    public PersonFullNameType getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(PersonFullNameType value) {
        this.spouseName = value;
    }

    public String getSpouseNameControlTxt() {
        return spouseNameControlTxt;
    }

    public void setSpouseNameControlTxt(String value) {
        this.spouseNameControlTxt = value;
    }

    public String getSpouseSSN() {
        return spouseSSN;
    }

    public void setSpouseSSN(String value) {
        this.spouseSSN = value;
    }

    public CheckboxType getNewAddressInd() {
        return newAddressInd;
    }

    public void setNewAddressInd(CheckboxType value) {
        this.newAddressInd = value;
    }

    public HomePhoneGrp getHomePhoneGrp() {
        return homePhoneGrp;
    }

    public void setHomePhoneGrp(HomePhoneGrp value) {
        this.homePhoneGrp = value;
    }

    public WorkPhoneGrp getWorkPhoneGrp() {
        return workPhoneGrp;
    }

    public void setWorkPhoneGrp(WorkPhoneGrp value) {
        this.workPhoneGrp = value;
    }

    public BigInteger getTaxDueAmt() {
        return taxDueAmt;
    }

    public void setTaxDueAmt(BigInteger value) {
        this.taxDueAmt = value;
    }

    public BigInteger getAdditionalBalanceDueAmt() {
        return additionalBalanceDueAmt;
    }

    public void setAdditionalBalanceDueAmt(BigInteger value) {
        this.additionalBalanceDueAmt = value;
    }

    public BigInteger getTotalBalanceDueAmt() {
        return totalBalanceDueAmt;
    }

    public void setTotalBalanceDueAmt(BigInteger value) {
        this.totalBalanceDueAmt = value;
    }

    public BigInteger getPaymentAmt() {
        return paymentAmt;
    }

    public void setPaymentAmt(BigInteger value) {
        this.paymentAmt = value;
    }

    public BigInteger getTotalTaxDueAmt() {
        return totalTaxDueAmt;
    }

    public void setTotalTaxDueAmt(BigInteger value) {
        this.totalTaxDueAmt = value;
    }

    public BigInteger getCalculatedMonthlyPymtAmt() {
        return calculatedMonthlyPymtAmt;
    }

    public void setCalculatedMonthlyPymtAmt(BigInteger value) {
        this.calculatedMonthlyPymtAmt = value;
    }

    public BigInteger getPaymentDueAmt() {
        return paymentDueAmt;
    }

    public void setPaymentDueAmt(BigInteger value) {
        this.paymentDueAmt = value;
    }

    public BigInteger getRevisedMonthlyPaymentAmt() {
        return revisedMonthlyPaymentAmt;
    }

    public void setRevisedMonthlyPaymentAmt(BigInteger value) {
        this.revisedMonthlyPaymentAmt = value;
    }

    public CheckboxType getCanNotIncreasePaymentInd() {
        return canNotIncreasePaymentInd;
    }

    public void setCanNotIncreasePaymentInd(CheckboxType value) {
        this.canNotIncreasePaymentInd = value;
    }

    public int getPaymentDueDayNum() {
        return paymentDueDayNum;
    }

    public void setPaymentDueDayNum(int value) {
        this.paymentDueDayNum = value;
    }

    public String getRoutingTransitNum() {
        return routingTransitNum;
    }

    public void setRoutingTransitNum(String value) {
        this.routingTransitNum = value;
    }

    public String getBankAccountNum() {
        return bankAccountNum;
    }

    public void setBankAccountNum(String value) {
        this.bankAccountNum = value;
    }

    public CheckboxType getNoElectronicPaymentInd() {
        return noElectronicPaymentInd;
    }

    public void setNoElectronicPaymentInd(CheckboxType value) {
        this.noElectronicPaymentInd = value;
    }

    public CheckboxType getPayrollDeductionAgreementInd() {
        return payrollDeductionAgreementInd;
    }

    public void setPayrollDeductionAgreementInd(CheckboxType value) {
        this.payrollDeductionAgreementInd = value;
    }

    public String getPrimaryResidenceCountyNm() {
        return primaryResidenceCountyNm;
    }

    public void setPrimaryResidenceCountyNm(String value) {
        this.primaryResidenceCountyNm = value;
    }

    public CheckboxType getMaritalStatusSingleInd() {
        return maritalStatusSingleInd;
    }

    public void setMaritalStatusSingleInd(CheckboxType value) {
        this.maritalStatusSingleInd = value;
    }

    public CheckboxType getMaritalStatusMarriedInd() {
        return maritalStatusMarriedInd;
    }

    public void setMaritalStatusMarriedInd(CheckboxType value) {
        this.maritalStatusMarriedInd = value;
    }

    public Boolean isShareHouseholdExpnsWithSpsInd() {
        return shareHouseholdExpnsWithSpsInd;
    }

    public void setShareHouseholdExpnsWithSpsInd(Boolean value) {
        this.shareHouseholdExpnsWithSpsInd = value;
    }

    public BigInteger getDependentsClaimedCnt() {
        return dependentsClaimedCnt;
    }

    public void setDependentsClaimedCnt(BigInteger value) {
        this.dependentsClaimedCnt = value;
    }

    public BigInteger getAge65OrOlderHouseholdCnt() {
        return age65OrOlderHouseholdCnt;
    }

    public void setAge65OrOlderHouseholdCnt(BigInteger value) {
        this.age65OrOlderHouseholdCnt = value;
    }

    public CheckboxType getPrimaryPdOnceAWeekInd() {
        return primaryPdOnceAWeekInd;
    }

    public void setPrimaryPdOnceAWeekInd(CheckboxType value) {
        this.primaryPdOnceAWeekInd = value;
    }

    public CheckboxType getPrimaryPdOnceEveryTwoWeeksInd() {
        return primaryPdOnceEveryTwoWeeksInd;
    }

    public void setPrimaryPdOnceEveryTwoWeeksInd(CheckboxType value) {
        this.primaryPdOnceEveryTwoWeeksInd = value;
    }

    public CheckboxType getPrimaryPdOnceAMonthInd() {
        return primaryPdOnceAMonthInd;
    }

    public void setPrimaryPdOnceAMonthInd(CheckboxType value) {
        this.primaryPdOnceAMonthInd = value;
    }

    public CheckboxType getPrimaryPdTwiceAMonthInd() {
        return primaryPdTwiceAMonthInd;
    }

    public void setPrimaryPdTwiceAMonthInd(CheckboxType value) {
        this.primaryPdTwiceAMonthInd = value;
    }

    public BigInteger getPrimaryNetIncomePerPayPrdAmt() {
        return primaryNetIncomePerPayPrdAmt;
    }

    public void setPrimaryNetIncomePerPayPrdAmt(BigInteger value) {
        this.primaryNetIncomePerPayPrdAmt = value;
    }

    public CheckboxType getSpousePdOnceAWeekInd() {
        return spousePdOnceAWeekInd;
    }

    public void setSpousePdOnceAWeekInd(CheckboxType value) {
        this.spousePdOnceAWeekInd = value;
    }

    public CheckboxType getSpousePdOnceEveryTwoWeeksInd() {
        return spousePdOnceEveryTwoWeeksInd;
    }

    public void setSpousePdOnceEveryTwoWeeksInd(CheckboxType value) {
        this.spousePdOnceEveryTwoWeeksInd = value;
    }

    public CheckboxType getSpousePdOnceAMonthInd() {
        return spousePdOnceAMonthInd;
    }

    public void setSpousePdOnceAMonthInd(CheckboxType value) {
        this.spousePdOnceAMonthInd = value;
    }

    public CheckboxType getSpousePdTwiceAMonthInd() {
        return spousePdTwiceAMonthInd;
    }

    public void setSpousePdTwiceAMonthInd(CheckboxType value) {
        this.spousePdTwiceAMonthInd = value;
    }

    public BigInteger getSpouseNetIncomePerPayPrdAmt() {
        return spouseNetIncomePerPayPrdAmt;
    }

    public void setSpouseNetIncomePerPayPrdAmt(BigInteger value) {
        this.spouseNetIncomePerPayPrdAmt = value;
    }

    public BigInteger getVehicleCnt() {
        return vehicleCnt;
    }

    public void setVehicleCnt(BigInteger value) {
        this.vehicleCnt = value;
    }

    public BigInteger getCarPaymentCnt() {
        return carPaymentCnt;
    }

    public void setCarPaymentCnt(BigInteger value) {
        this.carPaymentCnt = value;
    }

    public Boolean isHealthInsuranceInd() {
        return healthInsuranceInd;
    }

    public void setHealthInsuranceInd(Boolean value) {
        this.healthInsuranceInd = value;
    }

    public Boolean isHealthInsurancePremiumDedInd() {
        return healthInsurancePremiumDedInd;
    }

    public void setHealthInsurancePremiumDedInd(Boolean value) {
        this.healthInsurancePremiumDedInd = value;
    }

    public BigInteger getMthlyHealthInsurancePremiumAmt() {
        return mthlyHealthInsurancePremiumAmt;
    }

    public void setMthlyHealthInsurancePremiumAmt(BigInteger value) {
        this.mthlyHealthInsurancePremiumAmt = value;
    }

    public Boolean isCourtOrderedPaymentInd() {
        return courtOrderedPaymentInd;
    }

    public void setCourtOrderedPaymentInd(Boolean value) {
        this.courtOrderedPaymentInd = value;
    }

    public Boolean isCourtOrderedPaymentDedInd() {
        return courtOrderedPaymentDedInd;
    }

    public void setCourtOrderedPaymentDedInd(Boolean value) {
        this.courtOrderedPaymentDedInd = value;
    }

    public BigInteger getMthlyCourtOrderedPaymentAmt() {
        return mthlyCourtOrderedPaymentAmt;
    }

    public void setMthlyCourtOrderedPaymentAmt(BigInteger value) {
        this.mthlyCourtOrderedPaymentAmt = value;
    }

    public BigInteger getChildOrDependentCareExpenseAmt() {
        return childOrDependentCareExpenseAmt;
    }

    public void setChildOrDependentCareExpenseAmt(BigInteger value) {
        this.childOrDependentCareExpenseAmt = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "phoneNum",
            "foreignPhoneNum",
            "bestTimeToCallAtHomeTxt"
    })
    public static class HomePhoneGrp {

        @XmlElement(name = "PhoneNum")
        protected String phoneNum;
        @XmlElement(name = "ForeignPhoneNum")
        protected String foreignPhoneNum;
        @XmlElement(name = "BestTimeToCallAtHomeTxt")
        protected String bestTimeToCallAtHomeTxt;

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String value) {
            this.phoneNum = value;
        }

        public String getForeignPhoneNum() {
            return foreignPhoneNum;
        }

        public void setForeignPhoneNum(String value) {
            this.foreignPhoneNum = value;
        }

        public String getBestTimeToCallAtHomeTxt() {
            return bestTimeToCallAtHomeTxt;
        }

        public void setBestTimeToCallAtHomeTxt(String value) {
            this.bestTimeToCallAtHomeTxt = value;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "taxPeriodBeginDt",
            "taxPeriodEndDt"
    })
    public static class TaxPeriodDetailGrp {

        @XmlElement(name = "TaxPeriodBeginDt", required = true)
        @XmlSchemaType(name = "string")
        protected String taxPeriodBeginDt;
        @XmlElement(name = "TaxPeriodEndDt", required = true)
        @XmlSchemaType(name = "string")
        protected String taxPeriodEndDt;

        public String getTaxPeriodBeginDt() {
            return taxPeriodBeginDt;
        }

        public void setTaxPeriodBeginDt(String value) {
            this.taxPeriodBeginDt = value;
        }

        public String getTaxPeriodEndDt() {
            return taxPeriodEndDt;
        }

        public void setTaxPeriodEndDt(String value) {
            this.taxPeriodEndDt = value;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "phoneNum",
            "foreignPhoneNum",
            "telephoneNumberExtensionNum",
            "bestTimeToCallAtWorkTxt"
    })
    public static class WorkPhoneGrp {

        @XmlElement(name = "PhoneNum")
        protected String phoneNum;
        @XmlElement(name = "ForeignPhoneNum")
        protected String foreignPhoneNum;
        @XmlElement(name = "TelephoneNumberExtensionNum")
        protected String telephoneNumberExtensionNum;
        @XmlElement(name = "BestTimeToCallAtWorkTxt")
        protected String bestTimeToCallAtWorkTxt;

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String value) {
            this.phoneNum = value;
        }

        public String getForeignPhoneNum() {
            return foreignPhoneNum;
        }

        public void setForeignPhoneNum(String value) {
            this.foreignPhoneNum = value;
        }

        public String getTelephoneNumberExtensionNum() {
            return telephoneNumberExtensionNum;
        }

        public void setTelephoneNumberExtensionNum(String value) {
            this.telephoneNumberExtensionNum = value;
        }

        public String getBestTimeToCallAtWorkTxt() {
            return bestTimeToCallAtWorkTxt;
        }

        public void setBestTimeToCallAtWorkTxt(String value) {
            this.bestTimeToCallAtWorkTxt = value;
        }

    }

}
