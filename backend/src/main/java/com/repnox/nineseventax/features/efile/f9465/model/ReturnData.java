package com.repnox.nineseventax.features.efile.f9465.model;

import jakarta.xml.bind.annotation.*;

import java.math.BigInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "irs9465"
})
@XmlRootElement(name = "ReturnData")
public class ReturnData {

    @XmlElement(name = "IRS9465", required = true)
    protected IRS9465 irs9465;
    @XmlAttribute(name = "documentCnt", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger documentCnt;

    public IRS9465 getIRS9465() {
        return irs9465;
    }

    public void setIRS9465(IRS9465 value) {
        this.irs9465 = value;
    }

    public BigInteger getDocumentCnt() {
        return documentCnt;
    }

    public void setDocumentCnt(BigInteger value) {
        this.documentCnt = value;
    }

}
