package com.repnox.nineseventax.features.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRecord implements Serializable {

    @Id
    private String username;
    private String password;
    private String email;
    private int orderStatus;
    private int refundPayment;
    private int editRecord;
    private boolean ssnPrivacy;
    private boolean chargebackAuth;
    private boolean fulfilmentAccess;
    private boolean automatedScheduleAccess;
    private boolean manualStatusChange;
    private boolean botLogsAccess;
    private String userType;

    public void setUserData(UserRecord user) {
        this.username = user.username;
        this.email = user.email;
        this.password = user.password;
        this.userType = user.userType;
        this.refundPayment = user.refundPayment;
        this.orderStatus = user.orderStatus;
        this.editRecord = user.editRecord;
        this.ssnPrivacy = user.ssnPrivacy;
        this.chargebackAuth = user.chargebackAuth;
        this.fulfilmentAccess = user.fulfilmentAccess;
        this.automatedScheduleAccess = user.automatedScheduleAccess;
        this.manualStatusChange = user.manualStatusChange;
        this.botLogsAccess = user.botLogsAccess;
    }

}
