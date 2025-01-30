package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.repnox.nineseventax.features.payment.request.SettleRequest;
import com.repnox.nineseventax.features.payment.response.SettleResponse;

public interface OrbitalSettleService {

    SettleResponse settle(SettleRequest request) throws Exception;

}
