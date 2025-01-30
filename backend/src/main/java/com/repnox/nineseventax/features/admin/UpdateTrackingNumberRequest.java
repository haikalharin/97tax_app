package com.repnox.nineseventax.features.admin;

public class UpdateTrackingNumberRequest {

    private String trackingNumber;

    private boolean sendNotification;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public boolean isSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }
}
