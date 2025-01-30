package com.repnox.nineseventax.features.efile.f9465.model;

import gov.irs.efile.*;
import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnHeaderType", propOrder = {
        "returnTs",
        "consortiumReturnCd",
        "ispNum",
        "softwareId",
        "softwareVersionNum",
        "originatorGrp",
        "selfSelectPINGrp",
        "pinTypeCd",
        "juratDisclosureCd",
        "primaryPINEnteredByCd",
        "spousePINEnteredByCd",
        "primarySignaturePIN",
        "spouseSignaturePIN",
        "primarySignatureDt",
        "spouseSignatureDt",
        "returnTypeCd",
        "filer",
        "form9465BusinessFilerGrp"
})
public class ReturnHeaderType {

    @XmlElement(name = "ReturnTs", required = true)

    /**
     * ReturnTs fields format is required to be a UTC and of the format `yyyy-MM-dd'T'hh:mm:ssX`
     * (i.e. with `Z` at the end), even though a validation error from the IRS will say it is
     * possible to use zone-offsets like `+01:00`, `00:00`, etc.
     */
    @XmlSchemaType(name = "string")
    protected String returnTs;
    @XmlElement(name = "ConsortiumReturnCd")
    @XmlSchemaType(name = "string")
    protected ConsortiumType consortiumReturnCd;
    @XmlElement(name = "ISPNum")
    protected String ispNum;
    @XmlElement(name = "SoftwareId", required = true)
    protected String softwareId;
    @XmlElement(name = "SoftwareVersionNum")
    protected String softwareVersionNum;
    @XmlElement(name = "OriginatorGrp", required = true)
    protected OriginatorGrp originatorGrp;
    @XmlElement(name = "SelfSelectPINGrp")
    protected SelfSelectPINGrp selfSelectPINGrp;
    @XmlElement(name = "PINTypeCd", required = true)
    @XmlSchemaType(name = "string")
    protected PINCodeType pinTypeCd;
    @XmlElement(name = "JuratDisclosureCd", required = true)
    protected String juratDisclosureCd;
    @XmlElement(name = "PrimaryPINEnteredByCd")
    @XmlSchemaType(name = "string")
    protected PINEnteredByType primaryPINEnteredByCd;
    @XmlElement(name = "SpousePINEnteredByCd")
    @XmlSchemaType(name = "string")
    protected PINEnteredByType spousePINEnteredByCd;
    @XmlElement(name = "PrimarySignaturePIN", required = true)
    protected String primarySignaturePIN;
    @XmlElement(name = "SpouseSignaturePIN")
    protected String spouseSignaturePIN;
    @XmlElement(name = "PrimarySignatureDt", required = true)
    @XmlSchemaType(name = "string")
    protected String primarySignatureDt;
    @XmlElement(name = "SpouseSignatureDt")
    @XmlSchemaType(name = "string")
    protected String spouseSignatureDt;
    @XmlElement(name = "ReturnTypeCd", required = true)
    protected String returnTypeCd;
    @XmlElement(name = "Filer", required = true)
    protected Filer filer;
    @XmlElement(name = "Form9465BusinessFilerGrp")
    protected Form9465BusinessFilerGrp form9465BusinessFilerGrp;
    @XmlAttribute(name = "binaryAttachmentCnt", required = true)
    protected BigInteger binaryAttachmentCnt;

    public String getReturnTs() {
        return returnTs;
    }

    public void setReturnTs(String value) {
        this.returnTs = value;
    }

    public ConsortiumType getConsortiumReturnCd() {
        return consortiumReturnCd;
    }

    public void setConsortiumReturnCd(ConsortiumType value) {
        this.consortiumReturnCd = value;
    }

    public String getISPNum() {
        return ispNum;
    }

    public void setISPNum(String value) {
        this.ispNum = value;
    }

    public String getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(String value) {
        this.softwareId = value;
    }

    public String getSoftwareVersionNum() {
        return softwareVersionNum;
    }

    public void setSoftwareVersionNum(String value) {
        this.softwareVersionNum = value;
    }

    public OriginatorGrp getOriginatorGrp() {
        return originatorGrp;
    }

    public void setOriginatorGrp(OriginatorGrp value) {
        this.originatorGrp = value;
    }

    public SelfSelectPINGrp getSelfSelectPINGrp() {
        return selfSelectPINGrp;
    }

    public void setSelfSelectPINGrp(SelfSelectPINGrp value) {
        this.selfSelectPINGrp = value;
    }

    public PINCodeType getPINTypeCd() {
        return pinTypeCd;
    }

    public void setPINTypeCd(PINCodeType value) {
        this.pinTypeCd = value;
    }

