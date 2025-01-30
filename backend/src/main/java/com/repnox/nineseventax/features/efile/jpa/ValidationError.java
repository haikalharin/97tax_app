package com.repnox.nineseventax.features.efile.jpa;

import lombok.Data;

@Data
public class ValidationError {

    private String documentId;
    private String xpathContentTxt;
    private String errorCategoryCd;
    private String errorMessageTxt;
    private String ruleNum;
    private String severityCd;

}
