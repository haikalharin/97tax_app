package com.repnox.nineseventax.features.partnercode.model;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PartnerCodeRepo extends PagingAndSortingRepository<PartnerCode, Long>, JpaSpecificationExecutor<PartnerCode> {
    PartnerCode findByCode(String code);
}
