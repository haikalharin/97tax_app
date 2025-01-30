package com.repnox.nineseventax.features.casetracker;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CaseTrackerData {

    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date createdDate;
    private String status;
    private String customerFirstName;
    private String customerEmail;
    private String trackingNumber;
    private Boolean isCalifornia;
    private Boolean isNewJersey;
    private Boolean isGeorgia;
    private Boolean isIllinois;
    private Boolean isEin;

}
