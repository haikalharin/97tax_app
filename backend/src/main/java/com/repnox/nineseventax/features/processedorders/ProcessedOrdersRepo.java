package com.repnox.nineseventax.features.processedorders;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ProcessedOrdersRepo extends CrudRepository<ProcessedOrders, Long> {

	@Query("select p from ProcessedOrders p where p.versionId = ?1")
	ProcessedOrders findByVersionId(String versionId);
	
    @Query("from ProcessedOrders where sentToMailroom=?1")
    ArrayList<ProcessedOrders> findUnsentProcessedOrders(boolean sentToMailroom);
    
    @Modifying
    @Query("delete from ProcessedOrders u where u.sentToMailroom=?1")
    void deleteProcessedOrders(boolean sentToMailroom);
    
    
    @Modifying
    @Transactional
    @Query("delete from ProcessedOrders u where u.versionId = ?1")
    void deleteByVersionId(String versionId);
    
    @Modifying
    @Transactional
    @Query("delete from ProcessedOrders")
    void deleteAll();
    
    @Modifying
    @Transactional
    @Query("delete from ProcessedOrders u where u.createdDate < ?1")
    void deleteByCreatedDateBefore(Date date);
  
}
