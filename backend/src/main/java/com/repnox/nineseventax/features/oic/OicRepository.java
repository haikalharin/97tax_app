package com.repnox.nineseventax.features.oic;

import com.repnox.nineseventax.features.oic.model.OicModel;
import org.springframework.data.repository.CrudRepository;

public interface OicRepository extends CrudRepository<OicModel, Long> {

    OicModel getByKey(String key);

    OicModel getByOrderNum(Long orderNum);

}
