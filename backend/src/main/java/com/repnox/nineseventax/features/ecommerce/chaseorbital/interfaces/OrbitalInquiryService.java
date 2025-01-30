package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.repnox.nineseventax.features.payment.request.InquiryRequest;
import com.repnox.nineseventax.features.payment.response.InquiryResponse;

public interface OrbitalInquiryService {

    InquiryResponse inquiry(InquiryRequest request) throws Exception;

}
