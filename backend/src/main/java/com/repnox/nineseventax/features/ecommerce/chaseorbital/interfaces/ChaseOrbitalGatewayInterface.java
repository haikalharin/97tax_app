package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.repnox.nineseventax.features.ecommerce.chaseorbital.CustomerObject;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.OrbitalResponse;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.SemplestString;

import java.util.List;

public interface ChaseOrbitalGatewayInterface extends ServiceInitialize {
    OrbitalResponse CreateProfile(CustomerObject customerObject) throws Exception;
	List<CustomerObject> GetProfiles(List<String> customerProfileRefNumbers) throws Exception;
	OrbitalResponse CopyProfile(SemplestString customerProfileRefNumber) throws Exception;
	OrbitalResponse AuthorizeAndCapture(String customerProfileRefNumber, Double Amount, String cardSecVal) throws Exception;
	OrbitalResponse UpdateProfileRecurringBilling(String customerProfileRefNumber, Double recurringAmount, java.util.Date startDate, int dayOfMonth) throws Exception;
	OrbitalResponse terminateRecurringPayments(SemplestString customerProfileRefNumber) throws Exception;
}
