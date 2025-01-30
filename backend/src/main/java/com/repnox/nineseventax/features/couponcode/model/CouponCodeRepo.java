package com.repnox.nineseventax.features.couponcode.model;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CouponCodeRepo extends PagingAndSortingRepository<CouponCode, Long>, JpaSpecificationExecutor<CouponCode> {
    CouponCode findByCode(String code);
}
