package com.repnox.nineseventax.features.couponcode.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.io.Serializable;

@Entity
@Table(name="coupon_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponCode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2589803512485318184L;

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CODENUM_GEN")
    @TableGenerator(name = "CODENUM_GEN", table = "COUPON_CODENUM_GEN_TBL", pkColumnName = "CODENUM_GEN_NAME", valueColumnName = "CODENUM_GEN_VAL", pkColumnValue = "CODENUM_GEN_PK", initialValue = 40000, allocationSize = 1)
    private Long codeNum;

	@Column(name = "code")
	private String code;
	
	@Column(name = "percentageOff")
	private Float percentageOff;

	@Column(name = "partnerName")
	private String partnerName;

	@Column(name = "contactName")
	private String contactName;
	
	@Column(name = "commission")
	private Integer commission;

	@Column(name = "commissionTypeId")
	private Integer commissionTypeId;

	@Column(name = "streetAddress")
	private String streetAddress;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "zip")
	private String zip;

	@Column(name = "notes")
	private String notes;
}
