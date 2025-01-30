package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.repnox.nineseventax.features.payment.request.ProfileRequest;
import com.repnox.nineseventax.features.payment.response.ProfileResponse;

public interface OrbitalUpdateProfileService {

    ProfileResponse update(ProfileRequest request) throws Exception;

}
