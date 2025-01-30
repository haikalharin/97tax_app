package com.repnox.nineseventax.features.alerts.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "alert_config")
@Data
public class AlertConfig implements Serializable {

    private static final long serialVersionUID = -1360236139436750092L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column
    private String email;

    @Column
    private boolean enabled;

}
