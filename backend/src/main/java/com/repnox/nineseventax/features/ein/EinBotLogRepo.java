package com.repnox.nineseventax.features.ein;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EinBotLogRepo extends JpaRepository<EinBotLog, Long> {

    @Query(value = "SELECT * FROM ein_bot_log WHERE ein_order_id = ?1 ORDER BY created_date DESC LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<EinBotLog> findByOrderId(Long id, int offset, int size);

    @Query("SELECT COUNT(log) FROM EinBotLog log WHERE log.ein_order_id = ?1")
    long getCountByOrderId(Long id);

}
