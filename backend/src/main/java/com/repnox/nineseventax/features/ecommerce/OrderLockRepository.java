package com.repnox.nineseventax.features.ecommerce;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLockRepository extends JpaRepository<OrderLock, Long> {
    Optional<OrderLock> findByOrderId(Long orderId);
}