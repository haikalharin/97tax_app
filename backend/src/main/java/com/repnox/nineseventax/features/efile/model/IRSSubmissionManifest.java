package com.repnox.nineseventax.features.efile.model;

import gov.irs.efile.GovernmentCodeType;
import gov.irs.mef.serpiisbu.SensitiveDenySerializeBase;
import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "submissionId",
        "efin",
        "taxYr",
        "governmentCd",
        "federalSubmissionTypeCd",
        "taxPeriodBeginDt",
        "taxPeriodEndDt",
        "tin"}
)
@XmlRootElement(name = "IRSSubmissionManifest")
public class IRSSubmissionManifest extends SensitiveDenySerializeBase {

    @XmlElement(name = "SubmissionId", required = true)
    protected String submissionId;
    @XmlElement(name = "EFIN", required = true)
    protected String efin;
    @XmlElement(name = "TaxYr")
    protected String taxYr;
    @XmlElement(name = "GovernmentCd", required = true)
    @XmlSchemaType(name = "string")
    protected GovernmentCodeType governmentCd;
    @XmlElement(name = "FederalSubmissionTypeCd", required = true)
    protected String federalSubmissionTypeCd;
    @XmlElement(name = "TaxPeriodBeginDt")
    @XmlSchemaType(name = "string")
    protected String taxPeriodBeginDt;
    @XmlElement(name = "TaxPeriodEndDt")
    @XmlSchemaType(name = "string")
    protected String taxPeriodEndDt;
    @XmlElement(name = "TIN", required = true)
    protected String tin;

    public IRSSubmissionManifest() {
    }

    public String getSubmissionId() {
        return this.submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getEFIN() {
        return this.efin;
    }

    public void setEFIN(String efin) {
        this.efin = efin;
    }

    public String getTaxYr() {
        return this.taxYr;
    }

    public void setTaxYr(String taxYr) {
        this.taxYr = taxYr;
    }

    public GovernmentCodeType getGovernmentCd() {
        return this.governmentCd;
    }

    public void setGovernmentCd(GovernmentCodeType governmentCd) {
        this.governmentCd = governmentCd;
    }

    public String getFederalSubmissionTypeCd() {
        return this.federalSubmissionTypeCd;
    }

    public void setFederalSubmissionTypeCd(String federalSubmissionTypeCd) {
        this.federalSubmissionTypeCd = federalSubmissionTypeCd;
    }

    public String getTaxPeriodBeginDt() {
        return this.taxPeriodBeginDt;
    }

    public void setTaxPeriodBeginDt(String taxPeriodBeginDt) {
        this.taxPeriodBeginDt = taxPeriodBeginDt;
    }

    public String getTaxPeriodEndDt() {
        return this.taxPeriodEndDt;
    }

    public void setTaxPeriodEndDt(String taxPeriodEndDt) {
        this.taxPeriodEndDt = taxPeriodEndDt;
    }

    public String getTIN() {
        return this.tin;
    }

    public void setTIN(String tin) {
        this.tin = tin;
    }

}
