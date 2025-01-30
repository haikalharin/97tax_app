package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.repnox.nineseventax.features.payment.response.ProfileResponse;

public interface OrbitalFetchProfileService {

    ProfileResponse fetch(String version, String bin, String customerRefNum) throws Exception;

}
