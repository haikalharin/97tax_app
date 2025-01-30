package com.repnox.nineseventax.features.ein;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EinRecordRepo extends JpaRepository<EinOrder, Long> {

    @Query("from EinOrder where correlationId = ?1")
    EinOrder findByCorrelationId(String correlationId);

    @Query("from EinOrder where authorizeTransactionId = ?1")
    EinOrder findByAuthorizeTransactionId(String authTransId);

    @Query(
            value = "SELECT r.* from EinOrder r where r.status = 'On Hold' and datediff(now(), r.status_last_changed) >= 2",
            nativeQuery = true
    )
    List<EinOrder> findStaleOnHoldOrders();

    @Query("from EinOrder where status = 'Processing' and (shipengineStatusCode is NULL or shipengineStatusCode = '' or shipengineStatusCode <> 'in_transit') ORDER BY id DESC")
    List<EinOrder> findOrderInProcessingAndNotShipengine();

    @Query(
            nativeQuery = true,
            value="SELECT o.*, l.error_count FROM ein_orders o " +
                    "  LEFT JOIN (select COUNT(id) AS error_count, ein_order_id FROM ein_bot_log WHERE is_error = 1 GROUP BY ein_order_id) l ON o.id = l.ein_order_id " +
                    " WHERE (o.status = 'Processing' OR o.status = 'On Hold') " +
                    "   AND (ISNULL(o.ein) OR o.ein = '') " +
                    "   AND (o.manually_run = 1 || (ISNULL(l.error_count) OR l.error_count < 1)) " +
                    "   AND (ISNULL(o.start_after_millis) OR ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) > o.start_after_millis) " +
                    "   AND (ISNULL(o.restarts_count) OR o.restarts_count <= 3) " +
                    "   AND (ISNULL(o.access_denied_count) OR o.access_denied_count <= 5) " +
                    " ORDER BY o.manually_run DESC, o.access_denied_count DESC, o.created_date DESC LIMIT 1")
    /**
     * We do take both 'Processing' and 'On Hold' orders for processing assuming the scenario when auto-fulfillment
     * was switched OFF and some orders were handled with the 'On Hold' status; then later auto-fulfillment was
     * switched to be ON - these few orders need to be handled automatically, since they are ready from the user
     * perspective.
     */
    EinOrder findWaitingEinOrder(String bot_id);

    @Query(
            nativeQuery = true,
            value="SELECT o.* FROM ein_orders o " +
                    " WHERE o.status = 'Processing' " +
                    "   AND o.manually_run = 1 " +
                    "   AND (ISNULL(o.ein) OR o.ein = '') " +
                    "   AND (ISNULL(o.start_after_millis) OR ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) > o.start_after_millis) " +
                    "   AND (ISNULL(o.restarts_count) OR o.restarts_count <= 3) " +
                    "   AND (ISNULL(o.access_denied_count) OR o.access_denied_count <= 5) " +
                    " ORDER BY o.access_denied_count DESC, o.created_date DESC LIMIT 1")
    EinOrder findWaitingEinOrderManual(String bot_id);

}