    public String getJuratDisclosureCd() {
        return juratDisclosureCd;
    }

    public void setJuratDisclosureCd(String value) {
        this.juratDisclosureCd = value;
    }

    public PINEnteredByType getPrimaryPINEnteredByCd() {
        return primaryPINEnteredByCd;
    }

    public void setPrimaryPINEnteredByCd(PINEnteredByType value) {
        this.primaryPINEnteredByCd = value;
    }

    public PINEnteredByType getSpousePINEnteredByCd() {
        return spousePINEnteredByCd;
    }

    public void setSpousePINEnteredByCd(PINEnteredByType value) {
        this.spousePINEnteredByCd = value;
    }

    public String getPrimarySignaturePIN() {
        return primarySignaturePIN;
    }

    public void setPrimarySignaturePIN(String value) {
        this.primarySignaturePIN = value;
    }

    public String getSpouseSignaturePIN() {
        return spouseSignaturePIN;
    }

    public void setSpouseSignaturePIN(String value) {
        this.spouseSignaturePIN = value;
    }

    public String getPrimarySignatureDt() {
        return primarySignatureDt;
    }

    public void setPrimarySignatureDt(String value) {
        this.primarySignatureDt = value;
    }

    public String getSpouseSignatureDt() {
        return spouseSignatureDt;
    }

    public void setSpouseSignatureDt(String value) {
        this.spouseSignatureDt = value;
    }

    public String getReturnTypeCd() {
        return returnTypeCd;
    }

    public void setReturnTypeCd(String value) {
        this.returnTypeCd = value;
    }

    public Filer getFiler() {
        return filer;
    }

    public void setFiler(Filer value) {
        this.filer = value;
    }

    public Form9465BusinessFilerGrp getForm9465BusinessFilerGrp() {
        return form9465BusinessFilerGrp;
    }

    public void setForm9465BusinessFilerGrp(Form9465BusinessFilerGrp value) {
        this.form9465BusinessFilerGrp = value;
    }

    public BigInteger getBinaryAttachmentCnt() {
        return binaryAttachmentCnt;
    }

