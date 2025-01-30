package com.repnox.nineseventax.features.ecommerce;

import com.repnox.nineseventax.features.partnercode.model.PCUsageReport;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;



public interface OrderRecordRepo
        extends PagingAndSortingRepository<OrderRecord, Long>, JpaSpecificationExecutor<OrderRecord> {

    OrderRecord findByCorrelationId(String correlationId);

    OrderRecord findByAuthorizeTransactionId(String authTransId);

    @Transactional
    @Modifying
    @Query("update OrderRecord r set r.status='Deleted' where r.id in ?1")
    void markForDeletion(List<Long> ids);

    @Query("from OrderRecord o where o.createdDate between :startDate and :endDate")
    List<OrderRecord> findByOrderDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("from OrderRecord where email = ?1 and status in ?2 and createdDate > ?3 and product = ?4 and is_california is not true and is_new_jersey is not true and is_georgia is not true")
    List<OrderRecord> findPotentialDuplicateForIRS(String email, List<String> statuses, Date cutoff, String product);

    @Query("from OrderRecord where email = ?1 and status in ?2 and createdDate > ?3 and product = ?4 and is_california = true")
    List<OrderRecord> findPotentialDuplicateForCalifornia(String email, List<String> statuses, Date cutoff,
            String product);

    @Query("from OrderRecord where email = ?1 and status in ?2 and createdDate > ?3 and product = ?4 and is_new_jersey = true")
    List<OrderRecord> findPotentialDuplicateForNewJersey(String email, List<String> statuses, Date cutoff,
            String product);

    @Query("from OrderRecord where email = ?1 and status in ?2 and createdDate > ?3 and product = ?4 and is_georgia = true")
    List<OrderRecord> findPotentialDuplicateForGeorgia(String email, List<String> statuses, Date cutoff,
            String product);

    @Query("from OrderRecord where email = ?1 and status in ?2 and createdDate > ?3 and product = ?4 and is_illinois = true")
    List<OrderRecord> findPotentialDuplicateForIllinois(String email, List<String> statuses, Date cutoff,
             String product);

    @Query("from OrderRecord where email = ?1 and status in ?2 and createdDate > ?3 and product = ?4 and is_michigan = true")
    List<OrderRecord> findPotentialDuplicateForMichigan(String email, List<String> statuses, Date cutoff,
                                                        String product);

    @Query("from OrderRecord where (email = ?1 or phone = ?2) and status in ?3 and is_california is not true and is_new_jersey is not true and is_georgia is not true")
    List<OrderRecord> findPotentialDuplicate2ForIRS(String email, String phone, List<String> statuses);

    @Query("from OrderRecord where (email = ?1 or phone = ?2) and status in ?3 and is_california = true")
    List<OrderRecord> findPotentialDuplicate2ForCalifornia(String email, String phone, List<String> statuses);

    @Query("from OrderRecord where (email = ?1 or phone = ?2) and status in ?3 and is_new_jersey = true")
    List<OrderRecord> findPotentialDuplicate2ForNewJersey(String email, String phone, List<String> statuses);

    @Query("from OrderRecord where (email = ?1 or phone = ?2) and status in ?3 and is_georgia = true")
    List<OrderRecord> findPotentialDuplicate2ForGeorgia(String email, String phone, List<String> statuses);

    @Query("from OrderRecord where (email = ?1 or phone = ?2) and status in ?3 and is_illinois = true")
    List<OrderRecord> findPotentialDuplicate2ForIllinois(String email, String phone, List<String> statuses);

    @Query("from OrderRecord where email = ?1 and status in ?2 and createdDate >= ?3 and product = ?4")
    List<OrderRecord> findOrdersWithEmailAndStatusAndTime(String email, List<String> statuses, Date cutoff,String product);
    
    @Query("from OrderRecord where status='Processing'")
    List<OrderRecord> getProcessStatusList();

    @Query("Select orderRecord from OrderRecord orderRecord where orderRecord.email = :email "
            + "and orderRecord.status = :status and orderRecord.product = :product order by orderRecord.createdDate asc")
    List<OrderRecord> findByStatusAndEmailAndProductOrderByCreatedDate(@Param("email") String email,
            @Param("status") String status, @Param("product") String product);
    
    @Query("from OrderRecord where email = ?1 and status in ?2 and product = ?3")
    List<OrderRecord> findByStatusAndEmailAndProduct(String email, List<String> statuses, String product);
    
    // @Query(
    //     value = 
	// 		"Select report.partner_code, sum(report.amount), count(report.partner_code) from " 
	// 		+ "("
	// 		+ "Select o.partner_code, o.amount from orders o "
	// 		+ "left join partner_codes p on o.partner_code = p.code "
	// 		+ "where o.partner_code IS NOT NULL and o.partner_code != '' and o.created_date < ?2 and o.created_date > ?1 "
	// 		+ ") "
	// 		+ "as report "
	// 		+ "group by report.partner_code",
	// 	nativeQuery = true)
	@Query(
		"SELECT " + " new com.repnox.nineseventax.features.partnercode.model.PCUsageReport(p.partnerName, sum(o.amount), o.partnerCode, count(o.partnerCode), p.commission, p.commissionTypeId, p.contactName, o.phone, o.email, p.notes) " +
		// "SELECT o " +
		"FROM OrderRecord o " +
		"INNER JOIN PartnerCode p ON o.partnerCode = p.code " +
		"WHERE o.partnerCode IS NOT NULL AND o.partnerCode != '' AND o.createdDate <= ?2 AND o.createdDate >= ?1 " +
		"group by o.partnerCode"
	)
	List<PCUsageReport> findOrdersForUsageReport(Date startDate, Date endDate);

    List<OrderRecord> findOrdersByEmail(String email);
    
    @Query("from OrderRecord where status = ?1 ")
    List<OrderRecord> findOrdersByStatus(String status);
   
    @Query("from OrderRecord where status = 'Processing' and (shipengineStatusCode is NULL or shipengineStatusCode = '' or shipengineStatusCode <> 'in_transit') ORDER BY id DESC") 
    List<OrderRecord> findOrderInProcessingAndNotShipengine();
    
    @Query(
        value = "SELECT r.* from orders r where r.status = 'On Hold' and datediff(now(), r.status_last_changed) >= 2",
        nativeQuery = true
        ) 
    List<OrderRecord> findStaleOnHoldOrders();
        
    @Transactional
    @Modifying
	@Query("update OrderRecord r SET r.trackingNumber = ?1, r.shipengineStatusCode = ?2 where r.id = ?3")
	void updateShippingTrackingByOrderId(String trackingNumber,String shipengineStatusCode, Long orderId);

    OrderRecord findByOrbitalTransactionNumber(String transactionNumber);

}
