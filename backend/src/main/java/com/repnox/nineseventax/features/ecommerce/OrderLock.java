package com.repnox.nineseventax.features.ecommerce;

import javax.persistence.*;
import java.sql.Timestamp;

import lombok.Data;

@Entity
@Table(name = "order_locks")
@Data
public class OrderLock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "locked_at")
    private Timestamp lockedAt;
}