package com.repnox.nineseventax.features.ecommerce.chaseorbital;

import com.paymentech.orbital.sdk.interfaces.RequestIF;
import com.paymentech.orbital.sdk.interfaces.ResponseIF;
import com.paymentech.orbital.sdk.request.FieldNotFoundException;
import com.paymentech.orbital.sdk.request.Request;
import com.paymentech.orbital.sdk.transactionProcessor.TransactionException;
import com.paymentech.orbital.sdk.util.exceptions.InitializationException;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.ChaseOrbitalGatewayInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
 * Instructions for Processing
 * 
 * Note: Each Campaign is charged a stream of payments billed on a periodic cycle.  Each orbital profile can represent one credit card with a stream of payments.
 * 
 * 1.  Create a profile (or copy a profile by retrieving a profile (including full CC #)
 * 2.  Authenticate and Capture a charge
 * 3.  Update profile - set recurring billing on profile
 * 
 *     To Cancel: Update profile with end date or delete profile 
 *  4. Refund - Send New Order request    
 */

public class ChaseOrbitalGatewayServiceImpl implements ChaseOrbitalGatewayInterface {
	public static ChaseOrbitalGatewayObject gatewayObj = null;
	private static final Logger logger = LoggerFactory.getLogger(ChaseOrbitalGatewayServiceImpl.class);

	@Override
	public void initializeService(String input) throws Exception {
		/*
		 * Initialize gateway
		 */
		Object object = new Object();
		gatewayObj = new ChaseOrbitalGatewayObject(object);
		gatewayObj.start();
		synchronized (object) {
			object.wait();
		}

		logger.info("Initialized Chase Orbital Gateway");
	}

	@Override
	public OrbitalResponse CreateProfile(CustomerObject customerObject) throws Exception {
		logger.info("Will try to CreateProfile for [" + customerObject + "]");
		Request request = null;
		try {
			request = new Request(RequestIF.PROFILE_TRANSACTION);
			// the follow will create a profile that can be used in future transactions
			request.setFieldValue("OrbitalConnectionUsername", ChaseOrbitalGatewayObject.ORBITAL_USERNAME);
			request.setFieldValue("OrbitalConnectionPassword", ChaseOrbitalGatewayObject.ORBITAL_PASSWORD);
			request.setFieldValue("CustomerBin", "000002");
			request.setFieldValue("CustomerMerchantID", "700000013809");
			// Customer Info
			request.setFieldValue("CustomerName", customerObject.getName());
			request.setFieldValue("CustomerAddress1", customerObject.getAddress1());
			if (customerObject.getAddress2() != null && customerObject.getAddress2().trim().length() > 0) {
				request.setFieldValue("CustomerAddress2", customerObject.getAddress2());
			}

			request.setFieldValue("CustomerCity", customerObject.getCity());
			request.setFieldValue("CustomerState", customerObject.getStateAbbr());
			request.setFieldValue("CustomerZIP", customerObject.getZipCode());
			request.setFieldValue("CustomerPhone", customerObject.getPhone());
			// Create
			request.setFieldValue("CustomerProfileAction", "C");
			request.setFieldValue("CustomerProfileOrderOverrideInd", "NO");
			// Auto return profileID
			request.setFieldValue("CustomerProfileFromOrderInd", "A");
			request.setFieldValue("OrderDefaultAmount", "1050");
			request.setFieldValue("OrderDefaultDescription", "Profile Create");
			// Account type credit card
			request.setFieldValue("CustomerAccountType", "CC");
			request.setFieldValue("CCAccountNum", customerObject.getCreditCardNumber());
			request.setFieldValue("CCExpireDate", customerObject.getExpireDateMMYY());
			// Display the request
			logger.debug("\nProfile Request:\n" + request.getXML());
		} catch (InitializationException ie) {
			final String errMsg = "Unable to initialize request object";
			logger.error(errMsg, ie);
			throw new Exception(errMsg, ie);
		} catch (FieldNotFoundException fnfe) {
			final String errMsg = "Unable to find XML field in template";
			logger.error(errMsg, fnfe);
			throw new Exception(errMsg, fnfe);
		} catch (Exception ex) {
			final String errMsg = "Problem creating profile";
			logger.error(errMsg, ex);
			throw new Exception(errMsg, ex);
		}

		// run request
		ResponseIF response = null;
		try {
			response = gatewayObj.getTransactionProcessor().process(request);
		} catch (TransactionException tex) {
			logger.error("Transaction failed, including retries and failover " + tex.getMessage(), tex);
			throw new Exception("Transaction failed, including retries and failover" + ":" + tex.getMessage(), tex);
		}

		OrbitalResponse ret = OrbitalResponse.builder().build();
		// Display the response
		// This line displays the entire xml response on the java system
		// console.
		ret.setXmlReturn(response.toXmlString());
		ret.setIsGood(response.isGood());
		ret.setIsError(response.isError());
		ret.setIsQuickResponse(response.isQuickResponse());
		ret.setIsApproved(response.isApproved());
		ret.setIsDeclined(response.isDeclined());
		ret.setAuthCode(response.getAuthCode());
		ret.setTxRefNum(response.getTxRefNum());
		ret.setResponseCode(response.getResponseCode());
		ret.setStatus(response.getStatus());
		ret.setMessage(response.getMessage());
		ret.setAVSCode(response.getAVSResponseCode());
		ret.setCVV2ResponseCode(response.getCVV2RespCode());

		// profile specific
		ret.setCustomerRefNum(response.getValue("CustomerRefNum"));
		return ret;
	}

	@Override
	public OrbitalResponse AuthorizeAndCapture(String customerProfileRefNumber, Double Amount,
											   final String cardSecVal) throws Exception {
		logger.info("Will try to AuthorizeAndCampture for CustomerProfileRefNumber [" + customerProfileRefNumber
				+ "], Amount [" + Amount + "], CardSecVal [" + cardSecVal + "]");

		RequestIF request = null;
		try {
			// Tell the request object which template to use (see RequestIF.java)
			request = new Request(RequestIF.NEW_ORDER_TRANSACTION);
			// Basic Information
			request.setFieldValue("OrbitalConnectionUsername", ChaseOrbitalGatewayObject.ORBITAL_USERNAME);
			request.setFieldValue("OrbitalConnectionPassword", ChaseOrbitalGatewayObject.ORBITAL_PASSWORD);
			request.setFieldValue("IndustryType", "EC"); // eCommerce transaction
			request.setFieldValue("MessageType", "AC"); // Authorize and mark for capture
			request.setFieldValue("BIN", "000002");
			request.setFieldValue("MerchantID", "700000013809");

			request.setFieldValue("CardSecVal", cardSecVal);
			request.setFieldValue("CardSecValInd", "1");

			// Use Profile Data
			request.setFieldValue("CustomerRefNum", customerProfileRefNumber);
			// Generate random orderID
			String orderID = generateRandomOrderID(ChaseOrbitalGatewayObject.ORDERIDSTRING,
					ChaseOrbitalGatewayObject.ORDERIDLENGTH);
			request.setFieldValue("OrderID", orderID);
			Integer amountImpliedDecimal = (new Double(Amount * 100.0)).intValue();
			request.setFieldValue("Amount", String.valueOf(amountImpliedDecimal));

			// Display the request
			logger.info("AuthorizeAndCapture Request profile=" + customerProfileRefNumber + " orderID=" + orderID
					+ " : Amount (No decimal)=" + amountImpliedDecimal);
		} catch (InitializationException e) {
			final String errMsg = "Problem doing AuthorizeAndCampture for CustomerProfileRefNumber ["
					+ customerProfileRefNumber + "], Amount [" + Amount + "]";
			logger.error(errMsg, e);
			throw new Exception(errMsg, e);
		} catch (FieldNotFoundException e) {
			final String errMsg = "Problem doing AuthorizeAndCampture for CustomerProfileRefNumber ["
					+ customerProfileRefNumber + "], Amount [" + Amount + "]";
			logger.error(errMsg, e);
			throw new Exception(errMsg, e);
		} catch (Exception e) {
			final String errMsg = "Problem doing AuthorizeAndCampture for CustomerProfileRefNumber ["
					+ customerProfileRefNumber + "], Amount [" + Amount + "]";
			logger.error(errMsg, e);
			throw new Exception(errMsg, e);
		}

		// Process Transaction
		ResponseIF response = null;
		try {
			response = gatewayObj.getTransactionProcessor().process(request);
		} catch (TransactionException tex) {
			logger.error(tex.getMessage(), tex);
			throw new Exception("Transaction failed, including retries and failover " + tex.getMessage(), tex);
		}

		OrbitalResponse ret = OrbitalResponse.builder().build();
		// This line displays the entire xml response on the java system
		// console.
		ret.setXmlReturn(response.toXmlString());
		ret.setIsGood(response.isGood());
		ret.setIsError(response.isError());
		ret.setIsQuickResponse(response.isQuickResponse());
		ret.setIsApproved(response.isApproved());
		ret.setIsDeclined(response.isDeclined());
		ret.setAuthCode(response.getAuthCode());
		ret.setTxRefNum(response.getTxRefNum());
		ret.setResponseCode(response.getResponseCode());
		ret.setStatus(response.getStatus());
		ret.setMessage(response.getMessage());
		ret.setAVSCode(response.getAVSResponseCode());
		ret.setCVV2ResponseCode(response.getCVV2RespCode());

		// New Order Specific fields
		ret.setOrderId(response.getValue("OrderID"));
		if (!response.isError()) {
			ret.setAmountRequestedNoDecimal(String.valueOf(Amount));
			ret.setAmountRedeemedNoDecimal(String.valueOf(Amount));
		}

		return ret;
	}

	public OrbitalResponse AuthorizeAndCaptureWithSecureInfo(String customerProfileRefNumber, Double Amount,
															 final String cardSecVal, OrderRecord orderRecord) throws Exception {
		logger.info("Will try to AuthorizeAndCampture for CustomerProfileRefNumber [" + customerProfileRefNumber
				+ "], Amount [" + Amount + "], CardSecVal [" + cardSecVal + "]");
		// Create a request object
		// The request object uses the XML templates along with data we provide
		// to generate a String representation of the xml request

		RequestIF request = null;
		try {
			// Tell the request object which template to use (see RequestIF.java)
			request = new Request(RequestIF.NEW_ORDER_TRANSACTION);
			// Basic Information
			request.setFieldValue("OrbitalConnectionUsername", ChaseOrbitalGatewayObject.ORBITAL_USERNAME);
			request.setFieldValue("OrbitalConnectionPassword", ChaseOrbitalGatewayObject.ORBITAL_PASSWORD);
			request.setFieldValue("IndustryType", "EC");// eCommerce transaction
			request.setFieldValue("MessageType", "AC"); // Authorize and mark for capture
			request.setFieldValue("BIN", "000002");
			request.setFieldValue("MerchantID", "700000013809");
			request.setFieldValue("CardSecVal", cardSecVal);
			request.setFieldValue("CardSecValInd", "1");
			request.setFieldValue("AuthenticationECIInd", orderRecord.getAuthEci());
			request.setFieldValue("CAVV", orderRecord.getAuthCavv());
			request.setFieldValue("XID", orderRecord.getAuthXid());
			// Use Profile Data
			request.setFieldValue("CustomerRefNum", customerProfileRefNumber);

			// Generate random orderID
			String orderID = generateRandomOrderID(ChaseOrbitalGatewayObject.ORDERIDSTRING,
					ChaseOrbitalGatewayObject.ORDERIDLENGTH);

			request.setFieldValue("OrderID", orderID);
			request.setFieldValue("Amount", orderRecord.getAmount());

		} catch (InitializationException e) {
			final String errMsg = "Problem doing AuthorizeAndCampture for CustomerProfileRefNumber ["
					+ customerProfileRefNumber + "], Amount [" + Amount + "]";
			logger.error(errMsg, e);
			throw new Exception(errMsg, e);
		} catch (FieldNotFoundException e) {
			final String errMsg = "Problem doing AuthorizeAndCampture for CustomerProfileRefNumber ["
					+ customerProfileRefNumber + "], Amount [" + Amount + "]";
			logger.error(errMsg, e);
			throw new Exception(errMsg, e);
		} catch (Exception e) {
			final String errMsg = "Problem doing AuthorizeAndCampture for CustomerProfileRefNumber ["
					+ customerProfileRefNumber + "], Amount [" + Amount + "]";
			logger.error(errMsg, e);
			throw new Exception(errMsg, e);
		}

		// Process Transaction
		ResponseIF response = null;
		try {
			System.out.println(request.getXML());
			response = gatewayObj.getTransactionProcessor().process(request);
		} catch (TransactionException tex) {
			logger.error(tex.getMessage(), tex);
			throw new Exception("Transaction failed, including retries and failover " + tex.getMessage(), tex);
		}

		OrbitalResponse ret = OrbitalResponse.builder().build();
		ret.setXmlReturn(response.toXmlString());
		ret.setIsGood(response.isGood());
		ret.setIsError(response.isError());
		ret.setIsQuickResponse(response.isQuickResponse());
		ret.setIsApproved(response.isApproved());
		ret.setIsDeclined(response.isDeclined());
		ret.setAuthCode(response.getAuthCode());
		ret.setTxRefNum(response.getTxRefNum());
		ret.setResponseCode(response.getResponseCode());
		ret.setStatus(response.getStatus());
		ret.setMessage(response.getMessage());
		ret.setAVSCode(response.getAVSResponseCode());
		ret.setCVV2ResponseCode(response.getCVV2RespCode());

		// New Order Specific fields
		ret.setOrderId(response.getValue("OrderID"));
		if (!response.isError()) {
			ret.setAmountRequestedNoDecimal(String.valueOf(Amount));
			ret.setAmountRedeemedNoDecimal(String.valueOf(Amount));
		}

		return ret;
	}

	/*
	 * return a random string of length returnLength
	 */
	private static String generateRandomOrderID(String charString, int returnLength) {
		String random = "";
		for (int j = 0; j < returnLength; j++) {
			random += charString.charAt((int) (Math.random() * charString.length()));
		}
		return random;
	}

	@Override
	public OrbitalResponse UpdateProfileRecurringBilling(String customerProfileRefNumber, Double recurringAmount,
														 java.util.Date startDate, final int dayOfMonth) throws Exception {
		logger.info("Will try to UpdateProfileRecurringBilling for CustomerProfileRefNumber ["
				+ customerProfileRefNumber + "], RecurringAmount [" + recurringAmount + "], StartDate [" + startDate
				+ "], DayOfMonth [" + dayOfMonth + "]");
		RequestIF request = null;
		try {
			final Calendar endDateCal = Calendar.getInstance();
			endDateCal.setTime(startDate);
			endDateCal.add(Calendar.YEAR, 10);
			final java.util.Date endDate = endDateCal.getTime();
			final String endingDate = ChaseOrbitalGatewayObject.MMDDYYYY.format(endDate);
			final String startingDate = ChaseOrbitalGatewayObject.MMDDYYYY.format(startDate);
			request = new Request(RequestIF.PROFILE_TRANSACTION);
			request.setFieldValue("OrbitalConnectionUsername", ChaseOrbitalGatewayObject.ORBITAL_USERNAME);
			request.setFieldValue("OrbitalConnectionPassword", ChaseOrbitalGatewayObject.ORBITAL_PASSWORD);
			request.setFieldValue("CustomerBin", ChaseOrbitalGatewayObject.MERCHANT_BIN_ID);
			request.setFieldValue("CustomerMerchantID", ChaseOrbitalGatewayObject.MERCHANT_TERMINAL_ID);

			// Update profile using CustomerRefNum
			request.setFieldValue("CustomerProfileAction", "U");
			request.setFieldValue("CustomerProfileFromOrderInd", "S"); // use Profile CustomerRefNum
			request.setFieldValue("CustomerRefNum", customerProfileRefNumber);
			request.setFieldValue("OrderDefaultDescription", "Profile Update");
			final Double amountDouble = recurringAmount * 100.0;
			final int amount = amountDouble.intValue();
			logger.info(recurringAmount + " -> " + amount);
			request.setFieldValue("OrderDefaultAmount", "" + amount);

			// Managed Billing
			request.setFieldValue("MBType", "R"); // Recurring
			request.setFieldValue("MBOrderIdGenerationMethod", "DI"); // Dynamically generate orderID
			request.setFieldValue("MBRecurringStartDate", startingDate);
			request.setFieldValue("MBRecurringEndDate", endingDate);
			request.setFieldValue("MBRecurringFrequency", dayOfMonth + " * ?"); // Bill Monthly
			logger.debug("\nProfile Request:\n" + request.getXML());
		} catch (InitializationException ie) {
			logger.error("Unable to initialize request object " + ie.getMessage(), ie);
			throw new Exception("Unable to initialize request object" + ":" + ie.getMessage(), ie);
		} catch (FieldNotFoundException fnfe) {
			logger.error("Unable to find XML field in template " + fnfe.getMessage(), fnfe);
			throw new Exception("Unable to find XML field in template" + ":" + fnfe.getMessage(), fnfe);
		} catch (Exception ex) {
			logger.error("Problem", ex);
			throw ex;
		}

		// run request
		ResponseIF response = null;
		try {
			response = gatewayObj.getTransactionProcessor().process(request);
		} catch (TransactionException tex) {
			logger.error("Transaction failed, including retries and failover", tex);
			throw new Exception("Transaction failed, including retries and failover" + ":" + tex.getMessage(), tex);
		}

		OrbitalResponse ret = OrbitalResponse.builder().build();
		ret.setXmlReturn(response.toXmlString());
		ret.setIsGood(response.isGood());
		ret.setIsError(response.isError());
		ret.setIsQuickResponse(response.isQuickResponse());
		ret.setIsApproved(response.isApproved());
		ret.setIsDeclined(response.isDeclined());
		ret.setAuthCode(response.getAuthCode());
		ret.setTxRefNum(response.getTxRefNum());
		ret.setResponseCode(response.getResponseCode());
		ret.setStatus(response.getStatus());
		ret.setMessage(response.getMessage());
		ret.setAVSCode(response.getAVSResponseCode());
		ret.setCVV2ResponseCode(response.getCVV2RespCode());
		return ret;
	}

	@Override
	public OrbitalResponse terminateRecurringPayments(SemplestString customerProfileRefNumber) throws Exception {
		logger.info("Got request to terminate recurring payments for Customer Profile Ref Number ["
				+ customerProfileRefNumber + "]");
		RequestIF request = null;
		try {
			request = new Request(RequestIF.PROFILE_TRANSACTION);
			request.setFieldValue("OrbitalConnectionUsername", ChaseOrbitalGatewayObject.ORBITAL_USERNAME);
			request.setFieldValue("OrbitalConnectionPassword", ChaseOrbitalGatewayObject.ORBITAL_PASSWORD);
			request.setFieldValue("CustomerBin", ChaseOrbitalGatewayObject.MERCHANT_BIN_ID);
			request.setFieldValue("CustomerMerchantID", ChaseOrbitalGatewayObject.MERCHANT_TERMINAL_ID);
			request.setFieldValue("CustomerProfileAction", "D");
			request.setFieldValue("CustomerProfileFromOrderInd", "S"); // tells Orbital to use the provided customer
																		// profile ref num
			request.setFieldValue("CustomerRefNum", customerProfileRefNumber.getSemplestString());
		} catch (InitializationException ie) {
			final String errMsg = "Unable to initialize request object";
			logger.error(errMsg, ie);
			throw new Exception(errMsg, ie);
		} catch (FieldNotFoundException fnfe) {
			final String errMsg = "Unable to find XML field in template";
			logger.error(errMsg, fnfe);
			throw new Exception(errMsg, fnfe);
		} catch (Exception ex) {
			final String errMsg = "Problem creating profile";
			logger.error(errMsg, ex);
			throw new Exception(errMsg, ex);
		}
		// run request
		ResponseIF response = null;
		try {
			response = gatewayObj.getTransactionProcessor().process(request);
		} catch (TransactionException tex) {
			final String errMsg = "Transaction failed, including retries and failover";
			logger.error(errMsg, tex);
			throw new Exception(errMsg, tex);
		}

		final OrbitalResponse ret = OrbitalResponse.builder().build();
		ret.setXmlReturn(response.toXmlString());
		ret.setIsGood(response.isGood());
		ret.setIsError(response.isError());
		ret.setIsQuickResponse(response.isQuickResponse());
		ret.setIsApproved(response.isApproved());
		ret.setIsDeclined(response.isDeclined());
		ret.setAuthCode(response.getAuthCode());
		ret.setTxRefNum(response.getTxRefNum());
		ret.setResponseCode(response.getResponseCode());
		ret.setStatus(response.getStatus());
		ret.setMessage(response.getMessage());
		ret.setAVSCode(response.getAVSResponseCode());
		ret.setCVV2ResponseCode(response.getCVV2RespCode());
		// profile specific
		ret.setCustomerRefNum(response.getValue("CustomerRefNum"));
		return ret;
	}

	public CustomerObject getProfile(final String customerReferenceNumber) throws Exception {
		logger.info("Will try to Get Profile for CustomerProfileRefNumbers [" + customerReferenceNumber + "]");
		RequestIF request = null;

		try {
			request = new Request(RequestIF.PROFILE_TRANSACTION);
			request.setFieldValue("OrbitalConnectionUsername", ChaseOrbitalGatewayObject.ORBITAL_USERNAME);
			request.setFieldValue("OrbitalConnectionPassword",
					gatewayObj.getAes().encrypt(ChaseOrbitalGatewayObject.ORBITAL_PASSWORD));
			request.setFieldValue("CustomerBin", ChaseOrbitalGatewayObject.MERCHANT_BIN_ID);
			request.setFieldValue("CustomerMerchantID", ChaseOrbitalGatewayObject.MERCHANT_TERMINAL_ID);
			request.setFieldValue("CustomerProfileAction", "R");
			request.setFieldValue("CustomerProfileFromOrderInd", "S");
			request.setFieldValue("CustomerRefNum", customerReferenceNumber);
		} catch (InitializationException ie) {
			final String errMsg = "Unable to initialize request object";
			logger.error(errMsg, ie);
			throw new Exception(errMsg, ie);
		} catch (FieldNotFoundException fnfe) {
			final String errMsg = "Unable to find XML field in template";
			logger.error(errMsg, fnfe);
			throw new Exception(errMsg, fnfe);
		} catch (Exception ex) {
			final String errMsg = "Problem getting profile information";
			logger.error(errMsg, ex);
			throw new Exception(errMsg, ex);
		}

		ResponseIF response = null;
		try {
			response = gatewayObj.getTransactionProcessor().process(request);
		} catch (TransactionException tex) {
			final String errMsg = "Transaction failed, including retries and failover";
			logger.error(errMsg, tex);
			throw new Exception(errMsg, tex);
		}

		final String customerAddress1 = response.getValue("CustomerAddress1");
		final String customerAddress2 = response.getValue("CustomerAddress2");
		final String customerCity = response.getValue("CustomerCity");
		final String customerState = response.getValue("CustomerState");
		final String customerZip = response.getValue("CustomerZIP");
		final String customerEmail = response.getValue("CustomerEmail");
		final String phone = response.getValue("CustomerPhone");
		final String creditCardNumber = response.getValue("CCAccountNum");
		final String creditCardExpireDate = response.getValue("CCExpireDate");
		final String customerName = response.getValue("CustomerName");

		final OrbitalResponse ret = OrbitalResponse.builder().build();
		ret.setXmlReturn(response.toXmlString());
		ret.setIsGood(response.isGood());
		ret.setIsError(response.isError());
		ret.setIsQuickResponse(response.isQuickResponse());
		ret.setIsApproved(response.isApproved());
		ret.setIsDeclined(response.isDeclined());
		ret.setAuthCode(response.getAuthCode());
		ret.setTxRefNum(response.getTxRefNum());
		ret.setResponseCode(response.getResponseCode());
		ret.setStatus(response.getStatus());
		ret.setMessage(response.getMessage());
		ret.setAVSCode(response.getAVSResponseCode());
		ret.setCVV2ResponseCode(response.getCVV2RespCode());

		try {
			logger.info("Response: " + ret.toString());
		} catch (Exception e) {
			logger.error("Problem generating formatted string for GatewayReturnObject [" + ret + "]", e);
		}

		final CustomerObject customer = new CustomerObject();
		customer.setCustomerProfileRefNum(customerReferenceNumber);
		customer.setAddress1(customerAddress1);
		customer.setAddress2(customerAddress2);
		customer.setCity(customerCity);
		customer.setCreditCardNumber(creditCardNumber);
		customer.setEmail(customerEmail);
		customer.setExpireDateMMYY(creditCardExpireDate);
		customer.setName(customerName);
		customer.setPhone(phone);
		customer.setStateAbbr(customerState);
		customer.setZipCode(customerZip);

		return customer;
	}

	@Override
	public List<CustomerObject> GetProfiles(List<String> customerProfileRefNumbers) throws Exception {
		logger.info("Will try to Get Profiles for " + customerProfileRefNumbers.size() + " CustomerProfileRefNumbers ["
				+ customerProfileRefNumbers + "]");
		final List<CustomerObject> customers = new ArrayList<CustomerObject>();
		for (final String customerProfileRefNum : customerProfileRefNumbers) {
			final CustomerObject customer = getProfile(customerProfileRefNum);
			customers.add(customer);
		}
		logger.info("For of " + customerProfileRefNumbers.size() + " CustomerProfileRefNumbers, found "
				+ customers.size() + " Customers: [" + customers + "]");
		return customers;
	}

	@Override
	public OrbitalResponse CopyProfile(SemplestString customerProfileRefNumber) throws Exception {
		logger.info("Will try to Copy profile for CustomerProfileRefNum [" + customerProfileRefNumber + "]");
		final String customerProfileRefNum = customerProfileRefNumber.getSemplestString();
		final List<String> customerProfileNums = new ArrayList<String>();
		customerProfileNums.add(customerProfileRefNum);
		final List<CustomerObject> customers = GetProfiles(customerProfileNums);
		if (customers.isEmpty()) {
			final String errMsg = "Could not copy profile for CustomerProfileRefNum [" + customerProfileRefNumber
					+ "] because could not find that profile in Chase Orbital Gateway";
			logger.info(errMsg);
			throw new Exception(errMsg);
		}

		final CustomerObject customerToCopy = customers.get(0);
		final OrbitalResponse response = CreateProfile(customerToCopy);
		return response;
	}

	public String checkStatus(String json) throws Exception {
		return checkStatus(null, null);
	}

	@Override
	public String checkStatus(String input1, String input2) throws Exception {
		return "";
	}
}
