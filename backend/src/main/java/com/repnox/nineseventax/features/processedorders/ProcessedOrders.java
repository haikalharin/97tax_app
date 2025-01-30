package com.repnox.nineseventax.features.processedorders;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "processed_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedOrders implements Serializable {
  
    private static final long serialVersionUID = -9109516121940075735L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")

    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "M/d/yyyy hh:mm:ss a", timezone = "US/Eastern")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date createdDate;

    @Column(name = "pdfFile")
    private String pdfFile;
    // private byte[] pdfFile;
    
    @Column(name = "shipping_labels")
    private String shippingLabels;
    // private byte[] shippingLabels;
    
    @Column(name="versionId")
    private String versionId;

    @Column(name="sentToMailroom")
    private boolean sentToMailroom;
    
    private Long startOrderNumber;

    private Long endOrderNumber;
    
    private Integer ordersCounter;
}
