package com.repnox.nineseventax.features.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest implements Serializable {

    @Id
    private String uuid;

    private String username;

    @Transient
    private String password;
}