    public void setBinaryAttachmentCnt(BigInteger value) {
        this.binaryAttachmentCnt = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "primarySSN",
            "spouseSSN",
            "personFullName",
            "spouseName",
            "inCareOfNm",
            "primaryNameControlTxt",
            "spouseNameControlTxt",
            "usAddress",
            "foreignAddress"
    })
    public static class Filer {

        @XmlElement(name = "PrimarySSN", required = true)
        protected String primarySSN;
        @XmlElement(name = "SpouseSSN")
        protected String spouseSSN;
        @XmlElement(name = "PersonFullName", required = true)
        protected PersonFullNameType personFullName;
        @XmlElement(name = "SpouseName")
        protected PersonFullNameType spouseName;
        @XmlElement(name = "InCareOfNm")
        protected String inCareOfNm;
        @XmlElement(name = "PrimaryNameControlTxt", required = true)
        protected String primaryNameControlTxt;
        @XmlElement(name = "SpouseNameControlTxt")
        protected String spouseNameControlTxt;
        @XmlElement(name = "USAddress")
        protected USAddressType usAddress;
        @XmlElement(name = "ForeignAddress")
        protected ForeignAddressType foreignAddress;

        public String getPrimarySSN() {
            return primarySSN;
        }

        public void setPrimarySSN(String value) {
            this.primarySSN = value;
        }

        public String getSpouseSSN() {
            return spouseSSN;
        }

        public void setSpouseSSN(String value) {
            this.spouseSSN = value;
        }

        public PersonFullNameType getPersonFullName() {
            return personFullName;
        }

        public void setPersonFullName(PersonFullNameType value) {
            this.personFullName = value;
        }

        public PersonFullNameType getSpouseName() {
            return spouseName;
        }

        public void setSpouseName(PersonFullNameType value) {
            this.spouseName = value;
        }

        public String getInCareOfNm() {
            return inCareOfNm;
        }

        public void setInCareOfNm(String value) {
            this.inCareOfNm = value;
        }

        public String getPrimaryNameControlTxt() {
            return primaryNameControlTxt;
        }

        public void setPrimaryNameControlTxt(String value) {
            this.primaryNameControlTxt = value;
        }

        public String getSpouseNameControlTxt() {
            return spouseNameControlTxt;
        }

        public void setSpouseNameControlTxt(String value) {
            this.spouseNameControlTxt = value;
        }

        public USAddressType getUSAddress() {
            return usAddress;
        }

        public void setUSAddress(USAddressType value) {
            this.usAddress = value;
        }

        public ForeignAddressType getForeignAddress() {
            return foreignAddress;
        }

        public void setForeignAddress(ForeignAddressType value) {
            this.foreignAddress = value;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "businessNameLine1Txt",
            "businessNameControlTxt",
            "ein"
    })
    public static class Form9465BusinessFilerGrp {

        @XmlElement(name = "BusinessNameLine1Txt", required = true)
        protected String businessNameLine1Txt;
        @XmlElement(name = "BusinessNameControlTxt", required = true)
        protected String businessNameControlTxt;
        @XmlElement(name = "EIN", required = true)
        protected String ein;

        public String getBusinessNameLine1Txt() {
            return businessNameLine1Txt;
        }

        public void setBusinessNameLine1Txt(String value) {
            this.businessNameLine1Txt = value;
        }

        public String getBusinessNameControlTxt() {
            return businessNameControlTxt;
        }

        public void setBusinessNameControlTxt(String value) {
            this.businessNameControlTxt = value;
        }

        public String getEIN() {
            return ein;
        }

        public void setEIN(String value) {
            this.ein = value;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "efin",
            "originatorTypeCd",
            "practitionerPINGrp"
    })
    public static class OriginatorGrp {

        @XmlElement(name = "EFIN", required = true)
        protected String efin;
        @XmlElement(name = "OriginatorTypeCd", required = true)
        @XmlSchemaType(name = "string")
        protected OriginatorType originatorTypeCd;
        @XmlElement(name = "PractitionerPINGrp")
        protected PractitionerPINGrp practitionerPINGrp;

        public String getEFIN() {
            return efin;
        }

        public void setEFIN(String value) {
            this.efin = value;
        }

        public OriginatorType getOriginatorTypeCd() {
            return originatorTypeCd;
        }

        public void setOriginatorTypeCd(OriginatorType value) {
            this.originatorTypeCd = value;
        }

        public PractitionerPINGrp getPractitionerPINGrp() {
            return practitionerPINGrp;
        }

        public void setPractitionerPINGrp(PractitionerPINGrp value) {
            this.practitionerPINGrp = value;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "efin",
                "pin"
        })
        public static class PractitionerPINGrp {

            @XmlElement(name = "EFIN", required = true)
            protected String efin;
            @XmlElement(name = "PIN", required = true)
            protected String pin;

            public String getEFIN() {
                return efin;
            }

            public void setEFIN(String value) {
                this.efin = value;
            }

            public String getPIN() {
                return pin;
            }

            public void setPIN(String value) {
                this.pin = value;
            }

        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "primaryBirthDt",
            "spouseBirthDt",
            "primaryPriorYearAGIAmt",
            "spousePriorYearAGIAmt",
            "primaryPriorYearPIN",
            "spousePriorYearPIN"
    })
    public static class SelfSelectPINGrp {

        @XmlElement(name = "PrimaryBirthDt")
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar primaryBirthDt;
        @XmlElement(name = "SpouseBirthDt")
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar spouseBirthDt;
        @XmlElement(name = "PrimaryPriorYearAGIAmt")
        protected BigInteger primaryPriorYearAGIAmt;
        @XmlElement(name = "SpousePriorYearAGIAmt")
        protected BigInteger spousePriorYearAGIAmt;
        @XmlElement(name = "PrimaryPriorYearPIN")
        protected String primaryPriorYearPIN;
        @XmlElement(name = "SpousePriorYearPIN")
        protected String spousePriorYearPIN;

        public XMLGregorianCalendar getPrimaryBirthDt() {
            return primaryBirthDt;
        }

        public void setPrimaryBirthDt(XMLGregorianCalendar value) {
            this.primaryBirthDt = value;
        }

        public XMLGregorianCalendar getSpouseBirthDt() {
            return spouseBirthDt;
        }

        public void setSpouseBirthDt(XMLGregorianCalendar value) {
            this.spouseBirthDt = value;
        }

        public BigInteger getPrimaryPriorYearAGIAmt() {
            return primaryPriorYearAGIAmt;
        }

        public void setPrimaryPriorYearAGIAmt(BigInteger value) {
            this.primaryPriorYearAGIAmt = value;
        }

        public BigInteger getSpousePriorYearAGIAmt() {
            return spousePriorYearAGIAmt;
        }

        public void setSpousePriorYearAGIAmt(BigInteger value) {
            this.spousePriorYearAGIAmt = value;
        }

        public String getPrimaryPriorYearPIN() {
            return primaryPriorYearPIN;
        }

        public void setPrimaryPriorYearPIN(String value) {
            this.primaryPriorYearPIN = value;
        }

        public String getSpousePriorYearPIN() {
            return spousePriorYearPIN;
        }

        public void setSpousePriorYearPIN(String value) {
            this.spousePriorYearPIN = value;
        }

    }

}
