package com.repnox.nineseventax.features.efile.f9465.model;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "IRS9465")
public class IRS9465 extends IRS9465Type {

    @XmlAttribute(name = "documentName")
    protected String documentName;
    @XmlAttribute(name = "documentId", required = true)
    protected String documentId;
    @XmlAttribute(name = "softwareId")
    protected String softwareId;
    @XmlAttribute(name = "softwareVersionNum")
    protected String softwareVersionNum;

    public String getDocumentName() {
        if (documentName == null) {
            return "IRS9465";
        } else {
            return documentName;
        }
    }

    public void setDocumentName(String value) {
        this.documentName = value;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String value) {
        this.documentId = value;
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

}
