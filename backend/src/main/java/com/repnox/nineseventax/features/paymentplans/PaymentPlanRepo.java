package com.repnox.nineseventax.features.paymentplans;

import org.springframework.data.repository.CrudRepository;

public interface PaymentPlanRepo extends CrudRepository<PaymentPlanDetails, Long>  {

    PaymentPlanDetails findByOrderNum(Long orderNum);

}
