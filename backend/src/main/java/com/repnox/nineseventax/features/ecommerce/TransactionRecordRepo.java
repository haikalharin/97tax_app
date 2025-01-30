package com.repnox.nineseventax.features.ecommerce;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransactionRecordRepo extends JpaRepository<TransactionRecord, Long> {
    Page<TransactionRecord> findByAuthCode(String authCode, Pageable pageable);
}
