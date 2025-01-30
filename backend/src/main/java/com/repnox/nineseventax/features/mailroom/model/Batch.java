package com.repnox.nineseventax.features.mailroom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;



@Entity
@Table(name="mailroom_batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2589803512485318184L;

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "BATCHNUM_GEN")
    @TableGenerator(name = "BATCHNUM_GEN", table = "MAIL_ROOM_BATCHNUM_GEN_TBL", pkColumnName = "BATCHNUM_GEN_NAME", valueColumnName = "BATCHNUM_GEN_VAL", pkColumnValue = "BATCHNUM_GEN_PK", initialValue = 40000, allocationSize = 1)
    private Long id;
	
	@Column(name = "pdf_file")
	private String pdfFile;
	// private byte[] pdfFile;
	
	@Column(name = "shipping_labels")
	private String shippingLabels;
	// private byte[] shippingLabels;

	@Column(name="created_at")
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone="US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
	private Date createdAt;
	
	@Column(name="status")
	private String status;

	@Column(name="is_pdf_download")
    private Boolean isPdfDownload = false;
    
	@Column(name="is_label_download")
	private Boolean isLabelDownload = false;

	@Column(name="start_order_number")
	private Long startOrderNumber;

	@Column(name="end_order_number")
	private Long endOrderNumber;
}
