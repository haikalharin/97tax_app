package com.repnox.nineseventax.features.ecommerce;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "email_to_user_id_map", schema = "nineseventax")
public class EmailToUserIdMap {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    public EmailToUserIdMap() {
    }

    public EmailToUserIdMap(String email, Long userId) {
        this.email = email;
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "EmailToUserIdMap{" +
                "email='" + email + '\'' +
                ", userId=" + userId +
                '}';
    }
}