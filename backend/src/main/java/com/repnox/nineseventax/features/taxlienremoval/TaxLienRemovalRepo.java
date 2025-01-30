package com.repnox.nineseventax.features.taxlienremoval;

import org.springframework.data.repository.CrudRepository;

public interface TaxLienRemovalRepo extends CrudRepository<TaxLienRemovalDetails, Long> {

    TaxLienRemovalDetails getByOrderNum(Long orderNum);

}
