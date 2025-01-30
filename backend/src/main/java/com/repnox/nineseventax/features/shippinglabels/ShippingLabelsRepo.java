package com.repnox.nineseventax.features.shippinglabels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.processedorders.ProcessedOrders;

public interface ShippingLabelsRepo extends CrudRepository<ShippingLabels, Long> {

	@Transactional
	@Modifying
	@Query("update ShippingLabels r SET r.mailroomBatchId = ?1 where r.processBatchId = ?2")
	void updateMailRoomBatchId(Long mailroomBatchId, Long processBatchId);

	@Query("from ShippingLabels where mailroomBatchId = ?1")
	ArrayList<ShippingLabels> findByMailroomBatchId(Long mailroomBatchId);

	@Query("from ShippingLabels where processBatchId = ?1")
	ArrayList<ShippingLabels> findByProcessBatchId(Long processBatchId);

	@Query("select p from ShippingLabels p where p.trackingNumber = ?1")
	ShippingLabels findByTrackingNumber(String trackingNumber);

	@Query("select p from ShippingLabels p where p.labelId = ?1")
	ShippingLabels findByLabelId(String labelId);

	@Query("from ShippingLabels where order_id = ?1 AND service_code = 'usps_first_class_mail' AND package_code = 'package' ORDER BY id DESC")
	ArrayList<ShippingLabels> findByOrderNumber(Long orderNumber);

	@Modifying
	@Transactional
	@Query("delete from ShippingLabels where order_id = ?1")
	void deleteByOrderId(Long orderNumber);
}
