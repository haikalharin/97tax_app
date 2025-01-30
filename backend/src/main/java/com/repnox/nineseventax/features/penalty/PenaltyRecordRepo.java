package com.repnox.nineseventax.features.penalty;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenaltyRecordRepo extends JpaRepository<PenaltyOrder, Long> {

    @Query("from PenaltyOrder where correlationId = ?1")
    PenaltyOrder findByCorrelationId(String correlationId);

    @Query("from PenaltyOrder where authorizeTransactionId = ?1")
    PenaltyOrder findByAuthorizeTransactionId(String authTransId);

    @Query(
            value = "SELECT r.* from penalty_order r where r.status = 'On Hold' and datediff(now(), r.status_last_changed) >= 2",
            nativeQuery = true
    )
    List<PenaltyOrder> findStaleOnHoldOrders();

    @Query("from PenaltyOrder where status = 'Processing' and (shipengineStatusCode is NULL or shipengineStatusCode = '' or shipengineStatusCode <> 'in_transit') ORDER BY id DESC")
    List<PenaltyOrder> findOrderInProcessingAndNotShipengine();
}
