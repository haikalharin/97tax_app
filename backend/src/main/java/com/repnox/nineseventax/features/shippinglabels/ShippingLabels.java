package com.repnox.nineseventax.features.shippinglabels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "shipping_labels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingLabels implements Serializable {
  
    private static final long serialVersionUID = -9109516121940075735L;
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    
	private String labelId;
	
	private String status;
	
	private String shipmentId;

	private String shipDate;
	
	private String createdAt;

	private String trackingNumber;

	private String carrierId;
	
	private String serviceCode;
	
	private String packageCode;

	private String labelFormat;

	private String labelLayout;
	 
	private String trackingStatus;
	
	@Column(columnDefinition = "LONGTEXT")
	private String errorMessage;
	
	private Long processBatchId;
	 
	private Long mailroomBatchId;
	
	private String voidStatus; 

	private String labelUrl;
	
	private String statusCode;
	
	private String statusDescription;

	private String carrierStatusCode;
	
	private String carrierStatusDescription;

	private String estimatedDeliveryDate;
	
	private String actualDeliveryDate;
}