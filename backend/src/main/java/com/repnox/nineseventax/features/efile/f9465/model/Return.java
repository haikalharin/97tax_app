package com.repnox.nineseventax.features.efile.f9465.model;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "returnHeader",
        "returnData"
})
@XmlRootElement(name = "Return")
public class Return {

    @XmlElement(name = "ReturnHeader", required = true)
    protected ReturnHeaderType returnHeader;
    @XmlElement(name = "ReturnData", required = true)
    protected ReturnData returnData;
    @XmlAttribute(name = "returnVersion", required = true)
    protected String returnVersion;

    public ReturnHeaderType getReturnHeader() {
        return returnHeader;
    }

    public void setReturnHeader(ReturnHeaderType value) {
        this.returnHeader = value;
    }

    public ReturnData getReturnData() {
        return returnData;
    }

    public void setReturnData(ReturnData value) {
        this.returnData = value;
    }

    public String getReturnVersion() {
        if (returnVersion == null) {
            return "CUv27.0";
        } else {
            return returnVersion;
        }
    }

    public void setReturnVersion(String value) {
        this.returnVersion = value;
    }

}
