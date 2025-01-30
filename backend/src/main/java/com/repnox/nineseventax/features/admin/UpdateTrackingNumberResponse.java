package com.repnox.nineseventax.features.admin;

public class UpdateTrackingNumberResponse {

    private boolean saved;

    private boolean notificationSent;

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }
}
