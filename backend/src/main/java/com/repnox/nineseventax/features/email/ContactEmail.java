package com.repnox.nineseventax.features.email;

public class ContactEmail {

    private Boolean over25k;

    private Boolean over50k;

    private String name;

    private String email;

    private String subject;

    private String message;

    public Boolean getOver25k() {
        return over25k;
    }

    public void setOver25k(Boolean over25k) {
        this.over25k = over25k;
    }

    public Boolean getOver50k() {
        return over50k;
    }

    public void setOver50k(Boolean over50k) {
        this.over50k = over50k;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
